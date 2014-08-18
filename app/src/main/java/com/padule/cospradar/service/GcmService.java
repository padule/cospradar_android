package com.padule.cospradar.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.padule.cospradar.receiver.GcmReceiver;
import com.padule.cospradar.util.NotificationUtils;

public class GcmService extends IntentService {

    private static final String TAG = GcmService.class.getName();

    private static final String PARAM_ID = "id";
    private static final String PARAM_TITLE = "title";
    private static final String PARAM_TEXT = "text";
    private static final String PARAM_MODEL_ID = "model_id";
    private static final String PARAM_EXTRA_URL = "extra_url";
    private static final String PARAM_ICON_URL = "icon_url";
    private static final String PARAM_BIG_PICTURE_URL = "big_picture_url";
    private static final String PARAM_PRIORITY = "priority";

    public GcmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            showNotification(bundle);
        }

        GcmReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(Bundle bundle) {
        if (bundle.isEmpty()) {
            return;
        }

        int id = getInt(bundle, PARAM_ID, NotificationUtils.DEFAULT_ID);
        int modelId = getInt(bundle, PARAM_MODEL_ID, NotificationUtils.DEFAULT_MODEL_ID);
        int priority = getInt(bundle, PARAM_PRIORITY, NotificationUtils.DEFAULT_PRIORITY);
        String title = getString(bundle, PARAM_TITLE);
        String text = getString(bundle, PARAM_TEXT);
        String extraUrl = getString(bundle, PARAM_EXTRA_URL);
        String iconUrl = getString(bundle, PARAM_ICON_URL);
        String bigPictureUrl = getString(bundle, PARAM_BIG_PICTURE_URL);

        NotificationUtils.showForGcm(id, modelId, priority, title, text, extraUrl, iconUrl, bigPictureUrl, this);
    }

    private static String getString(Bundle bundle, String key) {
        return bundle.containsKey(key) ? bundle.getString(key) : null;
    }

    private static int getInt(Bundle bundle, String key, int defaultValue) {
        try {
            return Integer.valueOf(getString(bundle, key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
