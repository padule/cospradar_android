package com.padule.cospradar.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.fragment.ChatFragment;

public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void initView() {
        Charactor charactor = (Charactor)getIntent()
                .getSerializableExtra(Charactor.class.getName());
        initActionBar(charactor);
        showFragment(ChatFragment.newInstance(charactor), R.id.content_frame);
    }

    private void initActionBar(Charactor charactor) {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(charactor.getNameAndTitle());

        setActionBarIcon(bar, charactor);
    }

    private void setActionBarIcon(final ActionBar bar, Charactor charactor) {
        MainApplication.imageLoader.loadImage(charactor.getImageUrl(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                bar.setIcon(new BitmapDrawable(getResources(), bitmap));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
