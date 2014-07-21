package com.padule.cospradar.dialog;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseConfirmDialogFragment;
import com.padule.cospradar.event.LogoutBtnClickedEvent;

import de.greenrobot.event.EventBus;

public class LogoutDialogFragment extends BaseConfirmDialogFragment {

    public static void show(BaseActivity activity) {
        LogoutDialogFragment fragment = new LogoutDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), 
                LogoutDialogFragment.class.getName());
    }

    @Override
    protected int getMsgResId() {
        return R.string.logout_confirm;
    }

    @Override
    protected int getOkBtnTxtResId() {
        return R.string.logout;
    }

    @Override
    protected void onConfirm() {
        EventBus.getDefault().post(new LogoutBtnClickedEvent());
    }

}
