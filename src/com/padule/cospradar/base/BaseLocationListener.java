package com.padule.cospradar.base;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class BaseLocationListener implements LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static final int INTERVAL_MILLS = 5000;
    private static final int MIN_PLACE_DISTANCE_METER = 5; // TODO adjust
    private LocationClient locClient = null;
    private boolean reConnect = false;

    public BaseLocationListener(Context context) {
        locClient = new LocationClient(context, this, this);
    }

    public void start() {
        if (!locClient.isConnected() && !locClient.isConnecting()) {
            reConnect = false;
            locClient.connect();
        }
    }

    public void stop() {
        reConnect = false;
        locClient.removeLocationUpdates(this);
        locClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        reConnect = true;
        locClient.requestLocationUpdates(createLocationRequest(), this);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest req = LocationRequest.create();
        req.setFastestInterval(INTERVAL_MILLS);
        req.setInterval(INTERVAL_MILLS);
        req.setSmallestDisplacement(MIN_PLACE_DISTANCE_METER);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return req;
    }

    @Override
    public void onDisconnected() {
        if (reConnect) {
            start();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //
    }

    @Override
    public void onLocationChanged(Location loc) {
        //
    }
}
