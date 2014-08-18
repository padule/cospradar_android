package com.padule.cospradar.data;

import com.google.gson.annotations.SerializedName;

public class Device {

    public static final int PLATFORM_ANDROID = 1;

    @SerializedName("id") int id;
    @SerializedName("userid") int userId;
    @SerializedName("platform") int platform;
    @SerializedName("token") String token;
    @SerializedName("user") User user;

    public String getToken() {
        return token;
    }

}
