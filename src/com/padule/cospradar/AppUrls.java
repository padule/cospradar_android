package com.padule.cospradar;

public class AppUrls {

    private static final String API_URL = Constants.APP_URL;

    private static final String PATH_CHARACTORS = "/charactors";
    private static final String PATH_CHARACTOR_COMMENTS = "/charactor_comments";
    private static final String PATH_CHARACTOR_LOCATIONS = "/charactor_locations";
    private static final String PATH_USERS = "/users";

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_LIMIT = "limit";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_IMAGE = "image";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_COMMENT_CHARACTOR_ID = "comment_charactor_id";
    public static final String PARAM_CHARACTOR_ID = "charactor_id";
    public static final String PARAM_USER_ID = "user_id";
    public static final String PARAM_LATITUDE = "latitude";
    public static final String PARAM_LONGITUDE = "longitude";

    private static final String EXT_JSON = ".json";
    private static final String Q = "?";
    private static final String AND = "&";
    private static final String EQ = "=";
    private static final String SLASH = "/";

    public static String getCharactorsIndex(int page, String title) {
        return getCharactorsIndex(page, 0, title);
    }

    public static String getCharactorsIndex(int page, int userId) {
        return getCharactorsIndex(page, userId, null);
    }

    private static String getCharactorsIndex(int page, int userId, String title) {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTORS);
        sb.append(EXT_JSON);
        if (page > 0) {
            sb.append(Q);
            sb.append(createParam(PARAM_PAGE, page));
        } else {
            // TODO
            sb.append(Q);
            sb.append(createParam(PARAM_LIMIT, 300));
        }
        if (userId > 0) {
            sb.append(AND);
            sb.append(createParam(PARAM_USER_ID, userId));
        }
        if (title != null && !"".equals(title)) {
            sb.append(AND);
            sb.append(createParam(PARAM_TITLE, title));
        }
        return sb.toString();
    }

    public static String getCharactorsCreate() {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTORS);
        sb.append(EXT_JSON);
        return sb.toString();
    }

    public static String getCharactorLocationsCreate() {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTOR_LOCATIONS);
        sb.append(EXT_JSON);
        return sb.toString();
    }

    public static String getCharactorLocationsUpdate(int charactorLocationId) {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTOR_LOCATIONS);
        sb.append(SLASH);
        sb.append(charactorLocationId);
        sb.append(EXT_JSON);
        return sb.toString();
    }

    public static String getUsersCreate() {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_USERS);
        sb.append(EXT_JSON);
        return sb.toString();
    }

    public static String getCharactorCommentsIndex(int charactorId, int page) {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTOR_COMMENTS);
        sb.append(EXT_JSON);
        sb.append(Q);
        sb.append(createParam(PARAM_CHARACTOR_ID, charactorId));
        sb.append(AND);
        sb.append(createParam(PARAM_PAGE, page));
        return sb.toString();
    }

    public static String getCharactorCommentsCreate() {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTOR_COMMENTS);
        sb.append(EXT_JSON);
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
