package com.padule.cospradar.util;

import android.content.Context;
import android.text.Html;
import android.widget.EditText;

import com.padule.cospradar.R;

public class TextUtils {

    public static boolean isEmpty(EditText edit) {
        return isEmpty(edit.getText().toString());
    }

    public static boolean isEmpty(String text) {
        if (text == null) {
            return true;
        } else if (text.trim().length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String decodeCharacters(String text) {
        if (isEmpty(text)) {
            return "";
        }
        return Html.fromHtml(text).toString();
    }

    public static String getKiroMeterString(Context context, double meter) {
        double kiro = Math.floor(meter / 1000 * 10) / 10;
        return context.getString(R.string.radar_distance, String.valueOf(kiro));
    }

}
