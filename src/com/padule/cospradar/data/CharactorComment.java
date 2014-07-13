package com.padule.cospradar.data;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;

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

    public CharactorComment(int charactorId, String text, Charactor commentCharactor) {
        super();
        this.charactorId = charactorId;
        this.text = text;
        this.commentCharactor = commentCharactor;
        this.commentCharactorId = commentCharactor.id;
        this.createdAt = new Date();
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

    public boolean isUserCharactor(List<Charactor> charactors) {
        if (charactors == null) {
            return false;
        }

        for (Charactor charactor : charactors) {
            if (charactor.getId() == commentCharactorId) {
                return true;
            }
        }
        return false;
    }

}
