
package com.padule.cospradar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;

public class PhotoPreviewActivity extends BaseActivity {

    public static final String EXTRA_PHOTO_URL = "photo_url";

    private String imageUrl;

    @InjectView(R.id.img_preview) ImageView mImgPreview;
    @InjectView(R.id.root_preview) View mRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getIntent().getStringExtra(EXTRA_PHOTO_URL);

        this.setTheme(R.style.PhotoPreview);
        setContentView(R.layout.activity_photo_preview);
    }

    @OnClick(R.id.root_preview)
    void onClickRootPreview() {
        finish();
    }

    public static void start(Activity activity, String imageUrl) {
        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putExtra(EXTRA_PHOTO_URL, imageUrl);
        activity.startActivity(intent);
    }

    @Override
    public void initView() {
        if (imageUrl == null) {
            return;
        }
        MainApplication.IMAGE_LOADER.displayImage(imageUrl, mImgPreview);
    }
}
