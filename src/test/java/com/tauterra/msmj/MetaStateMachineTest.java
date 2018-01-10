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

import static com.tauterra.msmj.PlayerEvent.*;
import static com.tauterra.msmj.PlayerState.*;
import org.junit.BeforeClass;
import org.junit.Test;

enum PlayerState {
    Stopped, Open, Empty, Playing, Paused;
}

enum PlayerEvent {
    Play, OpenClose, Stop, Pause, EndPause, CDDetected;
}

class PlayerData {

    private String cdID = "";

    public PlayerData() {
    }

    static PlayerEvent startPlayback(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return null;
    }

    static PlayerEvent openDrawer(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return null;
    }

    static PlayerEvent closeDrawer(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        if (!data.getCdID().isEmpty()) {
            return CDDetected;
        }
        return null;
    }

    static PlayerEvent storeCDInfo(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return null;
    }

    static PlayerEvent stopPlayback(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return null;
    }

    static PlayerEvent pausePlayback(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return null;
    }

    static PlayerEvent stopAndOpen(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return null;
    }

    static PlayerEvent resumePlayback(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return null;
    }

    static boolean goodDiskFormat(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return true;
    }

    static boolean autoStart(PlayerState fromState, PlayerEvent event, PlayerState toState, PlayerData data) {
        return true;
    }

    public String getCdID() {
        return cdID;
    }

    public void setCdID(String cdID) {
        this.cdID = cdID;
    }
}

/**
 *
 * @author Nicholas Folse
 */
public class MetaStateMachineTest {

    private static MetaStateMachine<PlayerState, PlayerEvent, PlayerData> STATE_MACHINE;

    public MetaStateMachineTest() {
    }

    @BeforeClass
    public static void initStateMachine() {
        STATE_MACHINE = MetaStateMachineBuilder
                .begin(Stopped, Play, Playing, PlayerData::startPlayback, null)
                .define(Stopped, OpenClose, Open, PlayerData::openDrawer, null)
                .define(Stopped, Stop, Stopped, null, null)
                .define(Open, OpenClose, Empty, PlayerData::closeDrawer, null)
                .define(Empty, OpenClose, Open, PlayerData::openDrawer, null)
                .define(Empty, CDDetected, Stopped, PlayerData::storeCDInfo, PlayerData::goodDiskFormat)
                .define(Empty, CDDetected, Playing, PlayerData::storeCDInfo, PlayerData::autoStart)
                .define(Playing, Stop, Stopped, PlayerData::stopPlayback, null)
                .define(Playing, Pause, Paused, PlayerData::pausePlayback, null)
                .define(Playing, OpenClose, Open, PlayerData::stopAndOpen, null)
                .define(Paused, EndPause, Playing, PlayerData::resumePlayback, null)
                .define(Paused, Pause, Playing, PlayerData::resumePlayback, null)
                .define(Paused, Play, Playing, PlayerData::resumePlayback, null)
                .define(Paused, Stop, Stopped, PlayerData::stopPlayback, null)
                .define(Paused, OpenClose, Open, PlayerData::openDrawer, null)
                .build();
        STATE_MACHINE.setUnhandedEventHandler((s, e) -> {
            System.err.println("Unhandled state transition: state: " + s.toString() + ", event: " + e.toString());
        });
        STATE_MACHINE.addTransitionHandler((e) -> {
            System.out.println(e);
        });
    }

    @Test
    public void test() {
        PlayerData cd = new PlayerData();
        cd.setCdID("MyCD");
        final MetaStateMachine<PlayerState, PlayerEvent, PlayerData> msm = STATE_MACHINE;
        msm.reset(Empty);
        msm.setData(cd);

        msm.accept(Play);
        msm.accept(OpenClose);
        msm.accept(OpenClose);
        msm.accept(Play);
        msm.accept(Pause);
        msm.accept(EndPause);
        msm.accept(Stop);
        msm.accept(OpenClose);

        cd.setCdID("");
        msm.accept(OpenClose);
    }

}
