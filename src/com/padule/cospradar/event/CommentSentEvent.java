package com.padule.cospradar.event;

import com.padule.cospradar.data.Charactor;

public class CommentSentEvent {

    public final Charactor charactor;

    public CommentSentEvent(Charactor charactor) {
        this.charactor = charactor;
    }
}
