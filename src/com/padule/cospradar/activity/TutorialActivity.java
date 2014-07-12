package com.padule.cospradar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.TutorialPagerAdapter;
import com.padule.cospradar.event.PhotoChosenEvent;
import com.padule.cospradar.event.TutorialPageMoveEvent;
import com.padule.cospradar.ui.HoldableViewPager;
import com.padule.cospradar.util.ImageUtils;
import com.viewpagerindicator.CirclePageIndicator;

import de.greenrobot.event.EventBus;

public class TutorialActivity extends FragmentActivity {

    @InjectView(R.id.view_pager) HoldableViewPager mViewPager;
    @InjectView(R.id.indicator) CirclePageIndicator mIndicator;

    private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);
        initView();
    }

    public void onEvent(TutorialPageMoveEvent event) {
        mViewPager.setCurrentItem(event.pos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (adapter != null) {
            adapter = null;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, TutorialActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        initPager();
    }

    private void initPager() {
        adapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();

        switch (requestCode) {
        case Constants.REQ_ACTIVITY_CAMERA:
            if (resultCode == Activity.RESULT_OK) {
                Intent i = ImageUtils.createAviaryIntent(this, uri);
                startActivityForResult(i, Constants.REQ_ACTIVITY_AVIARY_CAMERA);
            }
            break;
        case Constants.REQ_ACTIVITY_GALLERY:
            if (resultCode == Activity.RESULT_OK) {
                Intent i = ImageUtils.createAviaryIntent(this, uri);
                startActivityForResult(i, Constants.REQ_ACTIVITY_AVIARY_GALLERY);
            }
            break;
        case Constants.REQ_ACTIVITY_AVIARY_CAMERA:
        case Constants.REQ_ACTIVITY_AVIARY_GALLERY:
            if (resultCode == Activity.RESULT_OK) {
                EventBus.getDefault().post(new PhotoChosenEvent(uri));
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        //
    }

}
