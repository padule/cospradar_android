package com.padule.cospradar.data;

import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;
import com.padule.cospradar.util.TimeUtils;

public class Charactor extends Data {

    private static final long serialVersionUID = 1L;
    private static final int LIMIT_DAY = 1;

    @SerializedName("id") int id;
    @SerializedName("user_id") int userId;
    @SerializedName("name") String name;
    @SerializedName("title") String title;
    @SerializedName("image") String image;
    @SerializedName("updated_at") Date updatedAt;

    @SerializedName("user") User user;

    public Charactor() {
        super();
    }

    public Charactor(int id, int userId, String name, String title, String image) {
        super();
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.title = title;
        this.image = image;
        this.updatedAt = new Date();
    }

    public boolean isEnabled() {
        int diff = TimeUtils.getDiffDays(new Date(), updatedAt);
        return diff >= LIMIT_DAY;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

}
