package com.padule.cospradar.data;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;
import com.padule.cospradar.util.ImageUtils;

public class Charactor extends Data {

    private static final long serialVersionUID = 1L;

    @SerializedName("id") int id;
    @SerializedName("user_id") int userId;
    @SerializedName("name") String name;
    @SerializedName("title") String title;
    @SerializedName("image") String image;
    @SerializedName("modified") Date updatedAt;
    @SerializedName("is_enabled") boolean isEnabled;

    @SerializedName("user") User user;
    @SerializedName("charactor_location") CharactorLocation charactorLocation;
    @SerializedName("latest_comment") CharactorComment latestComment;

    public Charactor() {
        super();
        this.name = "";
        this.title = "";
    }

    public Charactor(int id, User user, String name, String title, String image) {
        super();
        this.id = id;
        this.user = user;
        this.userId = user.getId();
        this.name = name;
        this.title = title;
        this.image = image;
        this.updatedAt = new Date();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getImageUrl() {
        return ImageUtils.convertToValidUrl(image);
    }

    public void setImage(String url) {
        this.image = url;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getNameAndTitle() {
        return name + " (" + title + ")";
    }

    public void setLocation(CharactorLocation charactorLocation) {
        this.charactorLocation = charactorLocation;
    }

    public CharactorLocation getLocation() {
        return charactorLocation;
    }

    public User getUser() {
        if (user.getCharactors().isEmpty()) {
            user.addCharactor(this);
        }
        return user;
    }

    public CharactorComment getLatestComment() {
        return latestComment;
    }

    public void setLatestComment(CharactorComment comment) {
        latestComment = comment;
    }

}
