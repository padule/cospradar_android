package com.padule.cospradar.fragment;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.radar.data.ARData;
import com.padule.cospradar.radar.data.GooglePlacesDataSource;
import com.padule.cospradar.radar.data.LocalDataSource;
import com.padule.cospradar.radar.data.NetworkDataSource;
import com.padule.cospradar.radar.data.WikipediaDataSource;
import com.padule.cospradar.radar.ui.Marker;
import com.padule.cospradar.ui.AugmentedView;

public class SearchRadarFragment extends SensorsFragment implements SensorEventListener {

    private static final String TAG = SearchRadarFragment.class.getName();
    private static final Map<String, NetworkDataSource> sources = new ConcurrentHashMap<String, NetworkDataSource>();
    private static final String locale = Locale.getDefault().getLanguage();
    private static final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor exeService = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, queue);

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    private float[] valuesAccelerometer = new float[3];;
    private float[] valuesMagneticField = new float[3];;
    private float[] matrixR = new float[16];
    private float[] matrixI = new float[16];
    private float[] matrixValues = new float[3];

    @InjectView(R.id.radar_view) AugmentedView mRadarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_radar, container, false);
        ButterKnife.inject(this, view);

        // Local
        LocalDataSource localData = new LocalDataSource(this.getResources());
        ARData.addMarkers(localData.getMarkers());

        NetworkDataSource wikipedia = new WikipediaDataSource(this.getResources());
        sources.put("wiki", wikipedia);
        NetworkDataSource googlePlaces = new GooglePlacesDataSource(this.getResources());
        sources.put("googlePlaces", googlePlaces);
        
        Log.d(TAG, "sources: " + sources.size());
        initSensors();
        return view;
    }

    @Override
    public void onResume() {
        sensorManager.registerListener(this, sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagneticField,
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(this, sensorAccelerometer);
        sensorManager.unregisterListener(this, sensorMagneticField);
        super.onPause();
    }

    private void initSensors() {
        sensorManager = (SensorManager)getActivity().getSystemService(Activity.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mRadarView.postInvalidate();
        }
    }

    //    @Override
    //    public void onSensorChanged(SensorEvent event) {
    //        switch(event.sensor.getType()){
    //        case Sensor.TYPE_ACCELEROMETER:
    //            valuesAccelerometer = event.values.clone();
    //            break;
    //        case Sensor.TYPE_MAGNETIC_FIELD:
    //            valuesMagneticField = event.values.clone();
    //            break;
    //        }
    //
    //        boolean success = SensorManager.getRotationMatrix(
    //                matrixR, matrixI, valuesAccelerometer, valuesMagneticField);
    //
    //        if (success) {
    //            SensorManager.getOrientation(matrixR, matrixValues);
    ////            mRadarView.update(matrixValues[0]);
    //        }
    //    }

    private void updateData(final double lat, final double lon, final double alt) {
        try {
            exeService.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "updateData: " + sources.size());
                    for (NetworkDataSource source : sources.values())
                        download(source, lat, lon, alt);
                }
            });
        } catch (RejectedExecutionException rej) {
            //            Log.w(TAG, "Not running new download Runnable, queue is full.");
        } catch (Exception e) {
            //            Log.e(TAG, "Exception running download Runnable.", e);
        }
    }

    private static boolean download(NetworkDataSource source, double lat, double lon, double alt) {
        if (source == null) return false;

        String url = null;
        try {
            url = source.createRequestURL(lat, lon, alt, ARData.getRadius(), locale);
        } catch (NullPointerException e) {
            Log.e(TAG, "download_error: " + e.getMessage());
            return false;
        }

        List<Marker> markers = null;
        try {
            markers = source.parse(url);
        } catch (NullPointerException e) {
            Log.e(TAG, "download_error: " + e.getMessage());
            return false;
        }

        ARData.addMarkers(markers);
        Log.d(TAG, "addMarkers: " + markers.size());
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        Location last = ARData.getCurrentLocation();
        Log.d(TAG, "lat: " + last.getLatitude() + ", lon: " + last.getLatitude() + ", alt: " + last.getAltitude());
        updateData(last.getLatitude(), last.getLongitude(), last.getAltitude());
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        updateData(location.getLatitude(), location.getLongitude(), location.getAltitude());
    }

}
