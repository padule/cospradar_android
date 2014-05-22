package com.padule.cospradar;

public class AppUrls {

    private static final String API_URL = Constants.APP_URL;

    private static final String PATH_CHARACTORS = "/charactors";

    private static final String PARAM_PAGE = "page";

    private static final String EXT_JSON = ".json";
    private static final String Q = "?";
    private static final String AND = "&";
    private static final String EQ = "=";

    public static String getCharactorsIndex(int page) {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTORS);
        sb.append(EXT_JSON);
        sb.append(Q);
        sb.append(createParam(PARAM_PAGE, page));
        return sb.toString();
    }

    private static String createParam(String key, int value) {
        return createParam(key, value + "");
    }

    private static String createParam(String key, String value) {
        if (value == null || "".equals(value) || "null".equals(value)) {
            return "";
        } else {
            return key + EQ + value;
        }
    }

}
