package com.padule.cospradar.util;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class AnalyticsUtils {

    public static String CATEGORY_RADAR = "01_Radar";

    public static String EVENT_CLICKED_SEARCH_WITH_TEXT = "ClickedSearchWithText";
    public static String EVENT_CLICKED_SEARCH_WITHOUT_TEXT = "ClickedSearchWithoutText";

    public static void activityStart(Activity activity) {
        EasyTracker.getInstance(activity).activityStart(activity);
    }

    public static void activityStop(Activity activity) {
        EasyTracker.getInstance(activity).activityStop(activity);
    }

    public static void sendEvent(String category, String action, Context context) {
        if (context != null) {
            EasyTracker tracker = EasyTracker.getInstance(context);
            tracker.send(MapBuilder.createEvent(category, action, null, null).build());
        }
    }

}
