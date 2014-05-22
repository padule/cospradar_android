package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.util.ImageUtils;

public class SearchListAdapter extends ArrayAdapter<Charactor> {

    private Context context;

    public SearchListAdapter(Context context) {
        this(context, new ArrayList<Charactor>());
    }

    public SearchListAdapter(Context context, List<Charactor> charactors) {
        super(context, R.layout.item_charactor, charactors);
        this.context = context;
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
        holder.bindData(charactor);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.img_icon) ImageView mImgIcon;
        @InjectView(R.id.txt_name) TextView mTxtName;
        @InjectView(R.id.txt_title) TextView mTxtTitle;
        @InjectView(R.id.txt_distance) TextView mTxtDistance;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void bindData(Charactor charactor) {
            if (charactor != null) {
                ImageUtils.displayRoundedImage(charactor.getImage(), mImgIcon);
                mTxtName.setText(charactor.getName());
                mTxtTitle.setText(charactor.getTitle());
                // TODO implment binding distance.
            }
        }
    }

}
