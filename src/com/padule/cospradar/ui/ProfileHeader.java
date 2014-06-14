package com.padule.cospradar.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.R.id;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;

public class ProfileHeader extends RelativeLayout {

    @InjectView(id.img_user) ImageView mImgUser;
    @InjectView(id.txt_user_name) TextView mTxtUserName;
    @InjectView(id.btn_add_charactor) Button mBtnAddCharactor;
    @InjectView(id.btn_chat) Button mBtnChat;

    public ProfileHeader(Context context, User user) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.ui_header_profile, this, true);
        ButterKnife.inject(this);

        bindData(user);
        initButton(user);
    }

    private void bindData(User user) {
        Charactor charactor = user.getCurrentCharactor();
        if (charactor != null) {
            ImageUtils.displayRoundedImage(charactor.getImageUrl(), mImgUser);
        } else {
            mImgUser.setImageResource(R.drawable.ic_no_user_rounded);
        }

        mTxtUserName.setText(user.getName());
    }

    private void initButton(User user) {
        if (AppUtils.isLoginUser(user)) {
            mBtnAddCharactor.setVisibility(View.VISIBLE);
            mBtnChat.setVisibility(View.GONE);
        } else {
            mBtnAddCharactor.setVisibility(View.GONE);
            mBtnChat.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_add_charactor)
    void onlickBtnAddCharactor() {
        //
    }

    @OnClick(R.id.btn_chat)
    void onClickBtnChat() {
        //
    }

}
