package com.padule.cospradar.service;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import com.padule.cospradar.Constants;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.data.Device;
import com.padule.cospradar.data.Result;
import com.padule.cospradar.data.User;

public interface ApiService {

    static final String API_URL = Constants.APP_URL;

    static final String PATH_USERS = "/users";
    static final String PATH_DEVICES = "/devices";
    static final String PATH_CHARACTORS = "/charactors";
    static final String PATH_CHARACTOR_COMMENTS = "/charactor_comments";
    static final String PATH_CHARACTOR_LOCATIONS = "/charactor_locations";
    static final String PATH_COMMENT_LIST = "/comment_list";
    static final String PATH_ID = "/{id}";

    static final String PARAM_PAGE = "page";
    static final String PARAM_NAME = "name";
    static final String PARAM_TITLE = "title";
    static final String PARAM_TEXT = "text";
    static final String PARAM_IMAGE = "image";
    static final String PARAM_LIMIT = "limit";
    static final String PARAM_DESC = "desc";
    static final String PARAM_CHARACTOR_ID = "charactor_id";
    static final String PARAM_COMMENT_CHARACTOR_ID = "comment_charactor_id";
    static final String PARAM_USER_ID = "user_id";
    static final String PARAM_LATITUDE = "latitude";
    static final String PARAM_LONGITUDE = "longitude";
    static final String PARAM_IS_ENABLED = "is_enabled";
    static final String PARAM_PLATFORM = "platform";
    static final String PARAM_TOKEN = "token";

    static final String EXT_JSON = ".json";

    @GET(PATH_CHARACTOR_COMMENTS + EXT_JSON)
    void getCharactorComments(@Query(PARAM_CHARACTOR_ID) int charactorId, @Query(PARAM_PAGE) int page, 
            Callback<List<CharactorComment>> cb);

    @POST(PATH_CHARACTOR_COMMENTS + EXT_JSON)
    void postCharactorComments(@Body CharactorComment comment, 
            Callback<CharactorComment> cb);

    @GET(PATH_CHARACTOR_COMMENTS + PATH_COMMENT_LIST + EXT_JSON)
    void getCharactorCommentsCommentList(@Query(PARAM_COMMENT_CHARACTOR_ID) int commentCharactorId,
            @Query(PARAM_PAGE) int page,
            Callback<List<Charactor>> cb);

    @GET(PATH_CHARACTORS + EXT_JSON)
    void getCharactors(@QueryMap Map<String, String> params, 
            Callback<List<Charactor>> cb);

    @GET(PATH_CHARACTORS + PATH_ID + EXT_JSON)
    void getCharactor(@Path("id") int charactorId, Callback<Charactor> cb);

    @Multipart
    @POST(PATH_CHARACTORS + EXT_JSON)
    void postCharactors(@Part(PARAM_NAME) TypedString name, @Part(PARAM_TITLE) TypedString title, 
            @Part(PARAM_USER_ID) TypedString userId, @Part(PARAM_IMAGE) TypedFile image,
            Callback<Charactor> cb);

    @Multipart
    @POST(PATH_CHARACTORS + PATH_ID + EXT_JSON)
    void putCharactors(@Path("id") int charactorId, @Part(PARAM_NAME) TypedString name, 
            @Part(PARAM_TITLE) TypedString title, @Part(PARAM_USER_ID) TypedString userId, 
            @Part(PARAM_IMAGE) TypedFile image,
            Callback<Charactor> cb);

    @DELETE(PATH_CHARACTORS + PATH_ID + EXT_JSON)
    void deleteCharactors(@Path("id") int charactorId, Callback<Result> cb);

    @FormUrlEncoded
    @POST(PATH_CHARACTOR_LOCATIONS + EXT_JSON)
    void postCharactorLocations(@Field(PARAM_CHARACTOR_ID) int charactorId, 
            @Field(PARAM_LATITUDE) double lat, @Field(PARAM_LONGITUDE) double lng, 
            Callback<CharactorLocation> cb);

    @FormUrlEncoded
    @POST(PATH_USERS + EXT_JSON)
    void postUsers(@Field(PARAM_NAME) String name, @Field(PARAM_IMAGE) String imgUrl, 
            Callback<User> cb);

    @FormUrlEncoded
    @POST(PATH_DEVICES + EXT_JSON)
    void postDevices(@Field(PARAM_USER_ID) int userId, 
            @Field(PARAM_PLATFORM) int platform, @Field(PARAM_TOKEN) String token,
            Callback<Device> cb);

}
