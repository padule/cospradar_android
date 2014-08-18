package com.padule.cospradar.service;

import android.content.Context;
import android.content.Intent;

import com.padule.cospradar.base.BaseTimerService;
import com.padule.cospradar.util.NotificationUtils;
import com.padule.cospradar.util.TimeUtils;

public class GooglePlayEvaluationTimerService extends BaseTimerService {

    private static final int INTERVAL_MILLS = TimeUtils.getSecondsFromDays(2) * 1000;

    public static void start(Context context) {
        stop(context);

        Intent intent = new Intent(context, GooglePlayEvaluationTimerService.class);
        intent.putExtra(EXTRA_TIME, INTERVAL_MILLS);
        context.startService(intent);
    }

    @Override
    protected void startEvent() {
        NotificationUtils.showGooglePlay(this);
        stopSelf();
    }

    private static void stop(Context context) {
        Intent intent = new Intent(context, CharactorResetSuggestionTimerService.class);
        context.stopService(intent);
    }
}
