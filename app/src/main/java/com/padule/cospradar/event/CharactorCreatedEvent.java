package com.padule.cospradar.event;

import com.padule.cospradar.data.Charactor;

public class CharactorCreatedEvent {

    public Charactor charactor;

    public CharactorCreatedEvent(Charactor charactor) {
        this.charactor = charactor;
    }

}
