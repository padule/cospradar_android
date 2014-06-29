package com.padule.cospradar.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.padule.cospradar.service.GcmService;

public class GcmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName component = 
                new ComponentName(context.getPackageName(), GcmService.class.getName());
        startWakefulService(context, (intent.setComponent(component)));
        setResultCode(Activity.RESULT_OK);
    }

}
