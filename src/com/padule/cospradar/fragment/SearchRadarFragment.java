package com.padule.cospradar.fragment;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.activity.CommentActivity;
import com.padule.cospradar.base.BaseFragment;
import com.padule.cospradar.base.BaseLocationListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.ui.RadarView;
import com.padule.cospradar.ui.RadarView.RadarListener;

public class SearchRadarFragment extends BaseFragment implements SensorEventListener, RadarListener {

    private static final String TAG = SearchRadarFragment.class.getName();
    private final static double RAD2DEG = 180/Math.PI;

    private float[] rotationMatrix = new float[9];
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] attitude = new float[3];

    private SensorManager sensorManager;

    @InjectView(R.id.radar_view)
    RadarView radarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_radar, container, false);
        ButterKnife.inject(this, view);

        initLocationListener();
        initSensors();
        initRadar();
        return view;
    }

    private void initRadar() {
        this.radarView.setRadarListener(this);
    }

    private void initLocationListener() {
        BaseLocationListener listener = new BaseLocationListener(getActivity()) {
            @Override
            public void onLocationChanged(Location loc) {
                super.onLocationChanged(loc);
                if (radarView != null) {
                    radarView.setCenterLocation((float)loc.getLatitude(), (float)loc.getLongitude());
                }
            }
        };
        listener.start();
    }

    private void initSensors() {
        sensorManager = (SensorManager)getActivity().getSystemService(Activity.SENSOR_SERVICE);
    }

    public void onResume(){
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void onPause(){
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
        case Sensor.TYPE_MAGNETIC_FIELD:
            geomagnetic = event.values.clone();
            break;
        case Sensor.TYPE_ACCELEROMETER:
            gravity = event.values.clone();
            break;
        }

        if (geomagnetic != null && gravity != null) {
            SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic);
            SensorManager.getOrientation(rotationMatrix, attitude);

            float azimuth = (float)(attitude[0] * RAD2DEG);
            int pitch = (int)(attitude[1] * RAD2DEG);
            int roll = (int)(attitude[2] * RAD2DEG);
            //            Log.d(TAG, "azimuth: " + azimuth + ", pitch: " + pitch + ", roll: " + roll);

            radarView.setAzimuth(azimuth);
            radarView.postInvalidate();
        }
    }

    @Override
    public void onClickCharactor(Charactor charactor) {
        CommentActivity.start(getActivity(), charactor);
    }
}
