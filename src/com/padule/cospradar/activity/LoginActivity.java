package com.padule.cospradar.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import butterknife.OnClick;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.User;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.TwitterUtils;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getName();

    private String callbackUrl;
    private Twitter twitter;
    private RequestToken requestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackUrl = getString(R.string.twitter_callback_url);
        twitter = TwitterUtils.getTwitterInstance(this);

        checkLoginStatus();

        this.setTheme(R.style.NoActionBar);
        setContentView(R.layout.activity_login);
    }

    private void checkLoginStatus() {
        if (TwitterUtils.hasAccessToken() && AppUtils.isLoggedIn()) {
            startMainActivity();
        }
    }

    private void saveUser() {
        AsyncTask<Void, Void, twitter4j.User> task = new AsyncTask<Void, Void, twitter4j.User>() {
            @Override
            protected twitter4j.User doInBackground(Void... params) {
                try {
                    String screenName = twitter.getScreenName();
                    return twitter.showUser(screenName);
                } catch (TwitterException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(twitter4j.User twitterUser) {
                String screenName = twitterUser.getScreenName();
                String imgUrl = twitterUser.getProfileImageURL();
                saveUser(screenName, imgUrl);
            }
        };
        task.execute();
    }

    private void saveUser(String screenName, String imgUrl) {
        if (screenName == null || imgUrl == null) {
            AppUtils.showToast(getString(R.string.login_failed), this);
            return;
        }

        String url = AppUrls.getUsersCreate();
        Dialog dialog = AppUtils.makeSendingDialog(this);
        final Map<String, Object> params = createParams(screenName, imgUrl);
        Log.d(TAG, "create_params: " + params.toString());

        aq.progress(dialog).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d(TAG, "create_url: " + url);
                saveUserCallBack(json, status);
            }
        });
    }

    private void saveUserCallBack(JSONObject json, AjaxStatus status) {
        if (json != null) {
            Log.d(TAG, json.toString());
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            User user = gson.fromJson(json.toString(), User.class);
            AppUtils.setUser(user);
            AppUtils.showToast(getString(R.string.login_succeeded), this);
            startMainActivity();
        } else {
            if (AppUtils.isMockMode()) {
                AppUtils.setUser(MockFactory.getUser1());
                AppUtils.showToast(getString(R.string.login_succeeded), this);
            } else {
                AppUtils.showToast(getString(R.string.login_failed), this);
                Log.e(TAG, "create_error_message: " + status.getMessage());
            }
        }
    }

    private Map<String, Object> createParams(String screenName, String imgUrl) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AppUrls.PARAM_NAME, screenName);
        params.put(AppUrls.PARAM_IMAGE, imgUrl);
        return params;
    }

    @Override
    protected void initView() {
        //
    }

    @OnClick(R.id.btn_login_twitter)
    public void onClickBtnLoginTwitter() {
        if (TwitterUtils.hasAccessToken()) {
            if (AppUtils.isLoggedIn()) {
                startMainActivity();
            } else {
                saveUser();
            }
        } else {
            authTwitter();
        }
    }

    private void authTwitter() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    requestToken = twitter.getOAuthRequestToken(callbackUrl);
                    return requestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    Log.e(TAG, "failed authorization url.");
                }
            }
        };
        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null || intent.getData() == null
                || !intent.getData().toString().startsWith(callbackUrl)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return twitter.getOAuthAccessToken(requestToken, params[0]);
                } catch (TwitterException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    AppUtils.showToast(LoginActivity.this.getString(R.string.login_succeeded), LoginActivity.this);
                    onSuccessOAuth(accessToken);
                } else {
                    AppUtils.showToast(LoginActivity.this.getString(R.string.login_failed), LoginActivity.this);
                }
            }
        };
        task.execute(verifier);
    }

    private void onSuccessOAuth(AccessToken accessToken) {
        TwitterUtils.storeAccessToken(accessToken);
        if (AppUtils.isLoggedIn()) {
            startMainActivity();
        } else {
            saveUser();
        }
    }

    private void startMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
