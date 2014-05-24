package com.padule.cospradar.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.util.ImageUtils;

public class DrawerHeader extends RelativeLayout {

    @InjectView(R.id.container_charactor_txt) View mContainerCharactorTxt;
    @InjectView(R.id.img_cover) ImageView mImgCover;
    @InjectView(R.id.img_charactor) ImageView mImgCharactor;
    @InjectView(R.id.txt_charactor_name) TextView mTxtCharactorName;
    @InjectView(R.id.txt_charactor_title) TextView mTxtCharactorTitle;
    @InjectView(R.id.txt_no_charactor) TextView mTxtNoCharactor;

    public DrawerHeader(Context context) {
        super(context);
    }

    public DrawerHeader(Context context, Charactor charactor) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_drawer_header, this, true);
        ButterKnife.inject(this);
        bindData(charactor);
    }

    private void bindData(Charactor charactor) {
        if (charactor != null) {
            ImageUtils.displayRoundedImage(charactor.getImageUrl(), mImgCharactor);
            mTxtCharactorName.setText(charactor.getName());
            mTxtCharactorTitle.setText(charactor.getTitle());
            MainApplication.imageLoader.displayImage(charactor.getImageUrl(), mImgCover);
        } else {
            mContainerCharactorTxt.setVisibility(View.GONE);
            mTxtNoCharactor.setVisibility(View.VISIBLE);
            mImgCharactor.setImageResource(R.drawable.ic_no_user_rounded);
        }
    }

}
