package com.padule.cospradar.fragment;

import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseTutorialFragment;
import com.padule.cospradar.event.TutorialPageMoveEvent;
import com.padule.cospradar.util.AppUtils;

import de.greenrobot.event.EventBus;

public class Tutorial1WelcomeFragment extends BaseTutorialFragment {

    public static final int POS = 0;

    @InjectView(R.id.txt_tutorial_hello) TextView mTxtTutorialHello;
    @InjectView(R.id.txt_tutorial_welcome) TextView mTxtTutorialWelcome;

    @OnClick(R.id.btn_next)
    void onClickBtnNext() {
        EventBus.getDefault().post(new TutorialPageMoveEvent(Tutorial2QuestionFragment.POS));
    }

    @Override
    protected int getContentLayoutResId() {
        return R.layout.fragment_tutorial1_welcome;
    }

    @Override
    protected void initView() {
        mTxtTutorialHello.setText(getString(R.string.tutorial_welcome_title, AppUtils.getUser().getScreenName()));
        mTxtTutorialWelcome.setText(getString(R.string.tutorial_welcome_sub, getString(R.string.app_name)));
    }

}
