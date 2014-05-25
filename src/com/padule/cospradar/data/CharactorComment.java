package com.padule.cospradar.data;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;
import com.padule.cospradar.util.AppUtils;

public class CharactorComment extends Data {

    private static final long serialVersionUID = 1L;

    @SerializedName("id") int id;
    @SerializedName("charactor_id") int charactorId;
    @SerializedName("comment_charactor_id") int commentCharactorId;
    @SerializedName("text") String text;
    @SerializedName("created") Date createdAt;
    @SerializedName("modified") Date updatedAt;

    @SerializedName("charactor") Charactor charactor;
    @SerializedName("comment_charactor") Charactor commentCharactor;

    public CharactorComment(int id, String text, 
            Charactor charactor, Charactor commentCharactor) {
        super();
        this.id = id;
        this.charactorId = charactor.id;
        this.commentCharactorId = commentCharactor.id;
        this.charactor = charactor;
        this.commentCharactor = commentCharactor;
        this.text = text;
        this.createdAt = new Date();
    }

    public CharactorComment(int charactorId, String text) {
        super();
        this.charactorId = charactorId;
        this.text = text;
        this.commentCharactor = AppUtils.getCharactor();
        this.text = text;
        this.createdAt = new Date();
    }

    public static CharactorComment createTmp(int charactorId, String text) {
        return new CharactorComment(charactorId, text);
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public boolean isCurrentCharactor() {
        Charactor charactor = AppUtils.getCharactor();
        return charactor != null && charactor.getId() == commentCharactorId;
    }

}
