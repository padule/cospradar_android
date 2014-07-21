package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.util.AppUtils;

public class ProfileCharactorsAdapter extends ArrayAdapter<Charactor> {

    private Context context;

    public ProfileCharactorsAdapter(Context context) {
        this(context, new ArrayList<Charactor>());
    }

    public ProfileCharactorsAdapter(Context context, List<Charactor> charactors) {
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
        initListeners(holder, parent, pos);

        return view;
    }

    private void initListeners(ViewHolder holder, final ViewGroup parent, final int pos) {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, pos, 0L);
            }
        };
        holder.mImgClicker.setOnClickListener(onClickListener);
        holder.mImgMenu.setOnClickListener(onClickListener);
        holder.mImgChat.setOnClickListener(onClickListener);
        holder.mTxtCharactorEnabledClicker.setOnClickListener(onClickListener);
    }

    static class ViewHolder {
        @InjectView(R.id.img_charactor) ImageView mImgCharactor;
        @InjectView(R.id.txt_charactor_name) TextView mTxtCharactorName;
        @InjectView(R.id.txt_title) TextView mTxtTitle;
        @InjectView(R.id.img_menu) ImageView mImgMenu;
        @InjectView(R.id.img_chat) ImageView mImgChat;
        @InjectView(R.id.loading) View mLoading;
        @InjectView(R.id.img_charactor_clicker) View mImgClicker;
        @InjectView(R.id.txt_charactor_enabled) TextView mTxtCharactorEnabled;
        @InjectView(R.id.txt_charactor_enabled_clicker) View mTxtCharactorEnabledClicker;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void bindData(Charactor charactor, Context context) {
            if (charactor != null) {
                mImgCharactor.setScaleType(ScaleType.CENTER_CROP);

                MainApplication.IMAGE_LOADER.displayImage(charactor.getImageUrl(), mImgCharactor, imageLoaderListener);
                mTxtCharactorName.setText(charactor.getName());
                mTxtTitle.setText(charactor.getTitle());

                mTxtCharactorEnabled.setEnabled(charactor.isEnabled());
                int resId = charactor.isEnabled() ? R.string.profile_charactor_enabled 
                        : R.string.profile_charactor_enabled_question;
                mTxtCharactorEnabled.setText(context.getString(resId));

                if (AppUtils.isLoginUser(charactor.getUser())) {
                    mImgMenu.setVisibility(View.VISIBLE);
                } else {
                    mImgMenu.setVisibility(View.GONE);
                    if (!charactor.isEnabled()) {
                        mTxtCharactorEnabled.setVisibility(View.GONE);
                    } else {
                        mTxtCharactorEnabled.setVisibility(View.VISIBLE);
                    }
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
                mLoading.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason reason) {
                mLoading.setVisibility(View.GONE);
            }
        };

    }

}
