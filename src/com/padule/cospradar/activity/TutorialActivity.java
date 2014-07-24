package com.padule.cospradar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.TutorialPagerAdapter;
import com.padule.cospradar.event.PhotoChosenEvent;
import com.padule.cospradar.event.TutorialBackBtnClickedEvent;
import com.padule.cospradar.event.TutorialCardFinishedEvent;
import com.padule.cospradar.event.TutorialPageMoveEvent;
import com.padule.cospradar.ui.HoldableViewPager;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.PrefUtils;
import com.viewpagerindicator.CirclePageIndicator;

import de.greenrobot.event.EventBus;

public class TutorialActivity extends FragmentActivity {

    private static final String EXTRA_RECT_BTN_SEARCH = "extra_rect_btn_search";
    private static final String EXTRA_RECT_EDIT_SEARCH = "extra_rect_edit_search";
    private static final String EXTRA_RECT_CHECK_REALTIME = "extra_rect_check_realtime";
    private static final String EXTRA_RECT_SEEK_BAR = "extra_rect_seek_bar";

    @InjectView(R.id.view_pager) HoldableViewPager mViewPager;
    @InjectView(R.id.indicator) CirclePageIndicator mIndicator;
    @InjectView(R.id.container_seethrough) RelativeLayout mContainerSeethrough;
    @InjectView(R.id.container_card) RelativeLayout mContainerCard;
    @InjectView(R.id.frame) RelativeLayout mFrame;
    @InjectView(R.id.container_description) LinearLayout mContainerDescription;
    @InjectView(R.id.txt_description) TextView mTxtDescription;
    @InjectView(R.id.btn_done) TextView mBtnDone;

    private PagerAdapter adapter;
    private int idxSeeThroughTutorial = 0;

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

    public void onEvent(TutorialCardFinishedEvent event) {
        PrefUtils.put(PrefUtils.KEY_TUTORIAL_SHOWN, true);
        showSeeThrough();
    }

    private void showSeeThrough() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.tutorial_close_translate);
        anim.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                mContainerCard.setVisibility(View.GONE);
                mContainerCard.setEnabled(false);
                mIndicator.setEnabled(false);
                mIndicator.setOnTouchListener(null);
                mIndicator.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                //
            }
            @Override
            public void onAnimationStart(Animation animation) {
                mContainerSeethrough.setVisibility(View.VISIBLE);
            }
        });
        mContainerCard.startAnimation(anim);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (adapter != null) {
            adapter = null;
        }
    }

    public static void start(Context context, int[] rectBtnSearch, 
            int[] rectEditSearch, int[] rectCheckRealtime, int[] rectSeekBar) {
        if (PrefUtils.getBoolean(PrefUtils.KEY_TUTORIAL_SHOWN, false)) return;

        Intent intent = new Intent(context, TutorialActivity.class);
        intent.putExtra(EXTRA_RECT_BTN_SEARCH, rectBtnSearch);
        intent.putExtra(EXTRA_RECT_EDIT_SEARCH, rectEditSearch);
        intent.putExtra(EXTRA_RECT_CHECK_REALTIME, rectCheckRealtime);
        intent.putExtra(EXTRA_RECT_SEEK_BAR, rectSeekBar);
        context.startActivity(intent);
    }

    private void initView() {
        initPager();
        initSeeThrough();
    }

    private void initSeeThrough() {
        showTutorial(EXTRA_RECT_BTN_SEARCH, getString(R.string.tutorial_seethrough1), getString(R.string.next));
    }

    @OnClick(R.id.btn_done)
    void onClickBtnDone() {
        switch(idxSeeThroughTutorial) {
        case 1:
            showTutorial(EXTRA_RECT_EDIT_SEARCH, getString(R.string.tutorial_seethrough2), getString(R.string.next));
            break;
        case 2:
            showTutorial(EXTRA_RECT_CHECK_REALTIME, getString(R.string.tutorial_seethrough3), getString(R.string.next));
            break;
        case 3:
            showTutorial(EXTRA_RECT_SEEK_BAR, getString(R.string.tutorial_seethrough4), getString(R.string.done));
            break;
        case 4:
            finish();
            break;
        }
    }

    private void showTutorial(String extraName, String text, String btnText) {
        int[] rect = getIntent().getIntArrayExtra(extraName);

        setBackground(mFrame, R.drawable.bg_tutorial_frame);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                rect[2]-rect[0], rect[3]-rect[1]+60);
        params.setMargins(rect[0], rect[1]-104, 0, 0);
        mFrame.setLayoutParams(params);

        RelativeLayout.LayoutParams txtParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        txtParams.setMargins(0, rect[1]-500, 0, 0);
        mContainerDescription.setLayoutParams(txtParams);

        mTxtDescription.setText(text);
        mBtnDone.setText(btnText);
        idxSeeThroughTutorial++;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void setBackground(View view, int drawableResId) {
        Drawable drawable = getResources().getDrawable(drawableResId);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mFrame.setBackgroundDrawable(drawable);
        } else {
            mFrame.setBackground(drawable);
        }

    }

    private void initPager() {
        adapter = new TutorialPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setOnTouchListener(null);
        mIndicator.setEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null) return;

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
        EventBus.getDefault().post(new TutorialBackBtnClickedEvent());
        finish();
        super.onBackPressed();
    }

}
