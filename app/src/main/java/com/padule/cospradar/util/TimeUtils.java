package com.padule.cospradar.util;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;

import com.padule.cospradar.R;

public class TimeUtils {

    public static int getSecondsFromDays(int days) {
        return days * 24 * 60 * 60;
    }

    public static int getSecondsFromHours(int hours) {
        return hours * 60 * 60;
    }

    public static int getDiffDays(Date eDate, Date sDate) {
        if (eDate == null || sDate == null) {
            return 0;
        }
        int oneDayTime = 1000 * 60 * 60 * 24;
        long diff = (eDate.getTime() - sDate.getTime()) / oneDayTime;
        return (int)diff;
    }

    private static long getDateTimeDiff(Date date, long unit) {
        long diffMills = System.currentTimeMillis() - date.getTime();
        return diffMills / unit;
    }

    private static long getDayDiff(Date date) {
        return getDateTimeDiff(date, 24 * 60 * 60 * 1000);
    }

    private static long getHourDiff(Date date) {
        return getDateTimeDiff(date, 60 * 60 * 1000);
    }

    private static long getMinDiff(Date date) {
        return getDateTimeDiff(date, 60 * 1000);
    }

    private static long getSecDiff(Date date) {
        return getDateTimeDiff(date, 1000);
    }

    public static String getDisplayDate(Date date, Context context) {
        if (date == null) {
            return "";
        }

        // date = convertUTCToLocal(date);

        long dayDiff = getDayDiff(date);
        long hourDiff = getHourDiff(date);
        long minDiff = getMinDiff(date);
        long secDiff = getSecDiff(date);

        if (dayDiff >= 1) {
            DateFormat format = android.text.format.DateFormat.getMediumDateFormat(context);
            return format.format(date);
        } else if (hourDiff > 1) {
            return context.getString(R.string.time_hours_ago, hourDiff);
        } else if (hourDiff == 1) {
            return context.getString(R.string.time_1hour_ago);
        } else if (minDiff > 1) {
            return context.getString(R.string.time_minutes_ago, minDiff);
        } else if (minDiff == 1) {
            return context.getString(R.string.time_1minute_ago);
        } else if (secDiff > 1) {
            return context.getString(R.string.time_seconds_ago, secDiff);
        } else if (secDiff == 1) {
            return context.getString(R.string.time_1second_ago);
        } else {
            return context.getString(R.string.time_just_now);
        }
    }

}
