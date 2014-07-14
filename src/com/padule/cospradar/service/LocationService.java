package com.padule.cospradar.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private boolean isUploading = false;

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

    private void uploadLocation(final double lat, final double lon) {
        if (!AppUtils.isLoggedIn() || isUploading) {
            return;
        }

        isUploading = true;
        MainApplication.API.getCharactors(createParams(AppUtils.getUser().getId()), 
                new Callback<List<Charactor>>() {
            @Override
            public void failure(RetrofitError e) {
                isUploading = false;
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(List<Charactor> charactors, Response response) {
                isUploading = false;
                for (Charactor charactor : charactors) {
                    if (charactor != null) {
                        // FIXME create charactors API return json is invalid...
                        createLocation(charactor, lat, lon);
                    }
                }
            }
        });
    }

    private Map<String, String> createParams(int userId) {
        Map<String, String> params = new HashMap<String, String>();
        if (userId > 0) {
            params.put(ApiService.PARAM_USER_ID, userId + "");
            params.put(ApiService.PARAM_DESC, "is_enabled");
        }
        params.put(ApiService.PARAM_LATITUDE, AppUtils.getLatitude() + "");
        params.put(ApiService.PARAM_LONGITUDE, AppUtils.getLongitude() + "");

        return params;
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
                if (charactor != null) {
                    charactor.setLocation(location);
                    AppUtils.setCharactor(charactor);
                }
            }
        });
    }

}
