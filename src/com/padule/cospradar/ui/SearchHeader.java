package com.padule.cospradar.ui;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.activity.CommentActivity;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseLocationListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.ui.RadarView.RadarListener;
import com.padule.cospradar.util.AppUtils;

public class SearchHeader extends RelativeLayout implements RadarListener {

    private static final int MAGNIFICATION = 10;

    @InjectView(R.id.radar_view) RadarView radarView;
    @InjectView(R.id.loading) View loading;
    @InjectView(R.id.seekbar_radar) SeekBar seekbar;

    public SearchHeader(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.ui_header_search, this, true);
        ButterKnife.inject(this);

        initLocationListener();
        initRadar();
        initSeekBar();
    }

    private void initSeekBar() {
        seekbar.setMax((int)(RadarView.MAX_RADIUS_KIROMETER * MAGNIFICATION - RadarView.MIN_RADIUS_KIROMETER * MAGNIFICATION));
        seekbar.setProgress((int)RadarView.DEFAULT_RADIUS_KIROMETER * MAGNIFICATION);;
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += RadarView.MIN_RADIUS_KIROMETER * MAGNIFICATION;
                radarView.updateScale((float)progress/MAGNIFICATION);
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
        this.radarView.setRadarListener(this);
    }

    private void initLocationListener() {
        BaseLocationListener listener = new BaseLocationListener(getContext()) {
            @Override
            public void onLocationChanged(Location loc) {
                super.onLocationChanged(loc);
                if (radarView != null) {
                    AppUtils.setLatLon((float)loc.getLatitude(), (float)loc.getLongitude());
                    loading.setVisibility(View.GONE);
                }
            }
        };
        listener.start();
    }

    @Override
    public void onClickCharactor(Charactor charactor) {
        CommentActivity.start((BaseActivity)getContext(), charactor);
    }

}
