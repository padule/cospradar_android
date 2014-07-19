package com.padule.cospradar.fragment;

import android.os.Bundle;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseConfirmDialogFragment;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.event.CurrentCharactorSelectedEvent;
import com.padule.cospradar.util.AppUtils;

import de.greenrobot.event.EventBus;

public class CharactorEnableConfirmDialogFragment extends BaseConfirmDialogFragment {

    private Charactor charactor;

    public static void show(BaseActivity activity, Charactor charactor) {
        if (!AppUtils.isLoginUser(charactor.getUserId())) return;

        CharactorEnableConfirmDialogFragment fragment = new CharactorEnableConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Charactor.class.getName(), charactor);
        fragment.setArguments(args);
        fragment.show(activity.getSupportFragmentManager(), 
                CharactorEnableConfirmDialogFragment.class.getName());
    }

    private Charactor getCharactorFromArgs() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(Charactor.class.getName())) {
            return (Charactor)args.getSerializable(Charactor.class.getName());
        } else {
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.charactor = getCharactorFromArgs();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onConfirm() {
        EventBus.getDefault().post(new CurrentCharactorSelectedEvent(charactor));
    }

    @Override
    protected String getMsg() {
        if (charactor.isEnabled()) {
            return getString(R.string.profile_charactor_disable_confirm_msg);
        } else {
            return getString(R.string.profile_charactor_enable_confirm_msg, charactor.getName());
        }
    }

    @Override
    protected int getOkBtnTxtResId() {
        return R.string.yes;
    }

    @Override
    protected int getCancelBtnTxtResId() {
        return R.string.no;
    }

}
