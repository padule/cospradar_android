package com.padule.cospradar.util;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.androidquery.util.AQUtility;
import com.padule.cospradar.MainApplication;

public class PrefUtils {
    
    public static final String KEY_PHOTO_PATH = "photo_path";
    public static final String KEY_PHOTO_NAME = "photo_name";
    public static final String KEY_LAT_LON = "lat_lon";

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

    public static String get(String name, String defaultValue) {
        return getPref().getString(name, defaultValue);
    }

    public static <T extends Enum<T>> void putEnum(Enum<T> value) {
        String key = value.getClass().getName();
        put(key, value.name());
        enums.put(key, value);
    }

    public static void clearEnum(Class<?> cls) {
        String key = cls.getName();
        put(key, (String) null);
    }

    private static Map<String, Object> enums = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T getEnum(Class<T> cls, T defaultValue) {
        String key = cls.getName();

        T result = (T) enums.get(key);
        if (result == null) {
            result = PrefUtils.getPrefEnum(cls, defaultValue);
            enums.put(key, result);
        }

        return result;
    }

    private static <T extends Enum<T>> T getPrefEnum(Class<T> cls, T defaultValue) {
        T result = null;

        String pref = get(cls.getName(), null);

        if (pref != null) {
            try {
                result = Enum.valueOf(cls, pref);
            } catch (Exception e) {
                clearEnum(cls);
                AQUtility.report(e);
            }
        }

        if (result == null) {
            result = defaultValue;
        }

        return result;
    }


}
