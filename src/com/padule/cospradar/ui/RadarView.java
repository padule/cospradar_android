package com.padule.cospradar.ui;

import java.util.ArrayList;
import java.util.Collections;
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
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.event.RadarCharactorClickedEvent;
import com.padule.cospradar.event.RadarCharactorDrawedEvent;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.TextUtils;

import de.greenrobot.event.EventBus;

public class RadarView extends View implements OnTouchListener {

    public static final double MAX_RADIUS_KIROMETER = 20.0;
    private static final double MIN_RADIUS_KIROMETER = 0.1;
    public static final int MIN_CHARACTORS_COUNT = 10;
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
    private double maxRadiusKiroMeter = MAX_RADIUS_KIROMETER;
    private double minRadiusKiroMeter = MIN_RADIUS_KIROMETER;

    private LruCache<Integer, Bitmap> bmpCache;
    private ScheduledExecutorService ses;

    private Bitmap emptyBmp;

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initColors(context);
        initBmpCache(context);
        scheduleRefresh();
    }

    public double getMaxRadiusKiroMeter() {
        return this.maxRadiusKiroMeter;
    }

    public double getMinRadiusKiroMeter() {
        return this.minRadiusKiroMeter;
    }

    public double getDefaultRadiusKiroMeter() {
        return getMaxRadiusKiroMeter() / 2;
    }

    public void setMaxRadiusKiroMeter(double maxRadiusKiroMeter) {
        if ((double)maxRadiusKiroMeter >= MAX_RADIUS_KIROMETER) {
            this.maxRadiusKiroMeter = Math.ceil(maxRadiusKiroMeter/10) * 10;
        } else {
            this.maxRadiusKiroMeter = maxRadiusKiroMeter;
        }
    }

    public void setMinRadiusKiroMeter(double minRadiusKiroMeter) {
        this.minRadiusKiroMeter = minRadiusKiroMeter;
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

    private void initBmpCache(Context context) {
        int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        bmpCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bmp) {
                return bmp.getByteCount();
            }
        };
        emptyBmp = ImageUtils.createEmptyIconBmp(RadarView.this.getContext(), ICON_SIZE);
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
        canvas.drawText(TextUtils.getDistanceString(getContext(), getRadiusMeter()), 
                width/2-TEXT_SIZE-TEXT_SIZE/2, width - TEXT_SIZE, paint);
    }

    private void drawBackground(final Canvas canvas) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawCircle(canvas, paint);
        int drawCount = drawCharactors(canvas, paint);

        EventBus.getDefault().post(new RadarCharactorDrawedEvent(drawCount));

        setOnTouchListener(null);
        setOnTouchListener(this);
    }

    private int drawCharactors(final Canvas canvas, Paint paint) {
        int drawCount = 0;
        canvas.save();

        List<Charactor> charas = new ArrayList<Charactor>(charactors);
        Collections.reverse(charas);

        for (final Charactor charactor : charas) {
            if (drawCharactor(canvas, paint, charactor)) {
                drawCount++;
            }
        }
        canvas.scale(scale, scale);
        canvas.restore();

        return drawCount;
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

    private boolean drawCharactor(final Canvas canvas, final Paint paint, final Charactor charactor) {
        int radius = width/2;
        CharactorLocation location = charactor.getLocation();
        if (location == null) {
            return false;
        }

        Bitmap bmp = bmpCache.get(Integer.valueOf(charactor.getId()));
        if (bmp != null) {
            float[] positions = convertLocationToPosition(
                    location.getLatitude(), location.getLongitude());
            float x_limit = positions[0] > 0 ? positions[0] + ICON_SIZE/2 : positions[0] - ICON_SIZE/2;
            float y_limit = positions[1] > 0 ? positions[1] + ICON_SIZE/2 : positions[1] - ICON_SIZE/2;

            if (x_limit*x_limit + y_limit*y_limit < (radius+ICON_SIZE/2)*(radius+ICON_SIZE/2)) {
                int x = (int)(radius+positions[0]-ICON_SIZE/2);
                int y = (int)(radius+positions[1]-ICON_SIZE/2);
                RectF rect = new RectF(x, y, x+ICON_SIZE, y+ICON_SIZE);
                canvas.drawBitmap(bmp, null, rect, paint);
                return true;
            }
        } else {
            bmpCache.put(Integer.valueOf(charactor.getId()), emptyBmp);
            ImageSize targetSize = new ImageSize(ICON_SIZE, ICON_SIZE);
            MainApplication.IMAGE_LOADER.loadImage(charactor.getImageUrl(), targetSize, 
                    new ImageLoadingListener() {
                @Override
                public void onLoadingComplete(String url, View view, Bitmap bmp) {
                    bmpCache.put(Integer.valueOf(charactor.getId()), bmp);
                }
                @Override
                public void onLoadingCancelled(String url, View view) {
                    bmpCache.put(Integer.valueOf(charactor.getId()), emptyBmp);
                }
                @Override
                public void onLoadingFailed(String url, View view, FailReason reason) {
                    bmpCache.put(Integer.valueOf(charactor.getId()), emptyBmp);
                }
                @Override
                public void onLoadingStarted(String url, View view) {
                    //
                }
            });
        }
        return false;
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
        double x = Math.cos(angle) * distance;
        double y = Math.sin(angle) * distance;
        //Log.d(TAG, "distance: " + distance + ", angle: " + angle + ", x: " + x + ", y: " + y);

        float[] coordinates = new float[2];
        coordinates[0] = convertRealCoordinatesToRadar(x);
        coordinates[1] = convertRealCoordinatesToRadar(y);
        //Log.d(TAG, "x: " + coordinates[0] + ", y: " + coordinates[1]);
        return coordinates;
    }

    private float convertRealCoordinatesToRadar(double coordinate) {
        return (float)(coordinate / getRadiusMeter() * width/2);
    }

    private float getRadiusMeter() {
        return (float)getDefaultRadiusKiroMeter() * 1000 / scale;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return touchDetector.onTouchEvent(event);
    }

    private boolean isTouched(float startX, float startY, float endX, float endY, MotionEvent event) {
        return event.getX(0) >= startX
                && event.getY(0) >= startY
                && event.getX(0) <= endX
                && event.getY(0) <= endY;
    }

    public void updateScale(float kirometer) {
        scale = (float)getDefaultRadiusKiroMeter() / kirometer;
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
                    ArrayList<Charactor> results = new ArrayList<Charactor>();

                    for (final Charactor charactor : charactors) {
                        int radius = width/2;
                        CharactorLocation location = charactor.getLocation();
                        float[] positions = convertLocationToPosition(location.getLatitude(), location.getLongitude());

                        if (isTouched(radius+positions[0]-ICON_SIZE/2, radius+positions[1]-ICON_SIZE/2, 
                                radius+positions[0]+ICON_SIZE/2, radius+positions[1]+ICON_SIZE/2, e)) {
                            results.add(charactor);
                        }
                    }
                    if (results != null && !results.isEmpty()) {
                        EventBus.getDefault().post(new RadarCharactorClickedEvent(results));
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
