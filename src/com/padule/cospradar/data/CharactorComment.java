package com.padule.cospradar.data;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;

public class CharactorComment extends Data {

    private static final long serialVersionUID = 1L;

    @SerializedName("id") int id;
    @SerializedName("charactor_id") int charactorId;
    @SerializedName("comment_charactor_id") int commentCharactorId;
    @SerializedName("text") String text;

    @SerializedName("charactor") Charactor charactor;
    @SerializedName("comment_charactor") Charactor commentCharactor;

    public CharactorComment(int id, int charactorId, int commentCharactorId,
            String text, Charactor charactor, Charactor commentCharactor) {
        super();
        this.id = id;
        this.charactorId = charactorId;
        this.commentCharactorId = commentCharactorId;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public int getCharactorId() {
        return charactorId;
    }

    public int getCommentCharactorId() {
        return commentCharactorId;
    }

    public String getText() {
        return text;
    }

    public Charactor getCharactor() {
        return charactor;
    }

    public Charactor getCommentCharactor() {
        return commentCharactor;
    }

}
