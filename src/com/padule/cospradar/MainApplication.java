package com.padule.cospradar;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


public class MainApplication extends Application 
implements Thread.UncaughtExceptionHandler {

    public static final ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
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
                .denyCacheImageMultipleSizesInMemory()
                .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 80, null)
                .memoryCache(new LruMemoryCache(8 * 1024 * 1024))
                .memoryCacheSize(16 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        imageLoader.init(config);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // TODO Error report;
    }
}
