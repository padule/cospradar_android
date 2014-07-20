package com.padule.cospradar.dialog;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseConfirmDialogFragment;
import com.padule.cospradar.event.UserDeleteEvent;

import de.greenrobot.event.EventBus;

public class UserDeleteDialogFragment extends BaseConfirmDialogFragment {

    public static void show(BaseActivity activity) {
        UserDeleteDialogFragment fragment = new UserDeleteDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), 
                UserDeleteDialogFragment.class.getName());
    }

    @Override
    protected int getTitleResId() {
        return R.string.setting_account_delete_confirm_title;
    }

    @Override
    protected int getMsgResId() {
        return R.string.setting_account_delete_confirm;
    }

    @Override
    protected int getOkBtnTxtResId() {
        return R.string.setting_account_delete_ok;
    }

    @Override
    protected void onConfirm() {
        EventBus.getDefault().post(new UserDeleteEvent());
    }

}
