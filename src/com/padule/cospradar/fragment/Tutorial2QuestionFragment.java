package com.padule.cospradar.fragment;

import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseTutorialFragment;
import com.padule.cospradar.event.TutorialPageMoveEvent;
import com.padule.cospradar.util.AppUtils;

import de.greenrobot.event.EventBus;

public class Tutorial2QuestionFragment extends BaseTutorialFragment {

    public static final int POS = 1;

    @InjectView(R.id.txt_tutorial_question) TextView mTxtTutorialQuestion;

    @OnClick(R.id.btn_yes)
    void onClickBtnYes() {
        EventBus.getDefault().post(new TutorialPageMoveEvent(Tutorial3RegistReadyFragment.POS));
    }

    @OnClick(R.id.btn_no)
    void onClickBtnNo() {
        EventBus.getDefault().post(new TutorialPageMoveEvent(Tutorial6FinishFragment.POS));
    }

    @Override
    protected int getContentLayoutResId() {
        return R.layout.fragment_tutorial2_question;
    }

    @Override
    protected void initView() {
        mTxtTutorialQuestion.setText(getString(R.string.tutorial_question_sub, AppUtils.getUser().getScreenName()));
    }

}
