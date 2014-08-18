package com.padule.cospradar.data;

import com.google.gson.annotations.SerializedName;
import com.padule.cospradar.base.Data;

public class CharactorLocation extends Data {

    private static final long serialVersionUID = 1L;

    @SerializedName("id") int id;
    @SerializedName("charactor_id") int charactorId;
    @SerializedName("latitude") float latitude;
    @SerializedName("longitude") float longitude;

    @SerializedName("charactor") Charactor charactor;

    public CharactorLocation(int id, int charactorId, float latitude,
            float longitude) {
        super();
        this.id = id;
        this.charactorId = charactorId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public CharactorLocation(int id, Charactor charactor, float latitude,
            float longitude) {
        this(id, charactor.id, latitude, longitude);
        this.charactor = charactor;
    }

    public int getId() {
        return id;
    }

    public int getCharactorId() {
        return charactorId;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public Charactor getCharactor() {
        return charactor;
    }

}
