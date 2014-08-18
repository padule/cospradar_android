package com.padule.cospradar.event;

import com.padule.cospradar.data.Charactor;

public class CharactorDeleteEvent {

    public Charactor charactor;

    public CharactorDeleteEvent(Charactor charactor) {
        this.charactor = charactor;
    }

}
