package com.padule.cospradar.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.activity.ProfileActivity;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.dialog.CharactorsDialogFragment;
import com.padule.cospradar.event.RadarCharactorClickedEvent;
import com.padule.cospradar.event.RadarCharactorDrawedEvent;
import com.padule.cospradar.event.SearchBtnClickedEvent;
import com.padule.cospradar.util.AnalyticsUtils;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.TextUtils;

import de.greenrobot.event.EventBus;

public class SearchHeader extends RelativeLayout {

    private static final int MAGNIFICATION = 10;

    @InjectView(R.id.radar_view) public RadarView mRadarView;
    @InjectView(R.id.seekbar_radar) public SeekBar mSeekBar;
    @InjectView(R.id.btn_reload) public ImageButton mBtnReload;
    @InjectView(R.id.edit_search) public EditText mEditSearch;
    @InjectView(R.id.text_count_header) TextView mTextCountHeader;
    @InjectView(R.id.check_realtime) public CheckBox mCheckRealtime;
    @InjectView(R.id.txt_min_distance) TextView mTxtMinDistance;
    @InjectView(R.id.txt_max_distance) TextView mTxtMaxDistance;

    public SearchHeader(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.ui_header_search, this, true);
        ButterKnife.inject(this);
        EventBus.getDefault().register(this);

        initSeekBar();
        initEditSearch();
    }

    @OnClick(R.id.btn_reload)
    void onClickBtnReload() {
        mBtnReload.setEnabled(false);
        String text = mEditSearch.getText().toString();
        EventBus.getDefault().post(new SearchBtnClickedEvent(text, mCheckRealtime.isChecked()));
        AnalyticsUtils.sendEvent(AnalyticsUtils.CATEGORY_RADAR, AnalyticsUtils.EVENT_CLICKED_SEARCH_WITH_TEXT, getContext());
        sendAnalyticsEvent(text);
    }

    private void sendAnalyticsEvent(String text) {
        String event = TextUtils.isEmpty(text) ? AnalyticsUtils.EVENT_CLICKED_SEARCH_WITHOUT_TEXT 
                : AnalyticsUtils.EVENT_CLICKED_SEARCH_WITH_TEXT;
        AnalyticsUtils.sendEvent(AnalyticsUtils.CATEGORY_RADAR, event, getContext());
    }

    private void initEditSearch() {
        Bitmap bmp = ((BitmapDrawable)getContext().getResources().getDrawable(R.drawable.ic_search)).getBitmap();
        Drawable searchIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmp, 50, 50, true));
        mEditSearch.setCompoundDrawablePadding(12);
        mEditSearch.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, null, null);
    }

    public void startSearching() {
        setCharactors(new ArrayList<Charactor>());
        mRadarView.startLoading();
        mTextCountHeader.setVisibility(View.VISIBLE);
        mTextCountHeader.setText(getContext().getString(R.string.searching));
    }

    private void initSeekBar() {
        mSeekBar.setMax((int)(mRadarView.getMaxRadiusKiroMeter() * MAGNIFICATION 
                - mRadarView.getMinRadiusKiroMeter() * MAGNIFICATION));
        mSeekBar.setProgress((int)mRadarView.getDefaultRadiusKiroMeter() * MAGNIFICATION);;
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += mRadarView.getMinRadiusKiroMeter() * MAGNIFICATION;
                mRadarView.updateScale((float)progress/MAGNIFICATION);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });

        mTxtMinDistance.setText(TextUtils.getDistanceString(getContext(), 
                mRadarView.getMinRadiusKiroMeter()*1000));
        mTxtMaxDistance.setText(TextUtils.getDistanceString(getContext(), 
                mRadarView.getMaxRadiusKiroMeter()*1000));
    }

    public void onEvent(RadarCharactorClickedEvent event) {
        ArrayList<Charactor> charactors = event.charactors;
        if (charactors.size() == 1) {
            ProfileActivity.start(getContext(), charactors.get(0).getUser());
        } else {
            CharactorsDialogFragment.show((BaseActivity)getContext(), charactors);
        }
    }

    public void setCharactors(List<Charactor> charactors) {
        if (charactors.isEmpty()) {
            mTextCountHeader.setText(getContext().getString(R.string.search_result_count_zero));
        } else {
            mTextCountHeader.setText(getContext().getString(R.string.search_result_count, charactors.size()));
        }
        mRadarView.setCharactors(charactors);
    }

    public void refresh(List<Charactor> charactors) {
        adjustRadius(charactors);
        initSeekBar();
        setCharactors(charactors);
        mRadarView.stopLoading();
        mBtnReload.setEnabled(true);
    }

    private void adjustRadius(List<Charactor> charactors) {
        if (charactors == null || charactors.isEmpty()) {
            return;
        }

        int checkIdx = RadarView.MIN_CHARACTORS_COUNT - 1;
        if (charactors.size() < RadarView.MIN_CHARACTORS_COUNT) {
            checkIdx = charactors.size() - 1;
        }

        float distance = getDistanceMeter(charactors.get(checkIdx).getLocation());
        if (distance/1000 > RadarView.MAX_RADIUS_KIROMETER) {
            mRadarView.setMaxRadiusKiroMeter(distance/1000*2);
        }
    }

    private float getDistanceMeter(CharactorLocation location) {
        float[] results = new float[3];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), 
                AppUtils.getLatitude(), AppUtils.getLongitude(), results);
        return results[0];
    }

    public void onEvent(RadarCharactorDrawedEvent event) {
        if (mBtnReload.isEnabled()) {
            mTextCountHeader.setText(getContext().getString(R.string.search_result_count, event.drawCount));
        }
    }

    public int[] getRect(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        int[] result = { rect.left, rect.top, rect.right, rect.bottom };
        return result;
    }

}
