package com.padule.cospradar.fragment;

import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseTutorialFragment;
import com.padule.cospradar.event.TutorialPageMoveEvent;
import com.padule.cospradar.util.AppUtils;

import de.greenrobot.event.EventBus;

public class Tutorial3RegistReadyFragment extends BaseTutorialFragment {

    public static final int POS = 2;

    @InjectView(R.id.txt_tutorial_regist_ready_sub) TextView mTxtTutorialRegistSub;

    @OnClick(R.id.btn_next)
    void onClickBtnYes() {
        EventBus.getDefault().post(new TutorialPageMoveEvent(Tutorial4RegistFragment.POS));
    }

    @Override
    protected int getContentLayoutResId() {
        return R.layout.fragment_tutorial3_regist_ready;
    }

    @Override
    protected void initView() {
        mTxtTutorialRegistSub.setText(getString(R.string.tutorial_regist_ready_sub, AppUtils.getUser().getScreenName()));
    }

}
