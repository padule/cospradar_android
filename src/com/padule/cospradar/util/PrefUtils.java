package com.padule.cospradar.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.padule.cospradar.MainApplication;

public class PrefUtils {

    public static final String KEY_PHOTO_PATH = "photo_path";
    public static final String KEY_PHOTO_NAME = "photo_name";
    public static final String KEY_LAT_LON = "lat_lon";
    public static final String KEY_LAST_NOTIFICATION_PRIORITY = "last_notification_priority";
    public static final String KEY_REGISTRATION_ID = "registration_id";

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

    public static boolean contains(String name) {
        return getPref().contains(name);
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
