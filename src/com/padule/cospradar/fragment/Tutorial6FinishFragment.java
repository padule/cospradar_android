package com.padule.cospradar.fragment;

import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseTutorialFragment;
import com.padule.cospradar.event.TutorialCardFinishedEvent;

import de.greenrobot.event.EventBus;

public class Tutorial6FinishFragment extends BaseTutorialFragment {

    public static final int POS = 5;

    @InjectView(R.id.btn_ok) TextView mBtnOk;

    @OnClick(R.id.btn_ok)
    void onClickBtnOk() {
        mBtnOk.setEnabled(false);
        EventBus.getDefault().post(new TutorialCardFinishedEvent());
    }

    @Override
    protected int getContentLayoutResId() {
        return R.layout.fragment_tutorial6_finish;
    }

    @Override
    protected void initView() {
        //
    }

}
