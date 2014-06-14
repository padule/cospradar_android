package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.util.AppUtils;

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
        @InjectView(R.id.img_charactor_left) ImageView mImgCharactor;
        @InjectView(R.id.txt_charactor_name_left) TextView mTxtCharactorName;
        @InjectView(R.id.txt_title) TextView mTxtTitle;
        @InjectView(R.id.img_menu) ImageView mImgMenu;
        @InjectView(R.id.loading) View mLoading;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void bindData(Charactor charactor, Context context) {
            if (charactor != null) {
                MainApplication.imageLoader.displayImage(charactor.getImageUrl(), mImgCharactor, imageLoaderListener);
                mTxtCharactorName.setText(charactor.getName());
                mTxtTitle.setText(charactor.getTitle());

                if (AppUtils.isLoginUser(charactor.getUser())) {
                    mImgMenu.setVisibility(View.VISIBLE);
                } else {
                    mImgMenu.setVisibility(View.GONE);
                }
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
                setEmptyImage();
                mLoading.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason reason) {
                setEmptyImage();
                mLoading.setVisibility(View.GONE);
            }

            private void setEmptyImage() {
                mImgCharactor.setScaleType(ScaleType.CENTER);
                mImgCharactor.setBackgroundResource(R.color.bg_gray);
            }
        };

    }

}
