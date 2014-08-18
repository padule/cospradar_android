
package com.padule.cospradar.util;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.Context;

import com.padule.cospradar.R;

public class TwitterUtils {

    private static final String PREF_TWITTER_TOKEN = "token";
    private static final String PREF_TWITTER_TOKEN_SECRET = "token_secret";

    public static Twitter getTwitterInstance(Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (hasAccessToken()) {
            twitter.setOAuthAccessToken(loadAccessToken());
        }
        return twitter;
    }

    public static void storeAccessToken(AccessToken accessToken) {
        PrefUtils.put(PREF_TWITTER_TOKEN, accessToken.getToken());
        PrefUtils.put(PREF_TWITTER_TOKEN_SECRET, accessToken.getTokenSecret());
    }

    public static AccessToken loadAccessToken() {
        String token = PrefUtils.get(PREF_TWITTER_TOKEN, null);
        String tokenSecret = PrefUtils.get(PREF_TWITTER_TOKEN_SECRET, null);
        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }

    public static boolean hasAccessToken() {
        return loadAccessToken() != null;
    }
}
