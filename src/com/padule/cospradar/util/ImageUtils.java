package com.padule.cospradar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.padule.cospradar.Constants;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;

public class ImageUtils {

    private static final int ACTION_BAR_ICON_SIZE = 100;

    private static final int POS_CHOOSER_GALLERY = 0;
    private static final int POS_CHOOSER_CAMERA = 1;

    private static final String INTENT_IMAGE_TYPE = "image/*";
    private static final String DIR_COSPRADER = "/cosprader";
    private static final String SUFFIX_JPG = ".jpg";
    private static final String SUFFIX_OCFL311 = ".OCFL311";
    private static final String PREFIX_HTTP = "http";
    private static final String PREFIX_JPG = "JPEG_";
    private static final String PREFIX_GALLERY = "content://com.android.gallery3d.provider";
    private static final String PREFIX_CONTENT = "content";
    private static final String PREFIX_FILE = "file";
    private static final String PREFIX_GALLERY_PKG_BEFORE = "com.android.gallery3d";
    private static final String PREFIX_GALLERY_PKG_AFTER = "com.google.android.gallery3d";
    private static final String PREFIX_FILE_PATH = "file://";

    public static String convertToValidUrl(String url) {
        if (url == null) {
            return url;
        } else if (url.startsWith(PREFIX_HTTP)
                || url.startsWith(PREFIX_CONTENT)
                || url.startsWith(PREFIX_FILE)) {
            return url;
        } else {
            return PREFIX_FILE_PATH + url;
        }
    }

    public static DisplayImageOptions roundedImageOptions = 
            new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_no_user)
            .showImageOnFail(R.drawable.ic_no_user_rounded)
            .resetViewBeforeLoading(true)
            .cacheOnDisc(true)
            .cacheInMemory(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new RoundedBitmapDisplayer(100))
            .build();

    public static void displayRoundedImage(String url, ImageView view) {
        MainApplication.IMAGE_LOADER.displayImage(url, view, roundedImageOptions);
    }

    public static void showChooserDialog(final Activity activity) {
        String[] items = activity.getResources().getStringArray(R.array.photo_chooser);

        new AlertDialog.Builder(activity)
        .setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch(which) {
                case POS_CHOOSER_GALLERY:
                    intent = createGalleryIntent(activity);
                    activity.startActivityForResult(intent, Constants.REQ_ACTIVITY_GALLERY);
                    break;
                case POS_CHOOSER_CAMERA:
                    intent = Intent.createChooser(createCameraIntent(), null);
                    activity.startActivityForResult(intent, Constants.REQ_ACTIVITY_CAMERA);
                    break;
                default:
                    break;
                }
            }
        }).create().show();
    }

    private static Intent createGalleryIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(INTENT_IMAGE_TYPE);
        return Intent.createChooser(intent, null);
    }

    private static Intent createCameraIntent() {
        File file = makePhotoFile();
        if (file == null) {
            return null;
        }
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        return intent;

    }

    private static File makePhotoFile() {
        File result = null;
        try {
            String folder = Environment.getExternalStorageDirectory() + DIR_COSPRADER;
            File file = new File(folder);
            file.mkdirs();
            String fileName = PREFIX_JPG + System.nanoTime() + SUFFIX_JPG;
            result = new File(file, fileName);

            PrefUtils.put(PrefUtils.KEY_PHOTO_PATH, result.getAbsolutePath());
            PrefUtils.put(PrefUtils.KEY_PHOTO_NAME, fileName);
        } catch (Exception e) {
            Log.e(ImageUtils.class.getName(), e.getMessage());
        }

        return result;
    }

    public static String getPath(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        if (uri.toString().startsWith(PREFIX_GALLERY)) {
            uri = Uri.parse(uri.toString().replace(PREFIX_GALLERY_PKG_BEFORE, PREFIX_GALLERY_PKG_AFTER));
        }

        if (PREFIX_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA, MediaColumns.DISPLAY_NAME};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null) {
                return null;
            }

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            String path = null;
            if (cursor.moveToFirst()) {
                path = cursor.getString(column_index);
                if (path == null) {
                    path = downloadImage(uri);
                }
            }
            return path;

        } else if (PREFIX_FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String downloadImage(Uri imageUri) {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), SUFFIX_OCFL311);
        } else {
            cacheDir = MainApplication.getContext().getCacheDir();
        }

        if (!cacheDir.exists()) cacheDir.mkdirs();
        File f = new File(cacheDir, UUID.randomUUID().toString() + PREFIX_JPG);
        try {
            InputStream is = MainApplication.getContext().getContentResolver().openInputStream(imageUri);
            @SuppressWarnings("resource")
            OutputStream os = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            return f.getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Bitmap createEmptyIconBmp(Context context, int iconSize) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_no_user_radar);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, iconSize, iconSize, false);
        bmp = null;
        return scaledBmp;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static Bitmap getNotificationLargeIcon(Context context, String iconUrl) {
        Bitmap bmp = null;
        if (!TextUtils.isEmpty(iconUrl)) {
            int height = (int)context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
            int width = (int)context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
            ImageSize isize = new ImageSize(width, height);
            bmp = MainApplication.IMAGE_LOADER.loadImageSync(iconUrl, isize);
        }
        if (bmp == null) {
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app);
        }
        return bmp;
    }

    public static void setActionBarIcon(final Context context, final ActionBar bar, String url) {
        MainApplication.IMAGE_LOADER.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bmp) {
                Bitmap scaledBmp = Bitmap.createScaledBitmap(
                        bmp, ACTION_BAR_ICON_SIZE, ACTION_BAR_ICON_SIZE, false);
                bar.setIcon(new BitmapDrawable(context.getResources(), scaledBmp));
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                setEmptyIcon();
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason reason) {
                setEmptyIcon();
            }
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                setEmptyIcon();
            }

            private void setEmptyIcon() {
                bar.setIcon(new BitmapDrawable(context.getResources(), 
                        ImageUtils.createEmptyIconBmp(context, ACTION_BAR_ICON_SIZE)));
            }
        });
    }

    public static void setOptionItemIcon(final Context context, final MenuItem item, String url) {
        MainApplication.IMAGE_LOADER.loadImage(url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bmp) {
                Bitmap scaledBmp = Bitmap.createScaledBitmap(
                        bmp, ACTION_BAR_ICON_SIZE, ACTION_BAR_ICON_SIZE, false);
                item.setIcon(new BitmapDrawable(context.getResources(), scaledBmp));
            }
        });
    }

}
