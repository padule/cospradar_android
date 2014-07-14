package com.padule.cospradar.event;

import java.util.ArrayList;

import com.padule.cospradar.data.Charactor;

public class RadarCharactorClickedEvent {

    public final ArrayList<Charactor> charactors;

    public RadarCharactorClickedEvent(ArrayList<Charactor> charactors) {
        this.charactors = charactors;
    }

}
