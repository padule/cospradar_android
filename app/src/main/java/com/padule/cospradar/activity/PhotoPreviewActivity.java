
package com.padule.cospradar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;

public class PhotoPreviewActivity extends FragmentActivity {

    @InjectView(R.id.img_preview) ImageView mImgPreview;
    @InjectView(R.id.root_preview) View mRoot;
    @InjectView(R.id.txt_name) TextView mTxtName;
    @InjectView(R.id.txt_title) TextView mTxtTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        ButterKnife.inject(this);
        initView();
    }

    @OnClick(R.id.root_preview)
    void onClickRootPreview() {
        finish();
    }

    public static void start(Activity activity, Charactor charactor) {
        Intent intent = new Intent(activity, PhotoPreviewActivity.class);
        intent.putExtra(Charactor.class.getName(), charactor);
        activity.startActivity(intent);
    }

    public void initView() {
        Charactor charactor = (Charactor)getIntent()
                .getSerializableExtra(Charactor.class.getName());
        if (charactor.getImageUrl() != null) {
            MainApplication.IMAGE_LOADER.displayImage(charactor.getImageUrl(), mImgPreview);
        }
        mTxtName.setText(charactor.getName());
        mTxtTitle.setText(charactor.getTitle());
    }
}
