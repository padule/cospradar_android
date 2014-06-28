package com.padule.cospradar.service;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;

import com.padule.cospradar.Constants;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;

public interface APIService {

    static final String API_URL = Constants.APP_URL;

    static final String PATH_CHARACTORS = "/charactors";
    static final String PATH_CHARACTOR_COMMENTS = "/charactor_comments";

    static final String PARAM_PAGE = "page";
    static final String PARAM_TITLE = "title";
    static final String PARAM_LIMIT = "limit";
    static final String PARAM_DESC = "desc";
    static final String PARAM_CHARACTOR_ID = "charactor_id";
    static final String PARAM_USER_ID = "user_id";
    static final String PARAM_LATITUDE = "latitude";
    static final String PARAM_LONGITUDE = "longitude";

    static final String EXT_JSON = ".json";

    @GET(PATH_CHARACTOR_COMMENTS + EXT_JSON)
    void getCharactorComments(@Query(PARAM_CHARACTOR_ID) int charactorId, @Query(PARAM_PAGE) int page, 
            Callback<List<CharactorComment>> cb);

    @POST(PATH_CHARACTOR_COMMENTS + EXT_JSON)
    void postCharactorComments(@Body CharactorComment comment, 
            Callback<CharactorComment> cb);

    @GET(PATH_CHARACTORS + EXT_JSON)
    void getCharactors(@QueryMap Map<String, String> params, 
            Callback<List<Charactor>> cb);

}
