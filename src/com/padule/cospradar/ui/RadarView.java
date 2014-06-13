package com.padule.cospradar.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.TextUtils;

public class RadarView extends View implements OnTouchListener {

    private static final String TAG = RadarView.class.getName();

    public static final double DEFAULT_RADIUS_KIROMETER = 10.0;
    public static final double MAX_RADIUS_KIROMETER = 20.0;
    public static final double MIN_RADIUS_KIROMETER = 0.1;
    private static final float DEFAULT_RADIUS_METER = (float)DEFAULT_RADIUS_KIROMETER * 1000;
    private static final int ICON_SIZE = 100;
    private static final int TEXT_SIZE = 40;
    private static final int RADAR_STROKE_WIDTH = 6;
    private static final double DEG2RAD = Math.PI/180;
    private static final int REFRESH_DELAY_MILLS = 3 * 1000;
    private static final float DEFAULT_LOADING_ANGLE = 240;

    private List<Charactor> charactors = new ArrayList<Charactor>();

    private int height;
    private int width;
    private float azimuth;
    private int bgColor;
    private int strokeColor;
    private int centerColor;
    private float scale = 1.0f;
    private float loadingAngle = DEFAULT_LOADING_ANGLE;
    private boolean isLoading = false;

    private LruCache<Integer, Bitmap> bmpCache;
    private RadarListener radarListener;
    private ScheduledExecutorService ses;

    public interface RadarListener {
        public void onClickCharactor(Charactor charactor);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initColors(context);
        initBmpCache(context);
        scheduleRefresh();
    }

    private void initColors(Context context) {
        bgColor = context.getResources().getColor(R.color.bg_radar);
        strokeColor = context.getResources().getColor(R.color.stroke_radar);
        centerColor = context.getResources().getColor(R.color.center_radar);
    }

    private void scheduleRefresh() {
        if (mTimer != null){
            mTimer.cancel();
        }
        timerTask = new RefreshTimerTask();
        mTimer = new Timer(true);
        mTimer.schedule(timerTask, REFRESH_DELAY_MILLS, REFRESH_DELAY_MILLS);
    }

    RefreshTimerTask timerTask = null;
    Timer mTimer = null;
    Handler mHandler = new Handler();
    private class RefreshTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post( new Runnable() {
                public void run() {
                    RadarView.this.postInvalidate();
                }
            });
        }
    }

    public void setRadarListener(RadarListener radarListener) {
        this.radarListener = radarListener;
    }

    private void initBmpCache(Context context) {
        int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        bmpCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bmp) {
                return bmp.getByteCount();
            }
        };
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (canvas == null) {
            return;
        }
        drawRadar(canvas);
    }

    private void drawRadar(final Canvas canvas) {
        if (isLoading) {
            drawSearching(canvas);
        }
        drawBackground(canvas);
        drawText(canvas);
    }

    private void drawSearching(final Canvas canvas) {
        canvas.save();
        Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.loading_radar));
        RectF arc = new RectF(0, 0, width, width);
        canvas.drawArc(arc, loadingAngle, 60, true, paint);
        canvas.restore();
    }

    private final Runnable loadingTask = new Runnable(){
        @Override
        public void run() {
            loadingAngle += 1;
            postInvalidate();
        }
    };

    public void startLoading() {
        isLoading = true;
        loadingAngle = DEFAULT_LOADING_ANGLE;
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(loadingTask, 0L, 3, TimeUnit.MILLISECONDS);
    }

    public void stopLoading() {
        isLoading = false;
        if (ses != null) {
            ses.shutdown();
            ses = null;
        }
        loadingAngle = DEFAULT_LOADING_ANGLE;
    }

    private void drawText(final Canvas canvas) {
        Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(getResources().getColor(R.color.text_white));
        canvas.drawText(TextUtils.getKiroMeterString(getContext(), getRadiusMeter()), 
                width/2-TEXT_SIZE-TEXT_SIZE/2, width - TEXT_SIZE, paint);
    }

    private void drawBackground(final Canvas canvas) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawCircle(canvas, paint);
        drawCharactors(canvas, paint);

        setOnTouchListener(null);
        setOnTouchListener(this);
    }

    private void drawCharactors(final Canvas canvas, Paint paint) {
        canvas.save();
        for (final Charactor charactor : charactors) {
            drawCharactor(canvas, paint, charactor);
        }
        canvas.scale(scale, scale);
        canvas.restore();
    }

    private void drawCircle(final Canvas canvas, Paint paint) {
        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgColor);
        final int radius = width / 2;
        canvas.drawCircle(radius, radius, radius, paint);
        canvas.scale(scale, scale);
        canvas.restore();

        canvas.save();
        int strokeCount = 3;
        for (int i = 1; i < strokeCount + 1; i++) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(strokeColor);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(RADAR_STROKE_WIDTH);
            int r = radius/strokeCount * i;
            canvas.drawCircle(radius, radius, r, p);
        }

        paint.setColor(centerColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(radius, radius, 24, paint);
    }

    private void drawCharactor(final Canvas canvas, final Paint paint, final Charactor charactor) {
        int radius = width/2;
        CharactorLocation location = charactor.getLocation();
        if (location == null) {
            return;
        }

        Bitmap bmp = bmpCache.get(Integer.valueOf(charactor.getId()));
        if (bmp != null) {
            float[] positions = convertLocationToPosition(
                    location.getLatitude(), location.getLongitude());
            float x_limit = positions[0] > 0 ? positions[0] + ICON_SIZE/2 : positions[0] - ICON_SIZE/2;
            float y_limit = positions[1] > 0 ? positions[1] + ICON_SIZE/2 : positions[1] - ICON_SIZE/2;

            if (x_limit*x_limit + y_limit*y_limit < radius*radius) {
                canvas.drawBitmap(bmp, radius + positions[0] - ICON_SIZE/2, 
                        radius + positions[1] - ICON_SIZE/2, paint);
            }
        } else {
            ImageSize targetSize = new ImageSize(ICON_SIZE, ICON_SIZE);
            MainApplication.imageLoader.loadImage(charactor.getImageUrl(), targetSize, 
                    new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String url, View view, Bitmap bmp) {
                    bmpCache.put(Integer.valueOf(charactor.getId()), bmp);
                }
            });
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = width;
        if (width < getSuggestedMinimumHeight()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(width, height);
        }
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    private float[] convertLocationToPosition(float lat, float lon) {
        float[] results = new float[3];

        Location.distanceBetween(lat, lon, AppUtils.getLatitude(), AppUtils.getLongitude(), results);

        float distance = results[0];
        double angle = (results[1] - azimuth) * DEG2RAD;
        double x = Math.cos(angle) * distance + ICON_SIZE/2;
        double y = Math.sin(angle) * distance + ICON_SIZE/2;
        //        Log.d(TAG, "distance: " + distance + ", angle: " + angle + ", x: " + x + ", y: " + y);

        float[] coordinates = new float[2];
        coordinates[0] = convertRealCoordinatesToRadar(x);
        coordinates[1] = convertRealCoordinatesToRadar(y);
        //        Log.d(TAG, "x: " + coordinates[0] + ", y: " + coordinates[1]);
        return coordinates;
    }

    private float convertRealCoordinatesToRadar(double coordinate) {
        return (float)(coordinate / getRadiusMeter() * width);
    }

    private float getRadiusMeter() {
        return DEFAULT_RADIUS_METER / scale;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return touchDetector.onTouchEvent(event);
    }

    private void setTouchEvent(float startX, float startY, float endX, float endY, 
            MotionEvent event, Charactor charactor) {
        if (event.getX(0) >= startX
                && event.getY(0) >= startY
                && event.getX(0) <= endX
                && event.getY(0) <= endY) {
            Log.e(TAG, "charactor_id: " + charactor.getId() + ", name: " + charactor.getName());
            if (radarListener != null) {
                radarListener.onClickCharactor(charactor);
            }
        }
    }

    public void updateScale(float kirometer) {
        scale = (float)DEFAULT_RADIUS_KIROMETER / kirometer;
        postInvalidate();
    }

    public void setCharactors(List<Charactor> charactors) {
        this.charactors = charactors;
        postInvalidate();
    }

    private GestureDetector touchDetector =
            new GestureDetector(RadarView.this.getContext(), new OnGestureListener(){
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }
                @Override
                public void onShowPress(MotionEvent e) {
                    //
                }
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    for (final Charactor charactor : charactors) {
                        int radius = width/2;
                        CharactorLocation location = charactor.getLocation();
                        float[] positions = convertLocationToPosition(location.getLatitude(), location.getLongitude());
                        setTouchEvent(radius + positions[0] - ICON_SIZE/2, radius + positions[1] - ICON_SIZE/2, 
                                radius + positions[0] + ICON_SIZE/2, radius + positions[1] + ICON_SIZE/2, 
                                e, charactor);
                    }
                    return true;
                }
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                        float distanceX, float distanceY) {
                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e) {
                    //
                }
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, 
                        float velocityX, float velocityY) {
                    return false;
                }
            });

}
