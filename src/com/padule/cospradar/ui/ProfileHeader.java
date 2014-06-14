package com.padule.cospradar.ui;

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
import com.padule.cospradar.activity.CharactorSettingActivity;
import com.padule.cospradar.activity.ChatBoardActivity;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;

public class ProfileHeader extends RelativeLayout {

    @InjectView(id.img_user) ImageView mImgUser;
    @InjectView(id.txt_user_name) TextView mTxtUserName;
    @InjectView(id.btn_add_charactor) Button mBtnAddCharactor;
    @InjectView(id.btn_chat) Button mBtnChat;
    @InjectView(id.btn_my_chat) Button mBtnMyChat;

    private User user;
    private BaseActivity activity;

    public ProfileHeader(BaseActivity activity, User user) {
        super(activity);
        LayoutInflater.from(activity).inflate(R.layout.ui_header_profile, this, true);
        ButterKnife.inject(this);

        this.user = user;
        this.activity = activity;

        bindData(user);
        initButton(user);
    }

    private void bindData(User user) {
        Charactor charactor = AppUtils.isLoginUser(user) ? AppUtils.getCharactor() : user.getCurrentCharactor();

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
            mBtnMyChat.setVisibility(View.VISIBLE);
            mBtnChat.setVisibility(View.GONE);
        } else {
            mBtnAddCharactor.setVisibility(View.GONE);
            mBtnMyChat.setVisibility(View.GONE);
            mBtnChat.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_add_charactor)
    void onlickBtnAddCharactor() {
        CharactorSettingActivity.start(activity);
    }

    @OnClick(R.id.btn_my_chat)
    void onClickBtnMyChat() {
        ChatBoardActivity.start(activity, AppUtils.getCharactor());
    }

    @OnClick(R.id.btn_chat)
    void onClickBtnChat() {
        ChatBoardActivity.start(activity, this.user.getCurrentCharactor());
    }

}
