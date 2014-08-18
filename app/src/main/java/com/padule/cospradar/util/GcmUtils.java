
package com.padule.cospradar.util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.padule.cospradar.Constants;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.data.Device;

public class GcmUtils {

    private static final String TAG = GcmUtils.class.getName();

    public static void register(final Context context) {
        String regId = PrefUtils.get(PrefUtils.KEY_REGISTRATION_ID, "");

        if (!TextUtils.isEmpty(regId)) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                String regId = null;
                try {
                    regId = gcm.register(Constants.GOOGLE_API_PROJECT_ID);
                    Log.d(TAG, "regId: " + regId);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage() + "");
                }

                saveDeviceId(regId);
                return null;
            }
        }.execute();
    }

    private static void saveDeviceId(final String regId) {
        if (!AppUtils.isLoggedIn()) {
            return;
        }

        MainApplication.API.postDevices(AppUtils.getUser().getId(), Device.PLATFORM_ANDROID, regId, 
                new Callback<Device>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(Device device, Response response) {
                Log.d(TAG, "device saved. device_id: " + device.getToken());
                PrefUtils.put(PrefUtils.KEY_REGISTRATION_ID, regId);
            }
        });
    }

}
