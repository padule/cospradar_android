package com.padule.cospradar.ui;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.radar.camera.CameraModel;
import com.padule.cospradar.radar.common.Orientation.ORIENTATION;
import com.padule.cospradar.radar.data.ARData;
import com.padule.cospradar.radar.data.ScreenPosition;
import com.padule.cospradar.radar.ui.objects.PaintableCircle;
import com.padule.cospradar.radar.ui.objects.PaintableLine;
import com.padule.cospradar.radar.ui.objects.PaintablePosition;
import com.padule.cospradar.radar.ui.objects.PaintableRadarPoints;
import com.padule.cospradar.radar.ui.objects.PaintableText;

public class RadarView extends View {

    private static final String TAG = RadarView.class.getName();

    private static final int LINE_COLOR = Color.argb(150, 0, 0, 220);
    private static final int RADAR_COLOR = Color.argb(100, 0, 0, 200);
    public static final float RADIUS = 500;
    private static final float PAD_X = 10;
    private static final float PAD_Y = 10;
    private static final int TEXT_COLOR = Color.rgb(255, 255, 255);
    private static final int TEXT_SIZE = 12;

    private static final StringBuilder DIR_TXT = new StringBuilder();
    private static final StringBuilder RADAR_TXT = new StringBuilder();
    private static final StringBuilder DIST_TXT = new StringBuilder();
    private static final StringBuilder DEC_TXT = new StringBuilder();

    private static final int ICON_SIZE = 100;

    private int height;
    private int width;
    private LruCache<Integer, Bitmap> bmpCache;

    private static ScreenPosition leftRadarLine = null;
    private static ScreenPosition rightRadarLine = null;
    private static PaintablePosition leftLineContainer = null;
    private static PaintablePosition rightLineContainer = null;
    private static PaintablePosition circleContainer = null;

    private static PaintableRadarPoints radarPoints = null;
    private static PaintablePosition pointsContainer = null;

    private static PaintableText paintableText = null;
    private static PaintablePosition paintedContainer = null;

    /**
     * Draw the radar on the given Canvas.
     * 
     * @param canvas
     *            Canvas to draw on.
     * @throws NullPointerException
     *             if Canvas is NULL.
     */
    public void draw(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        // Adjust upside down to compensate for zoom-bar
        int ui_ud_pad = 80;
        //        if (AugmentedReality.ui_portrait) 
        //            ui_ud_pad = 55;

        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        //        if (AugmentedReality.useRadarAutoOrientate) {
        orient = ARData.getDeviceOrientation();
        if (orient==ORIENTATION.PORTRAIT) {
            canvas.save();
            canvas.translate(0, canvas.getHeight());
            canvas.rotate(-90);
        } else if (orient==ORIENTATION.PORTRAIT_UPSIDE_DOWN) {
            canvas.save();
            canvas.translate(canvas.getWidth() - ui_ud_pad, 0);
            canvas.rotate(90);
        } else if (orient==ORIENTATION.LANDSCAPE_UPSIDE_DOWN) {
            canvas.save();
            canvas.translate(canvas.getWidth() - ui_ud_pad, canvas.getHeight());
            canvas.rotate(180);
        } else {
            // If landscape, do nothing
        }
        //        }

        // Update the radar graphics and text based upon the new pitch and bearing
        canvas.save();
        canvas.translate(0, 5);
        drawRadarCircle(canvas);
        drawRadarPoints(canvas);
        drawRadarLines(canvas);
        drawRadarText(canvas);
        canvas.restore();

        if (orient!=ORIENTATION.LANDSCAPE)
            canvas.restore();
    }

    private void drawRadarCircle(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        if (circleContainer == null) {
            PaintableCircle paintableCircle = new PaintableCircle(RADAR_COLOR, RADIUS, true);
            circleContainer = new PaintablePosition(paintableCircle, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
        }

        circleContainer.paint(canvas);
    }

    private void drawRadarPoints(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        if (radarPoints == null)
            radarPoints = new PaintableRadarPoints();

        if (pointsContainer == null)
            pointsContainer = new PaintablePosition(radarPoints, 0, 0, 0, 1);
        else
            pointsContainer.set(radarPoints, 0, 0, 0, 1);

        // Rotate the points to match the azimuth
        canvas.save();
        canvas.translate((PAD_X + radarPoints.getWidth() / 2), (PAD_X + radarPoints.getHeight() / 2));
        canvas.rotate(-ARData.getAzimuth());
        canvas.scale(1, 1);
        canvas.translate(-(radarPoints.getWidth() / 2), -(radarPoints.getHeight() / 2));
        pointsContainer.paint(canvas);
        canvas.restore();
    }

    private void drawRadarLines(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        // Left line
        if (leftLineContainer == null) {
            leftRadarLine.set(0, -RADIUS);
            leftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
            leftRadarLine.add(PAD_X + RADIUS, PAD_Y + RADIUS);

            float leftX = leftRadarLine.getX() - (PAD_X + RADIUS);
            float leftY = leftRadarLine.getY() - (PAD_Y + RADIUS);
            PaintableLine leftLine = new PaintableLine(LINE_COLOR, leftX, leftY);
            leftLineContainer = new PaintablePosition(leftLine, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
        }
        leftLineContainer.paint(canvas);

        // Right line
        if (rightLineContainer == null) {
            rightRadarLine.set(0, -RADIUS);
            rightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
            rightRadarLine.add(PAD_X + RADIUS, PAD_Y + RADIUS);

            float rightX = rightRadarLine.getX() - (PAD_X + RADIUS);
            float rightY = rightRadarLine.getY() - (PAD_Y + RADIUS);
            PaintableLine rightLine = new PaintableLine(LINE_COLOR, rightX, rightY);
            rightLineContainer = new PaintablePosition(rightLine, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
        }
        rightLineContainer.paint(canvas);
    }

    private void drawRadarText(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        // Direction text
        int range = (int) (ARData.getAzimuth() / (360f / 16f));
        DIR_TXT.setLength(0);
        if (range == 15 || range == 0)
            DIR_TXT.append("N");
        else if (range == 1 || range == 2)
            DIR_TXT.append("NE");
        else if (range == 3 || range == 4)
            DIR_TXT.append("E");
        else if (range == 5 || range == 6)
            DIR_TXT.append("SE");
        else if (range == 7 || range == 8)
            DIR_TXT.append("S");
        else if (range == 9 || range == 10)
            DIR_TXT.append("SW");
        else if (range == 11 || range == 12)
            DIR_TXT.append("W");
        else if (range == 13 || range == 14)
            DIR_TXT.append("NW");

        int azimuth = (int) ARData.getAzimuth();
        RADAR_TXT.setLength(0);
        RADAR_TXT.append(azimuth).append((char) 176).append(" ").append(DIR_TXT);
        // Azimuth text
        radarText(canvas, RADAR_TXT.toString(), (PAD_X+RADIUS), (PAD_Y-5), true);

        // Zoom text
        radarText(canvas, formatDist(ARData.getRadius() * 1000), (PAD_X+RADIUS), (PAD_Y+(RADIUS*2)-10), false);
    }

    private void radarText(Canvas canvas, String txt, float x, float y, boolean bg) {
        if (canvas == null || txt == null)
            throw new NullPointerException();

        if (paintableText == null)
            paintableText = new PaintableText(txt, TEXT_COLOR, TEXT_SIZE, bg);
        else
            paintableText.set(txt, TEXT_COLOR, TEXT_SIZE, bg);

        if (paintedContainer == null)
            paintedContainer = new PaintablePosition(paintableText, x, y, 0, 1);
        else
            paintedContainer.set(paintableText, x, y, 0, 1);

        paintedContainer.paint(canvas);
    }

    private static String formatDist(float meters) {
        DIST_TXT.setLength(0);
        if (meters < 1000)
            DIST_TXT.append((int) meters).append("m");
        else if (meters < 10000)
            DIST_TXT.append(formatDec(meters / 1000f, 1)).append("km");
        else
            DIST_TXT.append((int) (meters / 1000f)).append("km");
        return DIST_TXT.toString();
    }

    private static String formatDec(float val, int dec) {
        DEC_TXT.setLength(0);
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        DEC_TXT.append(front).append(".").append(back);
        return DEC_TXT.toString();
    }
    //}

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //        initBmpCache(context);
        if (leftRadarLine == null)
            leftRadarLine = new ScreenPosition();
        if (rightRadarLine == null)
            rightRadarLine = new ScreenPosition();
    }

    public RadarView(Context context) {
        super(context);
        if (leftRadarLine == null)
            leftRadarLine = new ScreenPosition();
        if (rightRadarLine == null)
            rightRadarLine = new ScreenPosition();

        //        initBmpCache(context);
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
        drawCircle(canvas);
    }

    private void drawCircle(final Canvas canvas) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(RADAR_COLOR);

        final int radius = width/2;
        canvas.save();
        //        canvas.translate(radius, radius);
        canvas.drawRect(0, 0, width, width, paint);
        //        canvas.drawCircle(0, 0, radius, paint);
        canvas.restore();

        canvas.drawLine(radius, radius, (float)(radius + radius * Math.sin(-direction)),
                (float)(radius - radius * Math.cos(-direction)), paint);

        List<Charactor> charactors = MockFactory.getCharactors();
        final Charactor myCharactor = charactors.get(0);
        Bitmap bmp = bmpCache.get(Integer.valueOf(myCharactor.getId()));
        if (bmp != null) {
            canvas.drawBitmap(bmp, (float)(radius - ICON_SIZE / 2),
                    (float)(radius - ICON_SIZE / 2), paint);
        }

        for (Charactor c : charactors) {
            //            Vector v = convertLocationToPosition(myCharactor)
        }

        //        if (bmp != null) {
        //            canvas.drawBitmap(bmp, (float)(radius + radius * Math.sin(-direction)),
        //                    (float)(radius - radius * Math.cos(-direction)), paint);
        //        }

        ImageSize targetSize = new ImageSize(120, 120);
        MainApplication.imageLoader.loadImage(myCharactor.getImageUrl(), targetSize, 
                new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String url, View view, Bitmap bmp) {
                bmpCache.put(Integer.valueOf(myCharactor.getId()), bmp);
            }
        });
    }

    private float direction;

    public void update(float dir){
        direction = dir;
        postInvalidate();
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

    //    public Vector convertLocationToPosition(float centerLat, float centerLon, Location point) {
    //        float[] x = new float[3];
    //        float y = 0f;
    //        float[] z = new float[3];
    //
    //        if (center == null || point == null) {
    //            return null;
    //        }
    //
    //        Location.distanceBetween(centerLat, centerLon, 
    //                point.getLatitude(), centerLon, z);
    //        Location.distanceBetween(centerLat, centerLon, 
    //                centerLat, point.getLongitude(), x);
    //
    //        y = (float)(point.getAltitude() - center.getAltitude());
    //        if (center.getLatitude() < point.getLatitude()) {
    //            z[0] *= -1;
    //        }
    //        if (center.getLongitude() > point.getLongitude()) {
    //            x[0] *= -1;
    //        }
    //
    //        return new Vector(x[0], (float)y, z[0]);
    //    }

}
