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

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;

public class ProfileCharactorListAdapter extends ArrayAdapter<Charactor> {

    private Context context;

    public ProfileCharactorListAdapter(Context context) {
        this(context, new ArrayList<Charactor>());
    }

    public ProfileCharactorListAdapter(Context context, List<Charactor> charactors) {
        super(context, R.layout.item_profile_charactor, charactors);
        this.context = context;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_profile_charactor, parent, false);
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
        @InjectView(R.id.img_charactor) ImageView mImgCharactor;
        @InjectView(R.id.txt_charactor_name) TextView mTxtCharactorName;
        @InjectView(R.id.txt_title) TextView mTxtTitle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void bindData(Charactor charactor, Context context) {
            if (charactor != null) {
                MainApplication.imageLoader.displayImage(charactor.getImageUrl(), mImgCharactor);
                mTxtCharactorName.setText(charactor.getName());
                mTxtTitle.setText(charactor.getTitle());
            }
        }

    }

}
