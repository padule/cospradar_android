package com.padule.cospradar.util;

import java.util.Date;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.padule.cospradar.MainApplication;

public class PrefUtils {

    public static final String KEY_PHOTO_PATH = "photo_path";
    public static final String KEY_PHOTO_NAME = "photo_name";
    public static final String KEY_LAT_LON = "lat_lon";
    public static final String KEY_LAST_NOTIFICATION_PRIORITY = "last_notification_priority";
    public static final String KEY_REGISTRATION_ID = "registration_id";
    public static final String KEY_TUTORIAL_SHOWN = "tutorial_shown";
    public static final String KEY_SUGGEST_CHARACTOR_SETTTING_TIME = "suggest_charactor_setting_time";

    private static SharedPreferences pref;

    public static SharedPreferences getPref() {
        if (pref == null) {
            pref = PreferenceManager
                    .getDefaultSharedPreferences(MainApplication.getContext());
        }

        return pref;
    }

    public static void put(String name, String value) {
        SharedPreferences.Editor edit = getPref().edit();
        edit.putString(name, value);
        edit.commit();
    }

    public static void put(String name, int value) {
        SharedPreferences.Editor edit = getPref().edit();
        edit.putInt(name, value);
        edit.commit();
    }

    public static void put(String name, Long value) {
        SharedPreferences.Editor edit = getPref().edit();
        edit.putLong(name, value);
        edit.commit();
    }

    public static void put(String name, Boolean value) {
        SharedPreferences.Editor edit = getPref().edit();
        edit.putBoolean(name, value);
        edit.commit();
    }

    public static void put(String name, Date value) {
        SharedPreferences.Editor edit = getPref().edit();
        edit.putLong(name, value.getTime());
        edit.commit();
    }

    public static void remove(String name) {
        SharedPreferences.Editor edit = getPref().edit();
        edit.remove(name);
        edit.commit();
    }

    public static boolean contains(String name) {
        return getPref().contains(name);
    }

    public static Date getDate(String name) {
        long time = getPref().getLong(name, 0L);
        if (time > 0) {
            return new Date(time);
        } else {
            return null;
        }
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        return getPref().getBoolean(name, defaultValue);
    }

    public static Long getLong(String name, Long defaultValue) {
        return getPref().getLong(name, defaultValue);
    }

    public static int getInt(String name, int defaultValue) {
        return getPref().getInt(name, defaultValue);
    }

    public static String get(String name, String defaultValue) {
        return getPref().getString(name, defaultValue);
    }

}
