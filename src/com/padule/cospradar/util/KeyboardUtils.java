package com.padule.cospradar.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardUtils {

    public static void hide(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        activity.getCurrentFocus().clearFocus();
    }

    public static void show(Context context, EditText edit) {
        show(context, edit, 0);
    }

    public static void show(final Context context, final EditText edit, int delayTime) {
        final Runnable showKeyboardDelay = new Runnable() {
            @Override
            public void run() {
                if (context != null) {
                    InputMethodManager imm = (InputMethodManager)context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        };
        new Handler().postDelayed(showKeyboardDelay, delayTime);
    }
}
