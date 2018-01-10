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

//struct transition_table : mpl::vector<
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 *
 * @author Nicholas Folse
 * @param <StateT> State-type (Enum)
 * @param <EventT> Event-type (Enum)
 * @param <DataT> Data-type (Object)
 */
public final class MetaStateMachineBuilder<StateT extends Enum, EventT extends Enum, DataT> {

    private final List<MSMDef<StateT, EventT, DataT>> defs = new ArrayList<>();

    private MetaStateMachineBuilder() {
    }

    /**
     * Begin defining a state machine.
     * @param <StateT>
     * @param <EventT>
     * @param <DataT>
     * @param fromState
     * @param event
     * @param toState
     * @param action
     * @param guard
     * @return 
     */
    public static <StateT extends Enum, EventT extends Enum, DataT> MetaStateMachineBuilder<StateT, EventT, DataT> begin(StateT fromState, EventT event, StateT toState, MSMAction<StateT, EventT, DataT> action, MSMGuard<StateT, EventT, DataT> guard) {
        MetaStateMachineBuilder<StateT, EventT, DataT> msm = new MetaStateMachineBuilder<>();
        msm.defs.add(new MSMDef<>(fromState, event, toState, action, guard));
        return msm;
    }

    /**
     * Continue defining a state machine.
     * @param fromState
     * @param event
     * @param toState
     * @param action
     * @param guard
     * @return 
     */
    public MetaStateMachineBuilder<StateT, EventT, DataT> define(StateT fromState, EventT event, StateT toState, MSMAction<StateT, EventT, DataT> action, MSMGuard<StateT, EventT, DataT> guard) {
        this.defs.add(new MSMDef<>(fromState, event, toState, action, guard));
        return this;
    }

    /**
     * Build a state machine
     * @return 
     */
    public MetaStateMachine build() {
        return this.build(() -> new MetaStateMachine<>());
    }

    public MetaStateMachine build(Supplier<? extends MetaStateMachine<StateT, EventT, DataT>> msmSupplier) {
        MetaStateMachine<StateT, EventT, DataT> msm = msmSupplier.get();
        msm.redefine(this.defs);
        return msm;
    }

}
