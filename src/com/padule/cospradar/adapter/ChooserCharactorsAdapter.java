package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseArrayAdapter;
import com.padule.cospradar.data.Charactor;

public class ChooserCharactorsAdapter extends BaseArrayAdapter<Charactor> {

    public ChooserCharactorsAdapter(Context context) {
        this(context, new ArrayList<Charactor>());
    }

    public ChooserCharactorsAdapter(Context context, List<Charactor> charactors) {
        super(context, R.layout.item_chooser_charactor, charactors);
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chooser_charactor, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        Charactor charactor = getItem(pos);
        holder.bindData(charactor, context);
        initListeners(holder, parent, pos);

        return view;
    }

    private void initListeners(ViewHolder holder, final ViewGroup parent, final int pos) {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GridView) parent).performItemClick(v, pos, 0L);
            }
        };
        holder.mImgClicker.setOnClickListener(onClickListener);
        holder.mImgCreateClicker.setOnClickListener(onClickListener);
    }

    static class ViewHolder {
        @InjectView(R.id.img_charactor) ImageView mImgCharactor;
        @InjectView(R.id.txt_charactor_name) TextView mTxtCharactorName;
        @InjectView(R.id.txt_title) TextView mTxtTitle;
        @InjectView(R.id.loading) View mLoading;
        @InjectView(R.id.img_charactor_clicker) View mImgClicker;
        @InjectView(R.id.img_enabled_check) ImageView mImgEnabledCheck;
        @InjectView(R.id.container_create) View mContainerCreate;
        @InjectView(R.id.img_create_charactor_clicker) View mImgCreateClicker;
        @InjectView(R.id.container_charactor)View mContainerCharactor; 

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void bindData(Charactor charactor, Context context) {
            if (charactor != null) {
                MainApplication.IMAGE_LOADER.displayImage(charactor.getImageUrl(), mImgCharactor, imageLoaderListener);
                mTxtCharactorName.setText(charactor.getName());
                mTxtTitle.setText(charactor.getTitle());
                mImgEnabledCheck.setVisibility(charactor.isEnabled() ? View.VISIBLE : View.GONE);

                mContainerCreate.setVisibility(View.GONE);
                mContainerCharactor.setVisibility(View.VISIBLE);
            } else {
                mContainerCreate.setVisibility(View.VISIBLE);
                mContainerCharactor.setVisibility(View.GONE);
            }
        }

        private ImageLoadingListener imageLoaderListener =  new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mLoading.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bmp) {
                mLoading.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mLoading.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason reason) {
                mLoading.setVisibility(View.GONE);
            }
        };

    }

}
