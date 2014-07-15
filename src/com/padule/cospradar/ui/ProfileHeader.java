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
import com.padule.cospradar.activity.CharactorChooserActivity;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;
import com.padule.cospradar.event.CurrentCharactorSelectedEvent;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;

import de.greenrobot.event.EventBus;

public class ProfileHeader extends RelativeLayout {

    @InjectView(id.img_user) ImageView mImgUser;
    @InjectView(id.txt_user_name) TextView mTxtUserName;
    @InjectView(id.btn_add_charactor) Button mBtnAddCharactor;

    private BaseActivity activity;

    public ProfileHeader(BaseActivity activity, User user) {
        super(activity);
        LayoutInflater.from(activity).inflate(R.layout.ui_header_profile, this, true);
        ButterKnife.inject(this);

        this.activity = activity;

        bindData(user);
        initButton(user);

        EventBus.getDefault().register(this);
    }

    public void onEvent(CurrentCharactorSelectedEvent event) {
        bindData(AppUtils.getUser());
    }

    private void bindData(User user) {
        Charactor charactor = AppUtils.isLoginUser(user) ? AppUtils.getCharactor() : user.getCurrentCharactor();

        if (charactor != null) {
            ImageUtils.displayRoundedImage(charactor.getImageUrl(), mImgUser);
        } else {
            mImgUser.setImageResource(R.drawable.ic_no_user_rounded);
        }

        mTxtUserName.setText(user.getScreenName());
    }

    private void initButton(User user) {
        if (AppUtils.isLoginUser(user)) {
            mBtnAddCharactor.setVisibility(View.VISIBLE);
        } else {
            mBtnAddCharactor.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_add_charactor)
    void onclickBtnAddCharactor() {
        CharactorChooserActivity.start(activity);
    }

    @OnClick(R.id.img_user_cover)
    void onClickImgUserCover() {
        CharactorChooserActivity.start(activity);
    }

}
