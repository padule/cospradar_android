package com.padule.cospradar.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;

public class User extends Data {

    private static final long serialVersionUID = 1L;
    private static final String AT = "@";

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
    
    public String getScreenName() {
        return AT + name;
    }

    public String getImage() {
        return image;
    }

    public List<Charactor> getCharactors() {
        if (charactors == null) {
            charactors = new ArrayList<Charactor>();
        }
        return charactors;
    }

    public Charactor getCurrentCharactor() {
        // TODO implements after adding is_enabled column to charactors.
        if (charactors != null && !charactors.isEmpty()) {
            return charactors.get(0);
        } else {
            return null;
        }
    }

    public void addCharactor(Charactor charactor) {
        if (charactor == null) {
            return;
        }

        for (Charactor c : getCharactors()) {
            if (c.getId() == charactor.getId()) {
                return;
            }
        }
        getCharactors().add(charactor);

    }

}
