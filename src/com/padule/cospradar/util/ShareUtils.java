package com.padule.cospradar.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.padule.cospradar.R;

public class ShareUtils {

    private static final String TAG = ShareUtils.class.getSimpleName();

    private static final int TWITTER_LIMIT_SIZE = 140;
    private static final int TWITTER_LIMIT_URL_SIZE = 22;
    private static final String HASH_TAG = "#cosradar";
    private static final String ELLIPSIS_STRING = "...";

    private static final String TYPE_TEXT_PLAIN = "text/plain";
    private static final String PACKAGE_TWITTER = "com.twitter.android";
    private static final String PACKAGE_WHATSAPP = "com.whatsapp";
    private static final String URL_LINE_MSG = "line://msg/text/";

    private static final String APP_TWITTER = "twitter";
    private static final String APP_LINE = "line";
    private static final String APP_WHATSAPP = "whatsapp";
    private static final String APP_OTHER = "other";

    public static void shareTwitter(String text, Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TYPE_TEXT_PLAIN);
        intent.setPackage(PACKAGE_TWITTER);
        intent.putExtra(Intent.EXTRA_TEXT, getTwitterText(text, 
                AppUtils.getGooglePlayUrl(APP_TWITTER, TAG)));

        try {
            sendAnalyticsEvent(AnalyticsUtils.EVENT_SHARED_TWITTER, context);
            context.startActivity(Intent.createChooser(intent, ""));
        } catch (ActivityNotFoundException e) {
            AppUtils.showToast(R.string.share_app_not_found, context);
        }
    }

    public static void shareLine(String text, Context context) {
        text = getTextWithUrl(text, AppUtils.getGooglePlayUrl(APP_LINE, TAG));
        text = TextUtils.getEncodedText(text);
        Uri uri = Uri.parse(URL_LINE_MSG + text);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            sendAnalyticsEvent(AnalyticsUtils.EVENT_SHARED_LINE, context);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            AppUtils.showToast(R.string.share_app_not_found, context);
        }
    }

    public static void shareWhatsapp(String text, Context context) {
        text = getTextWithUrl(text, AppUtils.getGooglePlayUrl(APP_WHATSAPP, TAG));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TYPE_TEXT_PLAIN);
        intent.setPackage(PACKAGE_WHATSAPP);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            sendAnalyticsEvent(AnalyticsUtils.EVENT_SHARED_WHATSAPP, context);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            AppUtils.showToast(R.string.share_app_not_found, context);
        }
    }

    public static void shareOther(String text, Context context) {
        text = getTextWithUrl(text, AppUtils.getGooglePlayUrl(APP_OTHER, TAG));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(TYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        try {
            sendAnalyticsEvent(AnalyticsUtils.EVENT_SHARED_OTHER, context);
            context.startActivity(Intent.createChooser(intent, ""));
        } catch (ActivityNotFoundException e) {
            AppUtils.showToast(R.string.share_app_not_found, context);
        }
    }

    private static void sendAnalyticsEvent(String action, Context context) {
        AnalyticsUtils.sendEvent(AnalyticsUtils.CATEGORY_SETTING, action, context);
    }

    private static String getTwitterText(String text, String url) {
        int limit = TWITTER_LIMIT_SIZE - TWITTER_LIMIT_URL_SIZE - HASH_TAG.length() - 2;
        if (text.length() > limit) {
            text = text.substring(0, limit - ELLIPSIS_STRING.length()) + ELLIPSIS_STRING;
        }
        return text + "\n" + url + " " + HASH_TAG;
    }

    private static String getTextWithUrl(String text, String url) {
        return text + "\n" + url;
    }

}
