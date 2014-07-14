package com.padule.cospradar.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.padule.cospradar.R;
import com.padule.cospradar.activity.LoginActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;

public class AppUtils {

    private static final String TAG = AppUtils.class.getName();
    private static final String PACKAGE_MMS = "mms";
    private static final String PACKAGE_GMAIL = "android.gm";
    private static final String PACKAGE_EMAIL = "android.email";
    private static final String INTENT_TYPE_MSG = "message/rfc822";
    private static final String INTENT_TYPE_TEXT = "text/plain";

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
        return isLoginUser(user.getId());
    }

    public static boolean isLoginUser(int userId) {
        User loginUser = getUser();
        if (loginUser != null && loginUser.getId() == userId) {
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

    public static void showToast(int stringResId, Context context, int duration) {
        showToast(context.getResources().getString(stringResId), context, duration);
    }

    public static void showToast(String message, Context context) {
        showToast(message, context, Toast.LENGTH_LONG);
    }

    public static void showToast(int stringResId, Context context) {
        showToast(context.getResources().getString(stringResId), context);
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
            return context.getString(R.string.version_prefix, packageInfo.versionName);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
            return "";
        }
    }

    public static void logout(Context context) {
        AppUtils.setCharactor(null);
        AppUtils.setUser(null);

        showToast(context.getString(R.string.logout_done), context);
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openMailChooser(Context context, String text, String[] mails, String subject) {
        Intent mailIntent = new Intent();
        mailIntent.setAction(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_TEXT, text);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, mails);
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mailIntent.setType(INTENT_TYPE_MSG);

        PackageManager pm = context.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType(INTENT_TYPE_TEXT);

        Intent openInChooser = Intent.createChooser(mailIntent, "");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (ResolveInfo ri : resInfo) {
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains(PACKAGE_EMAIL)) {
                mailIntent.setPackage(packageName);
            } else if (packageName.contains(PACKAGE_MMS) || packageName.contains(PACKAGE_GMAIL)) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(INTENT_TYPE_TEXT);
                if (packageName.contains(PACKAGE_MMS)) {
                    intent.putExtra("subject", subject);
                    intent.putExtra("sms_body", text);
                    intent.putExtra("address", mails[0]);
                    intent.setType(INTENT_TYPE_MSG);
                } else if(packageName.contains(PACKAGE_GMAIL)) {
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_EMAIL, mails);
                    intent.setType(INTENT_TYPE_MSG);
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        context.startActivity(openInChooser);
    }

    public static void showWebView(String url, Context context) {
        if (TextUtils.isEmpty(url)) return;

        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static String getGooglePlayUrl(String utmSource, String utmContent){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("play.google.com");
        builder.path("/store/apps/details");
        builder.appendQueryParameter("id", "com.padule.cospradar");

        if (utmSource != null && utmContent != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("utm_source=");
            sb.append(utmSource);
            sb.append("&utm_medium=direct");
            sb.append("&");
            sb.append("utm_content=");
            sb.append(utmContent);
            builder.appendQueryParameter("referrer", sb.toString());
        }

        return builder.build().toString();  
    }

}
