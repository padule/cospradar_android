package com.padule.cospradar.event;

import com.padule.cospradar.data.Charactor;

public class CommentCharactorSelectedEvent {

    public final Charactor charactor;

    public CommentCharactorSelectedEvent(Charactor charactor) {
        this.charactor = charactor;
    }
}
