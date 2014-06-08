package com.padule.cospradar.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.base.BaseLocationListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.util.AppUtils;

public class LocationService extends Service {

    private static final String TAG = LocationService.class.getName();

    private BaseLocationListener listener;
    private AQuery aq;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        aq = new AQuery(this);
        initLocationListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "MyService#onDestroy", Toast.LENGTH_SHORT).show();
    }

    private void initLocationListener() {
        if (listener == null) {
            listener = new BaseLocationListener(this) {
                @Override
                public void onLocationChanged(Location loc) {
                    super.onLocationChanged(loc);
                    Log.d(TAG, "onLocationChanged: " + loc);
                    uploadLocation(loc.getLatitude(), loc.getLongitude());
                }
            };
            listener.start();
        }
    }

    private void uploadLocation(double lat, double lon) {
        Charactor charactor = AppUtils.getCharactor();
        if (charactor != null) {
            if (charactor.getLocation() != null) {
                updateLocation(charactor, lat, lon);
            } else {
                createLocation(charactor, lat, lon);
            }
        }
    }

    private void createLocation(Charactor charactor, double lat, double lon) {
        String url = AppUrls.getCharactorLocationsCreate();
        final Map<String, Object> params = createParams(charactor.getId(), lat, lon);
        Log.d(TAG, "create_params: " + params.toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d(TAG, "create_url: " + url);
                uploadLocationCallBack(json, status);
            }
        });
    }

    private Map<String, Object> createParams(int charactorId, double lat, double lon) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AppUrls.PARAM_LATITUDE, String.valueOf(lat));
        params.put(AppUrls.PARAM_LONGITUDE, String.valueOf(lon));
        params.put(AppUrls.PARAM_CHARACTOR_ID, charactorId);
        return params;
    }

    private void uploadLocationCallBack(JSONObject json, AjaxStatus status) {
        if (json != null) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            CharactorLocation location = gson.fromJson(json.toString(), CharactorLocation.class);

            Charactor charactor = AppUtils.getCharactor();
            charactor.setLocation(location);
            AppUtils.setCharactor(charactor);
        } else {
            Log.e(TAG, "upload_error_message: " + status.getMessage());
        }
    }

    private void updateLocation(Charactor charactor, double lat, double lon) {
        String url = AppUrls.getCharactorLocationsUpdate(charactor.getLocation().getId());
        final Map<String, Object> params = createParams(charactor.getId(), lat, lon);
        Log.d(TAG, "update_params: " + params.toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d(TAG, "update_url: " + url);
                uploadLocationCallBack(json, status);
            }
        });
    }

}
