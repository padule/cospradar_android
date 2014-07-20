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
import com.padule.cospradar.activity.ChatBoardActivity;
import com.padule.cospradar.activity.LoginActivity;
import com.padule.cospradar.activity.MainActivity;
import com.padule.cospradar.base.BaseActivity;

public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getName();

    public static final int DEFAULT_ID = -1;

    private static final int ID_CHATBOARD_MINE_COMMENTED = 101;
    private static final int ID_CHATBOARD_COMMENTED = 102;
    private static final int ID_GOOGLE_PLAY = 901;

    public static final int DEFAULT_MODEL_ID = -1;
    public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;

    private static final String TAG_GCM = "gcm";
    private static final String TAG_WEB = "web";

    private static final String EXTRA_NOTIFICATION_ID = "notification_id";
    private static final String EXTRA_MODEL_ID = "model_id";
    private static final String EXTRA_EXTRA_URL = "extra_url";

    public static void show(int id, int modelId, final int priority, final String title, 
            final String text, final String extraUrl, final String iconUrl, 
            final String bigPictureUrl, final Context context) {

        if (!validate(id, modelId, text)) {
            return;
        }

        final String createdText = TextUtils.isEmpty(text) ? createText(id, context) : text;
        final PendingIntent intent = createIntent(id, modelId, extraUrl, context);

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                show(TAG_GCM, priority, title, createdText, iconUrl,
                        bigPictureUrl, context, intent);
                return null;
            }
        }.execute();
    }

    private static void showForWeb(final int id, final String text, final String extraUrl, 
            final String iconUrl, final Context context) {
        final PendingIntent intent = createIntent(id, DEFAULT_MODEL_ID, extraUrl, context);

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                show(TAG_WEB, DEFAULT_PRIORITY, null, text, iconUrl, null, context, intent);
                return null;
            }
        }.execute();
    }

    private static String createText(int id, Context context) {
        switch(id) {
        case ID_CHATBOARD_MINE_COMMENTED:
            return context.getString(R.string.gcm_chatboard_mine_commented);
        case ID_CHATBOARD_COMMENTED:
            return context.getString(R.string.gcm_chatboard_commented);
        default:
            return "";
        }
    }

    public static void showGooglePlay(Context context) {
        showForWeb(ID_GOOGLE_PLAY, context.getString(R.string.rating_notification, 
                AppUtils.getUser().getScreenName(), context.getString(R.string.app_name)), 
                AppUtils.getGooglePlayUrl(null, null), null, context);
    }

    private static void show(String tag, int priority, String title, String text, 
            String iconUrl, String bigPictureUrl, Context context, PendingIntent intent) {
        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name);
        }

        try {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.ic_app) // TODO change notification icon.
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(text)
            .setTicker(text)
            .setPriority(Integer.MAX_VALUE)
            .setContentIntent(intent)
            .setAutoCancel(!TAG_WEB.equals(tag));

            Notification notification = buildNotification(builder, context, title, text, iconUrl, bigPictureUrl);

            notify(context, notification, priority, tag);

        } catch(Exception e) {
            Log.e(TAG, e.toString() + "");
        }
    }

    private static void notify(Context context, Notification notification, int priority, String tag) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (shouldClear(priority)) {
            notificationManager.cancel(tag, DEFAULT_ID);
            notificationManager.notify(tag, DEFAULT_ID, notification);
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

    private static PendingIntent createIntent(int id, int modelId, String extraUrl, Context context) {
        Class<?> clazz = AppUtils.isLoggedIn() ? MainActivity.class : LoginActivity.class;
        Intent intent = new Intent(context, clazz);
        intent.putExtra(EXTRA_NOTIFICATION_ID, id);
        intent.putExtra(EXTRA_MODEL_ID, modelId);
        intent.putExtra(EXTRA_EXTRA_URL, extraUrl);
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

        return true;
    }

    public static void checkIntent(BaseActivity activity) {
        Intent intent = activity.getIntent();
        if (intent == null) return;

        int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, DEFAULT_ID);

        switch(notificationId) {
        case ID_GOOGLE_PLAY:
            String url = intent.getStringExtra(EXTRA_EXTRA_URL);
            AppUtils.showWebView(url, activity);
            break;
        case ID_CHATBOARD_MINE_COMMENTED:
        case ID_CHATBOARD_COMMENTED:
            int charactorId = intent.getIntExtra(EXTRA_MODEL_ID, DEFAULT_MODEL_ID);
            ChatBoardActivity.start(activity, charactorId);
            break;
        }
    }

}
