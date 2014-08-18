package com.padule.cospradar.base;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public abstract class BaseTimerService extends Service {

    protected static final String EXTRA_TIME = "time";

    private Timer timer = null;
    Handler handler = new Handler();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int time = intent.getIntExtra(EXTRA_TIME, 0);
        if (time <= 0) return 0;

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post( new Runnable() {
                    public void run() {
                        startEvent();
                    }
                });
            }
        }, time, time);

        return START_STICKY;
    }

    protected abstract void startEvent();

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
