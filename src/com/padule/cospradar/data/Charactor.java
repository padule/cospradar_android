package com.padule.cospradar.data;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.TimeUtils;

public class Charactor extends Data {

    private static final long serialVersionUID = 1L;
    private static final int LIMIT_DAY = 1;

    @SerializedName("id") int id;
    @SerializedName("user_id") int userId;
    @SerializedName("name") String name;
    @SerializedName("title") String title;
    @SerializedName("image") String image;
    @SerializedName("modified") Date updatedAt;

    @SerializedName("user") User user;
    @SerializedName("charactor_location") CharactorLocation charactorLocation;

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
        int diff = TimeUtils.getDiffDays(new Date(), updatedAt);
        return diff < LIMIT_DAY;
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
        return user;
    }

}
