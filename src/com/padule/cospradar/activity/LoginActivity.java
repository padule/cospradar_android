package com.padule.cospradar.activity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
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
import android.view.View;
import android.widget.Button;
import butterknife.InjectView;
import butterknife.OnClick;

import com.crashlytics.android.Crashlytics;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.User;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.TwitterUtils;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getName();

    private String callbackUrl;
    private Twitter twitter;
    private RequestToken requestToken;

    @InjectView(R.id.btn_login_twitter) Button mBtnLoginTwitter;
    @InjectView(R.id.loading) View mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Crashlytics.start(this);
        callbackUrl = getString(R.string.twitter_callback_url);
        twitter = TwitterUtils.getTwitterInstance(this);

        checkLoginStatus();

        this.setTheme(R.style.NoActionBar);
        setContentView(R.layout.activity_login);
    }

    private void checkLoginStatus() {
        if (TwitterUtils.hasAccessToken() && AppUtils.isLoggedIn()) {
            MainActivity.start(this);
            finish();
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
            AppUtils.showToast(R.string.login_failed, this);
            toggleLoginBtnStatus(true);
            return;
        }

        final Dialog dialog = AppUtils.makeSendingDialog(this);
        dialog.show();

        MainApplication.API.postUsersLogin(screenName, imgUrl, new Callback<User>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, "create_error_message: " + e.getMessage());
                dialog.dismiss();
                AppUtils.showToast(R.string.login_failed, LoginActivity.this);
                toggleLoginBtnStatus(true);
            }

            @Override
            public void success(User user, Response response) {
                dialog.dismiss();
                AppUtils.setUser(user);
                AppUtils.showToast(R.string.login_succeeded, LoginActivity.this);
                MainActivity.start(LoginActivity.this);
                finish();
            }

        });
    }

    @Override
    protected void initView() {
        //
    }

    @OnClick(R.id.btn_login_twitter)
    public void onClickBtnLoginTwitter() {
        toggleLoginBtnStatus(false);

        if (TwitterUtils.hasAccessToken()) {
            if (AppUtils.isLoggedIn()) {
                MainActivity.start(this);
                finish();
            } else {
                saveUser();
            }
        } else {
            authTwitter();
        }
    }

    private void toggleLoginBtnStatus(boolean enabled) {
        int resColorId = enabled ? R.color.text_white : R.color.text_gray;
        mBtnLoginTwitter.setTextColor(getResources().getColor(resColorId));
        mBtnLoginTwitter.setEnabled(enabled);

        int visibility = enabled ? View.GONE : View.VISIBLE;
        mLoading.setVisibility(visibility);
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
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    onSuccessOAuth(accessToken);
                } else {
                    AppUtils.showToast(R.string.login_failed, LoginActivity.this);
                }
            }
        };
        task.execute(verifier);
    }

    private void onSuccessOAuth(AccessToken accessToken) {
        TwitterUtils.storeAccessToken(accessToken);
        if (AppUtils.isLoggedIn()) {
            MainActivity.start(this);
            finish();
        } else {
            saveUser();
        }
    }

}
