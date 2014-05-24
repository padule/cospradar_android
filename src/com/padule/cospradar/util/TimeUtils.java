package com.padule.cospradar.util;

import java.util.Date;

public class TimeUtils {

    public static int getDiffDays(Date eDate, Date sDate) {
        if (eDate == null || sDate == null) {
            return 0;
        }
        int oneDayTime = 1000 * 60 * 60 * 24;
        long diff = (eDate.getTime() - sDate.getTime()) / oneDayTime;
        return (int)diff;
    }

}
