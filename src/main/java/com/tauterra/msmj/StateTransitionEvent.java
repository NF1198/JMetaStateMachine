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

/**
 *
 * @author Nicholas Folse
 */
public class StateTransitionEvent<StateT extends Enum, EventT extends Enum, DataT> {

    private final StateT fromState;
    private final EventT event;
    private final StateT toState;
    private final DataT data;

    public StateTransitionEvent(StateT fromState, EventT event, StateT toState, DataT data) {
        this.fromState = fromState;
        this.event = event;
        this.toState = toState;
        this.data = data;
    }

    public StateT getFromState() {
        return fromState;
    }

    public EventT getEvent() {
        return event;
    }

    public StateT getToState() {
        return toState;
    }

    public DataT getData() {
        return data;
    }

    @Override
    public String toString() {
        return "StateTransitionEvent{" + "fromState=" + fromState + ", event=" + event + ", toState=" + toState + ", data=" + data + '}';
    }
    
    

}
