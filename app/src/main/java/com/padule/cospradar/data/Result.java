package com.padule.cospradar.data;

import com.google.gson.annotations.SerializedName;

public class Result {

    private static String SUCCESS = "success";

    @SerializedName("message") String message;

    public boolean success() {
        return SUCCESS.equals(message);
    }

}
