package com.padule.cospradar.service;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.base.BaseLocationListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.util.AppUtils;

public class LocationService extends Service {

    private static final String TAG = LocationService.class.getName();

    private BaseLocationListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initLocationListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (listener != null) {
            listener.stop();
        }
    }

    private void initLocationListener() {
        if (listener == null) {
            listener = new BaseLocationListener(this) {
                @Override
                public void onLocationChanged(Location loc) {
                    super.onLocationChanged(loc);
                    Log.d(TAG, "onLocationChanged: " + loc);
                    AppUtils.setLatLon((float)loc.getLatitude(), (float)loc.getLongitude());
                    uploadLocation(loc.getLatitude(), loc.getLongitude());
                }
            };
            listener.start();
        }
    }

    private void uploadLocation(double lat, double lon) {
        Charactor charactor = AppUtils.getCharactor();
        if (charactor != null) {
            // FIXME create charactors API return json is invalid...
            createLocation(charactor, lat, lon);
        }
    }

    private void createLocation(Charactor charactor, double lat, double lon) {
        MainApplication.API.postCharactorLocations(charactor.getId(), lat, lon,
                new Callback<CharactorLocation>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, "upload_error_message: " + e.getMessage());
            }

            @Override
            public void success(CharactorLocation location, Response response) {
                Charactor charactor = AppUtils.getCharactor();
                charactor.setLocation(location);
                AppUtils.setCharactor(charactor);
            }
        });
    }

}
