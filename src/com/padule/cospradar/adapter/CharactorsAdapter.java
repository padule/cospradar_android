package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseArrayAdapter;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorLocation;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.TextUtils;

public class CharactorsAdapter extends BaseArrayAdapter<Charactor> {

    private static final String TAG = CharactorsAdapter.class.getName();

    public CharactorsAdapter(Context context) {
        this(context, new ArrayList<Charactor>());
    }

    public CharactorsAdapter(Context context, List<Charactor> charactors) {
        super(context, R.layout.item_charactor, charactors);
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_charactor, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        Charactor charactor = getItem(pos);
        holder.bindData(charactor, context);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.img_icon) ImageView mImgIcon;
        @InjectView(R.id.txt_name) TextView mTxtName;
        @InjectView(R.id.txt_title) TextView mTxtTitle;
        @InjectView(R.id.txt_distance) TextView mTxtDistance;
        @InjectView(R.id.txt_user_name) TextView mTxtUserName;
        @InjectView(R.id.txt_charactor_enabled) TextView mTxtCharactorEnabled;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void bindData(Charactor charactor, Context context) {
            if (charactor != null) {
                ImageUtils.displayRoundedImage(charactor.getImageUrl(), mImgIcon);
                mTxtName.setText(charactor.getName());
                mTxtTitle.setText(charactor.getTitle());

                int visibility = charactor.isEnabled() ? View.VISIBLE : View.GONE;
                mTxtCharactorEnabled.setVisibility(visibility);

                bindUserName(charactor);
                bindDistance(charactor, context);
            }
        }

        void bindUserName(Charactor charactor) {
            if (charactor.getUser() != null) {
                mTxtUserName.setText(charactor.getUser().getScreenName());
            }
        }

        void bindDistance(Charactor charactor, Context context) {
            mTxtDistance.setText("");
            CharactorLocation location = charactor.getLocation();

            if (location != null) {
                Log.d(TAG, "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());

                // TODO fix server API
                if (location.getLatitude() == 0f && location.getLatitude() == 0f) {
                    return;
                }

                float[] results = new float[3];
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), 
                        AppUtils.getLatitude(), AppUtils.getLongitude(), results);
                float meter = results[0];

                String distance = TextUtils.getDistanceString(context, (double)meter);
                mTxtDistance.setText(distance);
            }
        }

    }

}
