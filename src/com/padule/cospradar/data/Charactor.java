package com.padule.cospradar.data;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;

public class Charactor extends Data {

    private static final long serialVersionUID = 1L;

    @SerializedName("id") int id;
    @SerializedName("user_id") int userId;
    @SerializedName("name") String name;
    @SerializedName("title") String title;
    @SerializedName("image") String image;

    @SerializedName("user") User user;

    public Charactor(int id, int userId, String name, String title, String image) {
        super();
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.title = title;
        this.image = image;
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

}
