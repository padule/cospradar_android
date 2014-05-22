package com.padule.cospradar.util;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;

public class ImageUtils {

    private static DisplayImageOptions roundedImageOptions = 
            new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_no_user)
            .showImageOnFail(R.drawable.ic_no_user)
            .resetViewBeforeLoading(true)
            .cacheOnDisc(true)
            .cacheInMemory(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new RoundedBitmapDisplayer(300))
            .build();

    public static void displayRoundedImage(String url, ImageView view) {
        MainApplication.imageLoader.displayImage(url, view, roundedImageOptions);
    }

}
