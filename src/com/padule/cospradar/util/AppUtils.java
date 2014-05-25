package com.padule.cospradar.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;

public class AppUtils {

    public static void setCharactor(Charactor charactor) {
        String str = charactor != null ? charactor.serializeToString() : null;
        PrefUtils.put(Charactor.class.getName(), str);
    }

    public static void setUser(User user) {
        String str = user != null ? user.serializeToString() : null;
        PrefUtils.put(User.class.getName(), str);
    }

    public static boolean isMockMode() {
        return Constants.MOCK_MODE;
    }

    public static Charactor getCharactor() {
        Charactor charactor = (Charactor)Charactor.deSerializeFromString(PrefUtils.get(Charactor.class.getName(), null));
        if (charactor != null && !charactor.isEnabled()) {
            setCharactor(null);
            charactor = null;
        }
        return charactor;
    }

    public static User getUser() {
        return (User)User.deSerializeFromString(PrefUtils.get(User.class.getName(), null));
    }

    public static void showToast(String message, Context context) {
        if (message == null || message.length() == 0) {
            return;
        }

        try {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            AQUtility.report(e);
        }
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

}
