package com.padule.cospradar.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.padule.cospradar.util.NotificationUtils;

public class TimerService extends Service {

    private static final String TAG = TimerService.class.getName();

    private static final int TYPE_DEFAULT = -1;
    public static final int TYPE_GOOGLE_PLAY = 0;
    private static final String EXTRA_TIME = "time";
    private static final String EXTRA_TYPE = "type";

    private Timer timer = null;
    Handler handler = new Handler();

    public static void start(Context context, int seconds, int type) {
        Intent intent = new Intent(context, TimerService.class);
        intent.putExtra(EXTRA_TIME, seconds * 1000);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int time = intent.getIntExtra(EXTRA_TIME, 0);
        final int type = intent.getIntExtra(EXTRA_TYPE, TYPE_DEFAULT);
        if (time <= 0) return 0;

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post( new Runnable() {
                    public void run() {
                        startEvent(type);
                    }
                });
            }
        }, time, time);

        return START_STICKY;
    }

    private void startEvent(int type) {
        switch(type) {
        case TYPE_GOOGLE_PLAY:
            NotificationUtils.showGooglePlay(this);
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        if( timer != null ){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
