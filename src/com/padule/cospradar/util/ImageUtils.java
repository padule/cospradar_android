package com.padule.cospradar.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.aviary.android.feather.FeatherActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.padule.cospradar.Constants;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;

public class ImageUtils {

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

    private static final String[] AVIARY_TOOLS_LIST = {
        "EFFECTS", "CROP", "BRIGHTNESS", "CONTRAST", "DRAWING", "TEXT", "RED_EYE", "WHITEN"
    };

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

    public static void showChooserDialog(final Fragment fragment) {
        String[] items = fragment.getResources().getStringArray(R.array.photo_chooser);

        new AlertDialog.Builder(fragment.getActivity())
        .setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                switch(which) {
                case POS_CHOOSER_GALLERY:
                    intent = createGalleryIntent(fragment.getActivity());
                    fragment.startActivityForResult(intent, Constants.REQ_ACTIVITY_GALLERY);
                    break;
                case POS_CHOOSER_CAMERA:
                    intent = Intent.createChooser(createCameraIntent(), null);
                    fragment.startActivityForResult(intent, Constants.REQ_ACTIVITY_CAMERA);
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

    public static Intent createAviaryIntent(Context context, Uri uri) {
        Intent intent = new Intent(context, FeatherActivity.class);
        intent.setData(uri);
        intent.putExtra(com.aviary.android.feather.library.Constants.EXTRA_IN_API_KEY_SECRET,
                Constants.AVIARY_SECRET);
        intent.putExtra(com.aviary.android.feather.library.Constants.EXTRA_TOOLS_LIST, AVIARY_TOOLS_LIST);
        return intent;
    }

}
