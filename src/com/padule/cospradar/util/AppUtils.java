package com.padule.cospradar.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;

public class AppUtils {

    private static final String TAG = AppUtils.class.getName();

    public static void setCharactor(Charactor charactor) {
        User user = getUser();
        user.addCharactor(charactor);
        setUser(user);
        String str = charactor != null ? charactor.serializeToString() : null;
        PrefUtils.put(Charactor.class.getName(), str);
    }

    public static void setUser(User user) {
        String str = user != null ? user.serializeToString() : null;
        PrefUtils.put(User.class.getName(), str);
    }

    public static void setLatLon(float lat, float lon) {
        PrefUtils.put(PrefUtils.KEY_LAT_LON, lat + "," + lon);
    }

    public static float getLatitude() {
        float[] array = getLatLon();
        if (array != null) {
            return array[0];
        } else {
            return 0;
        }
    }

    public static float getLongitude() {
        float[] array = getLatLon();
        if (array != null) {
            return array[1];
        } else {
            return 0;
        }
    }

    private static float[] getLatLon() {
        try {
            String latLon = PrefUtils.get(PrefUtils.KEY_LAT_LON, null);
            if (latLon != null) {
                String[] array = latLon.split(",");
                float[] result = new float[2];
                result[0] = Float.parseFloat(array[0]);
                result[1] = Float.parseFloat(array[1]);
                return result;
            } else {
                return null;
            }
        } catch(Exception e) {
            return null;
        }
    }

    public static Charactor getCharactor() {
        Charactor charactor = (Charactor)Charactor.deSerializeFromString(PrefUtils.get(Charactor.class.getName(), null));
        if (charactor != null && !charactor.isEnabled()) {
            setCharactor(null);
            charactor = null;
        }
        return charactor;
    }

    public static boolean isCurrentCharactor(Charactor charactor) {
        if (getCharactor() != null 
                && charactor != null
                && getCharactor().getId() == charactor.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public static User getUser() {
        return (User)User.deSerializeFromString(PrefUtils.get(User.class.getName(), null));
    }

    public static boolean isLoggedIn() {
        return getUser() != null;
    }

    public static boolean isLoginUser(User user) {
        User loginUser = getUser();
        if (loginUser != null && loginUser.getId() == user.getId()) {
            return true;
        } else {
            return false;
        }
    }

    public static void showToast(String message, Context context, int duration) {
        if (message == null || message.length() == 0) {
            return;
        }

        try {
            Toast toast = Toast.makeText(context, message, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            //
        }
    }

    public static void showToast(String message, Context context) {
        showToast(message, context, Toast.LENGTH_LONG);
    }

    private static Dialog makeProgressDialog(String message, Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setMessage(message);

        return dialog;
    }

    public static Dialog makeLoadingDialog(Context context) {
        String message = context.getString(R.string.loading);
        return makeProgressDialog(message, context);
    }

    public static Dialog makeSendingDialog(Context context) {
        String message = context.getString(R.string.sending);
        return makeProgressDialog(message, context);
    }

    public static void vibrate(long mills, Context context) {
        Vibrator vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(mills);
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
            return "";
        }
    }

}
