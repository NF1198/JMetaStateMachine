/*
 * Copyright 2018 tauTerra, LLC Nicholas Folse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tauterra.msmj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author Nicholas Folse
 * @param <StateT>
 * @param <EventT>
 * @param <DataT>
 */
public class MetaStateMachine<StateT extends Enum, EventT extends Enum, DataT> {

    // data
    private DataT data;

    // state 
    private StateT state;

    // state transition table
    private final Map<Long, Set<MSMDef<StateT, EventT, DataT>>> stt = new HashMap<>();

    private BiConsumer<StateT, EventT> unhandledEventHandler = null;

    private List<Consumer<StateTransitionEvent<StateT, EventT, DataT>>> stateTransitionEventHandlers;

    protected MetaStateMachine() {
    }

    void redefine(List<MSMDef<StateT, EventT, DataT>> defs) {
        stt.clear();
        defs.forEach(d -> {
            final Long key = computeStateTransitionKey(d.getFromState(), d.getEvent());
            if (!stt.containsKey(key)) {
                stt.put(key, new LinkedHashSet<>());
            }
            stt.get(key).add(d);
        });
    }

    public void reset(StateT state) {
        this.state = state;
    }

    public void setUnhandedEventHandler(BiConsumer<StateT, EventT> handler) {
        this.unhandledEventHandler = handler;
    }

    public Consumer<StateTransitionEvent<StateT, EventT, DataT>> addTransitionHandler(Consumer<StateTransitionEvent<StateT, EventT, DataT>> handler) {
        if (this.stateTransitionEventHandlers == null) {
            this.stateTransitionEventHandlers = new ArrayList<>();
        }
        this.stateTransitionEventHandlers.add(handler);
        return handler;
    }

    public void removeTransitionHandler(Consumer<StateTransitionEvent<StateT, EventT, DataT>> handler) {
        if (handler == null) {
            return;
        }
        if (this.stateTransitionEventHandlers == null) {
            return;
        }
        this.stateTransitionEventHandlers.remove(handler);
    }

    public void setData(DataT value) {
        this.data = value;
    }

    public DataT getData() {
        return this.data;
    }

    public StateT getState() {
        return this.state;
    }

    public StateT accept(EventT event) {
        while (event != null) {
            final StateT oldState = this.state;
            final EventT transitionEvent = event;
            final Long key = computeStateTransitionKey(this.state, event);
            final Set<MSMDef<StateT, EventT, DataT>> defs = this.stt.get(key);
            if (defs == null) {
                if (this.unhandledEventHandler != null) {
                    this.unhandledEventHandler.accept(this.state, event);
                }
                return this.state;
            }
            for (MSMDef<StateT, EventT, DataT> def : defs) {
                if (def.getGuard() != null) {
                    boolean guardResult = def.getGuard().check(this.state, event, def.getToState(), this.data);
                    if (!guardResult) {
                        // only continue if guard doesn't match
                        continue;
                    }
                }
                try {
                    if (def.getAction() != null) {
                        event = def.getAction().apply(this.state, event, def.getToState(), this.data);
                    }
                } catch (Exception e) {
                    throw (e);
                } finally {
                    final StateT newState = def.getToState();
                    this.state = def.getToState();
                    if (!oldState.equals(newState) && this.stateTransitionEventHandlers != null) {
                        final StateTransitionEvent<StateT, EventT, DataT> ste = new StateTransitionEvent<>(oldState, transitionEvent, newState, this.data);
                        this.stateTransitionEventHandlers.forEach(h -> h.accept(ste));
                    }
                }
                // break loop if guard passed
                break;
            }
        }
        return this.state;
    }

    private Long computeStateTransitionKey(StateT state, EventT event) {
        return (long) ((long) state.ordinal() << 32L) + (long) event.ordinal();
    }

}
