package com.padule.cospradar.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
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
import com.padule.cospradar.base.BaseLocationListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.ui.RadarView.RadarListener;
import com.padule.cospradar.util.AppUtils;

public class SearchHeader extends RelativeLayout implements RadarListener {

    private static final int MAGNIFICATION = 10;

    @InjectView(R.id.radar_view) RadarView mRadarView;
    @InjectView(R.id.seekbar_radar) SeekBar mSeekBar;
    @InjectView(R.id.btn_reload) ImageButton mBtnReload;
    @InjectView(R.id.edit_search) EditText mEditSearch;
    @InjectView(R.id.text_count_header) TextView mTextCountHeader;

    private SearchListener listener;

    public SearchHeader(Context context, SearchListener listener) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.ui_header_search, this, true);
        ButterKnife.inject(this);

        this.listener = listener;
        initLocationListener();
        initRadar();
        initSeekBar();
    }

    public interface SearchListener {
        public void onClickBtnReload(String searchText);
    }

    @OnClick(R.id.btn_reload)
    void onClickBtnReload() {
        if (listener != null) {
            String text = mEditSearch.getText().toString();
            listener.onClickBtnReload(text);
        }
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

    private void initRadar() {
        this.mRadarView.setRadarListener(this);
    }

    private void initLocationListener() {
        BaseLocationListener listener = new BaseLocationListener(getContext()) {
            @Override
            public void onLocationChanged(Location loc) {
                super.onLocationChanged(loc);
                if (mRadarView != null) {
                    AppUtils.setLatLon((float)loc.getLatitude(), (float)loc.getLongitude());
                }
            }
        };
        listener.start();
    }

    @Override
    public void onClickCharactor(Charactor charactor) {
        ProfileActivity.start(getContext(), charactor.getUser());
    }

    public void setCharactors(List<Charactor> charactors) {
        mTextCountHeader.setText(getContext().getString(R.string.search_result_count, charactors.size()));
        mRadarView.setCharactors(charactors);
    }

    public void refresh(List<Charactor> charactors) {
        setCharactors(charactors);
        mRadarView.stopLoading();
    }


}
