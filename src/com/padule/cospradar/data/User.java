package com.padule.cospradar.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;

public class User extends Data {

    private static final long serialVersionUID = 1L;

    @SerializedName("id") int id;
    @SerializedName("name") String name;
    @SerializedName("image") String image;

    @SerializedName("charactors") List<Charactor> charactors;

    public User(int id, String name, String image) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public List<Charactor> getCharactors() {
        return charactors;
    }

}
