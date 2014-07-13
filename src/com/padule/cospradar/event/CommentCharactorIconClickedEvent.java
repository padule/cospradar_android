package com.padule.cospradar.event;

import com.padule.cospradar.data.Charactor;

public class CommentCharactorIconClickedEvent {

    public Charactor charactor;

    public CommentCharactorIconClickedEvent(Charactor charactor) {
        this.charactor = charactor;
    }

}
