package com.padule.cospradar;


public class AppUrls {

    private static final String API_URL = Constants.APP_URL;

    private static final String PATH_USERS = "/users";

    public static final String PARAM_NAME = "name";
    public static final String PARAM_IMAGE = "image";

    private static final String EXT_JSON = ".json";

    public static String getUsersCreate() {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_USERS);
        sb.append(EXT_JSON);
        return sb.toString();
    }

}
