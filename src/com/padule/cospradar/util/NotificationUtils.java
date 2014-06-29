package com.padule.cospradar.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.activity.LoginActivity;
import com.padule.cospradar.activity.MainActivity;

public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getName();

    public static final int DEFAULT_ID = -1;
    public static final int DEFAULT_MODEL_ID = -1;
    public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;

    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_MODEL_ID = "model_id";

    public static void show(int id, int modelId, final int priority, final String title, 
            final String text, final String extraUrl, final String iconUrl, 
            final String bigPictureUrl, final Context context) {

        if (!validate(id, modelId, text)) {
            return;
        }

        final PendingIntent intent = createIntent(id, modelId, context);

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                show(priority, title, text, extraUrl, iconUrl,
                        bigPictureUrl, context, intent);
                return null;
            }
        }.execute();
    }

    private static void show(int priority, String title, String text, 
            String extraUrl, String iconUrl, String bigPictureUrl, 
            Context context, PendingIntent intent) {

        if (title == null) {
            title = context.getString(R.string.app_name);
        }

        try {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_launcher) // TODO change notification icon.
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(text)
            .setTicker(text)
            .setPriority(Integer.MAX_VALUE)
            .setContentIntent(intent)
            .setAutoCancel(true);

            Notification notification = buildNotification(builder, context, title, text, iconUrl, bigPictureUrl);

            notify(context, notification, priority);

        } catch(Exception e) {
            Log.e(TAG, e.toString() + "");
        }
    }

    private static void notify(Context context, Notification notification, int priority) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (shouldClear(priority)) {
            notificationManager.cancel(TAG, DEFAULT_ID);
            notificationManager.notify(TAG, DEFAULT_ID, notification);
        }
    }

    private static boolean shouldClear(int priority) {
        int lastPriority = PrefUtils.getInt(PrefUtils.KEY_LAST_NOTIFICATION_PRIORITY, 0);
        PrefUtils.put(PrefUtils.KEY_LAST_NOTIFICATION_PRIORITY, Math.max(priority, lastPriority));
        return priority >= lastPriority;
    }

    public static void cancelAll(Context context) {
        NotificationManager manager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(TAG, DEFAULT_ID);
        PrefUtils.put(TAG, 0);
    }

    private static Notification buildNotification(Builder builder, Context context, 
            String title, String text, String iconUrl, String bigPictureUrl) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return builder.build();
        } else if (!TextUtils.isEmpty(bigPictureUrl)) {
            return createBigPictureStyle(context, iconUrl, bigPictureUrl, builder, title, text);
        } else {
            return createBigTextStyle(context, iconUrl, builder, title, text);
        }
    }

    private static Notification createBigTextStyle(Context context, String iconUrl, 
            Builder builder, String title, String text) {
        Bitmap largeIcon = ImageUtils.getNotificationLargeIcon(context, iconUrl);
        builder.setLargeIcon(largeIcon);

        return new BigTextStyle(builder)
        .setBigContentTitle(title)
        .bigText(text)
        .build();
    }

    private static Notification createBigPictureStyle(Context context, String iconUrl,
            String bigPictureUrl, Builder builder, String title, String text) {
        Bitmap largeIcon = ImageUtils.getNotificationLargeIcon(context, iconUrl);
        Bitmap bigPicture = MainApplication.IMAGE_LOADER.loadImageSync(bigPictureUrl);
        builder.setLargeIcon(largeIcon);

        return new BigPictureStyle(builder)
        .setBigContentTitle(title)
        .bigPicture(bigPicture)
        .setSummaryText(text)
        .build();
    }

    private static PendingIntent createIntent(int id, int modelId, Context context) {
        Class<?> clazz = AppUtils.isLoggedIn() ? MainActivity.class : LoginActivity.class;
        Intent intent = new Intent(context, clazz);
        intent.putExtra(EXTRA_NOTIFICATION_ID, id);
        intent.putExtra(EXTRA_MODEL_ID, modelId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return PendingIntent.getActivity(context, (int) System.currentTimeMillis(), 
                intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private static boolean validate(int id, int modelId, String text) {
        if (id == DEFAULT_ID) {
            Log.e(TAG, "id is invalid");
            return false;
        }

        if (modelId == DEFAULT_MODEL_ID) {
            Log.e(TAG, "model_id is invalid");
            return false;
        }

        if (TextUtils.isEmpty(text)) {
            Log.e(TAG, "text is nothing");
            return false;
        }

        return true;
    }

}
