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
@FunctionalInterface
public interface MSMGuard<StateT extends Enum, EventT extends Enum, DataT> {

    boolean check(StateT currentState, EventT event, StateT newState, DataT data);
}
