package com.padule.cospradar;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.androidquery.util.AQUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.padule.cospradar.service.APIService;

public class MainApplication extends Application 
implements Thread.UncaughtExceptionHandler {

    public static final ImageLoader IMAGE_LOADER = ImageLoader.getInstance();

    private static Gson GSON = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();

    private static RestAdapter REST_ADAPTER = new RestAdapter.Builder()
    .setEndpoint(Constants.APP_URL).setConverter(new GsonConverter(GSON)).build();

    public static APIService API = REST_ADAPTER.create(APIService.class);

    @Override
    public void onCreate() {
        initImageLoader();
        AQUtility.setContext(this);
        AQUtility.setExceptionHandler(this);
        AQUtility.setCacheDir(null);

        super.onCreate();
    }

    public static Context getContext() {
        return AQUtility.getContext();
    }

    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .showImageForEmptyUri(R.drawable.ic_no_photo)
        .showImageOnFail(R.drawable.ic_no_photo)
        .resetViewBeforeLoading(true)
        .cacheOnDisc(true)
        .cacheInMemory(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.EXACTLY)
        .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 3)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 80, null)
                .memoryCache(new LruMemoryCache(8 * 1024 * 1024))
                .memoryCacheSize(16 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        IMAGE_LOADER.init(config);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // TODO Error report;
    }
}
