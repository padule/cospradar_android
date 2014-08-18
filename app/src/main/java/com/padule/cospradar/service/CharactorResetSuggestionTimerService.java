package com.padule.cospradar.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.padule.cospradar.base.BaseTimerService;
import com.padule.cospradar.util.NotificationUtils;
import com.padule.cospradar.util.TimeUtils;

public class CharactorResetSuggestionTimerService extends BaseTimerService {

    private static final String TAG = CharactorResetSuggestionTimerService.class.getName();
    private static final int INTERVAL_HOURS = 8;

    private static void start(Context context) {
        stop(context);

        Intent intent = new Intent(context, CharactorResetSuggestionTimerService.class);
        intent.putExtra(EXTRA_TIME, TimeUtils.getSecondsFromHours(INTERVAL_HOURS) * 1000);
        context.startService(intent);
    }

    public static void startOrStop(Context context, boolean isCharactorEnabled) {
        Log.d(TAG, "startOrStop: " + isCharactorEnabled);

        if(isCharactorEnabled) {
            start(context);
        } else {
            stop(context);
        }
    }

    private static void stop(Context context) {
        Intent intent = new Intent(context, CharactorResetSuggestionTimerService.class);
        context.stopService(intent);
    }

    @Override
    protected void startEvent() {
        Log.d(TAG, "startEvent");
        NotificationUtils.showCharactorResetSuggestion(this, INTERVAL_HOURS);
        stopSelf();
    }
}
