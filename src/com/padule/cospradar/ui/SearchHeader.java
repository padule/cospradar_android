package com.padule.cospradar.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
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
import com.padule.cospradar.event.RadarCharactorClickedEvent;
import com.padule.cospradar.event.RadarCharactorDrawedEvent;
import com.padule.cospradar.event.SearchBtnClickedEvent;
import com.padule.cospradar.fragment.CharactorsDialogFragment;

import de.greenrobot.event.EventBus;

public class SearchHeader extends RelativeLayout {

    private static final int MAGNIFICATION = 10;

    @InjectView(R.id.radar_view) RadarView mRadarView;
    @InjectView(R.id.seekbar_radar) SeekBar mSeekBar;
    @InjectView(R.id.btn_reload) ImageButton mBtnReload;
    @InjectView(R.id.edit_search) EditText mEditSearch;
    @InjectView(R.id.text_count_header) TextView mTextCountHeader;

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
        EventBus.getDefault().post(new SearchBtnClickedEvent(text));
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
        mSeekBar.setMax((int)(RadarView.MAX_RADIUS_KIROMETER * MAGNIFICATION - RadarView.MIN_RADIUS_KIROMETER * MAGNIFICATION));
        mSeekBar.setProgress((int)RadarView.DEFAULT_RADIUS_KIROMETER * MAGNIFICATION);;
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += RadarView.MIN_RADIUS_KIROMETER * MAGNIFICATION;
                mRadarView.updateScale((float)progress/MAGNIFICATION);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });
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
        setCharactors(charactors);
        mRadarView.stopLoading();
        mBtnReload.setEnabled(true);
    }

    public void onEvent(RadarCharactorDrawedEvent event) {
        if (mBtnReload.isEnabled()) {
            mTextCountHeader.setText(getContext().getString(R.string.search_result_count, event.drawCount));
        }
    }

}
