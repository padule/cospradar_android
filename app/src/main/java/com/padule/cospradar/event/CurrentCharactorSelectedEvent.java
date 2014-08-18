package com.padule.cospradar.event;

import com.padule.cospradar.data.Charactor;

public class CurrentCharactorSelectedEvent {

    public final Charactor charactor;

    public CurrentCharactorSelectedEvent(Charactor charactor) {
        this.charactor = charactor;
    }
}
