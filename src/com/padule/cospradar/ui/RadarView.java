package com.padule.cospradar.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.mock.MockFactory;

public class RadarView extends View implements OnTouchListener {

    private static final String TAG = RadarView.class.getName();

    private static final double DEFAULT_RADIUS_KIROMETER = 10.0;
    private static final double MAX_RADIUS_KIROMETER = 100.0;
    private static final double MIN_RADIUS_KIROMETER = 0.1;
    private static final float DEFAULT_RADIUS_METER = (float)DEFAULT_RADIUS_KIROMETER * 1000;
    private static final int ICON_SIZE = 100;
    private static final int TEXT_SIZE = 40;
    private static final double DEG2RAD = Math.PI/180;

    private List<Charactor> charactors = new ArrayList<Charactor>();

    private int height;
    private int width;
    private float azimuth;
    private int color;
    private float scale = 1.0f;

    private float centerLat;
    private float centerLon;

    private LruCache<Integer, Bitmap> bmpCache;
    private RadarListener radarListener;

    public interface RadarListener {
        public void onClickCharactor(Charactor charactor);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        color = context.getResources().getColor(R.color.apptheme_color);
        initBmpCache(context);
    }

    public RadarView(Context context) {
        super(context);
        color = context.getResources().getColor(R.color.apptheme_color);
        initBmpCache(context);
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
        drawBackground(canvas);
        drawText(canvas);
    }

    private void drawText(final Canvas canvas) {
        Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(getResources().getColor(R.color.text_white));
        canvas.drawText(getResources().getString(R.string.radar_distance, getRadiusKiroMeterString()), 
                width/2-TEXT_SIZE-TEXT_SIZE/2, width - TEXT_SIZE, paint);
    }

    private void drawArc(final Canvas canvas) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((int)(color * 1.1));
        RectF arc = new RectF(0, 0, width, width);
        canvas.drawArc(arc, 240, 60, true, paint);
    }

    private void drawBackground(final Canvas canvas) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);

        final int radius = width / 2;
        canvas.save();
        canvas.drawCircle(radius, radius, radius, paint);
        drawArc(canvas);
        canvas.scale(scale, scale);
        canvas.restore();

        // TODO Remove mock code
        charactors = MockFactory.getCharactors();

        canvas.save();
        for (final Charactor charactor : charactors) {
            drawCharactor(canvas, paint, charactor);
        }
        canvas.scale(scale, scale);

        setOnTouchListener(null);
        setOnTouchListener(this);

        canvas.restore();
    }

    private void drawCharactor(final Canvas canvas, final Paint paint, final Charactor charactor) {
        int radius = width/2;
        Bitmap bmp = bmpCache.get(Integer.valueOf(charactor.getId()));
        if (bmp != null) {
            float[] positions = convertLocationToPosition(
                    charactor.getLocation().getLatitude(), charactor.getLocation().getLongitude());
            canvas.drawBitmap(bmp, radius + positions[0] - ICON_SIZE/2, 
                    radius + positions[1] - ICON_SIZE/2, paint);
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

        Location.distanceBetween(lat, lon, centerLat, centerLon, results);

        float distance = results[0];
        double angle = (results[1] - azimuth) * DEG2RAD;
        double x = Math.cos(angle) * distance + ICON_SIZE/2;
        double y = Math.sin(angle) * distance + ICON_SIZE/2;
        Log.d(TAG, "distance: " + distance + ", angle: " + angle + ", x: " + x + ", y: " + y);

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

    private float getRadiusKiroMeter() {
        return getRadiusMeter() / 1000;
    }

    private String getRadiusKiroMeterString() {
        double kiro = Math.floor(getRadiusMeter() / 1000 * 10) / 10;
        return String.valueOf(kiro);
    }

    public void setCenterLocation(float centerLat, float centerLon) {
        this.centerLat = centerLat;
        this.centerLon = centerLon;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleDetector.onTouchEvent(event);
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

    private void checkMinMaxScale() {
        if (RadarView.this.getRadiusKiroMeter() < MIN_RADIUS_KIROMETER) {
            scale = (float)(DEFAULT_RADIUS_KIROMETER / MIN_RADIUS_KIROMETER);
        } else if (RadarView.this.getRadiusKiroMeter() > MAX_RADIUS_KIROMETER) {
            scale = (float)(DEFAULT_RADIUS_KIROMETER / MAX_RADIUS_KIROMETER);
        }
    }

    private ScaleGestureDetector scaleDetector = 
            new ScaleGestureDetector(RadarView.this.getContext(), 
                    new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    scale *= detector.getScaleFactor();
                    checkMinMaxScale();
                    invalidate();
                    return super.onScaleBegin(detector);
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    scale *= detector.getScaleFactor();
                    checkMinMaxScale();
                    invalidate();
                    super.onScaleEnd(detector);
                }

                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    scale *= detector.getScaleFactor();
                    checkMinMaxScale();
                    invalidate();
                    return true;
                };
            });

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
