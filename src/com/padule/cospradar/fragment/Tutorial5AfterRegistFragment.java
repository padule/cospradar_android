package com.padule.cospradar.fragment;

import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseTutorialFragment;
import com.padule.cospradar.event.TutorialPageMoveEvent;

import de.greenrobot.event.EventBus;

public class Tutorial5AfterRegistFragment extends BaseTutorialFragment {

    public static final int POS = 4;

    @OnClick(R.id.btn_next)
    void onClickBtnNext() {
        EventBus.getDefault().post(new TutorialPageMoveEvent(Tutorial6FinishFragment.POS));
    }

    @Override
    protected int getContentLayoutResId() {
        return R.layout.fragment_tutorial5_after_regist;
    }

    @Override
    protected void initView() {
        //
    }

}
