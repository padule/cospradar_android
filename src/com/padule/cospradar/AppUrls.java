package com.padule.cospradar;


public class AppUrls {

    private static final String API_URL = Constants.APP_URL;

    private static final String PATH_CHARACTORS = "/charactors";
    private static final String PATH_CHARACTOR_COMMENTS = "/charactor_comments";
    private static final String PATH_USERS = "/users";
    private static final String PATH_COMMENT_LIST = "/comment_list";

    private static final String PARAM_PAGE = "page";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_IMAGE = "image";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_COMMENT_CHARACTOR_ID = "comment_charactor_id";
    public static final String PARAM_USER_ID = "user_id";

    private static final String EXT_JSON = ".json";
    private static final String Q = "?";
    private static final String AND = "&";
    private static final String EQ = "=";

    public static String getCharactorsCreate() {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTORS);
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

    public static String getCharactorCommentsCommentList(int commentCharactorId, int page) {
        StringBuffer sb = new StringBuffer();
        sb.append(API_URL);
        sb.append(PATH_CHARACTOR_COMMENTS);
        sb.append(PATH_COMMENT_LIST);
        sb.append(EXT_JSON);
        sb.append(Q);
        sb.append(createParam(PARAM_COMMENT_CHARACTOR_ID, commentCharactorId));
        sb.append(AND);
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
