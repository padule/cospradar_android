package com.padule.cospradar.service;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import com.padule.cospradar.Constants;
import com.padule.cospradar.data.CharactorComment;

public interface APIService {

    static final String API_URL = Constants.APP_URL;

    static final String PATH_CHARACTOR_COMMENTS = "/charactor_comments";

    static final String PARAM_PAGE = "page";
    static final String PARAM_CHARACTOR_ID = "charactor_id";

    static final String EXT_JSON = ".json";

    @GET(PATH_CHARACTOR_COMMENTS + EXT_JSON)
    void getCharactorComments(@Query(PARAM_CHARACTOR_ID) int charactorId, @Query(PARAM_PAGE) int page, 
            Callback<List<CharactorComment>> cb);

}
