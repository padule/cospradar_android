package com.padule.cospradar.fragment;

import android.app.Dialog;
import android.os.Bundle;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseConfirmDialogFragment;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.event.CharactorDeleteEvent;

import de.greenrobot.event.EventBus;

public class CharactorDeleteDialogFragment extends BaseConfirmDialogFragment {

    private Charactor charactor;

    public static void show(BaseActivity activity, Charactor charactor) {
        CharactorDeleteDialogFragment fragment = new CharactorDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Charactor.class.getName(), charactor);
        fragment.setArguments(args);
        fragment.show(activity.getSupportFragmentManager(), 
                CharactorDeleteDialogFragment.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null && args.containsKey(Charactor.class.getName())) {
            this.charactor = (Charactor)args.getSerializable(Charactor.class.getName());
        }

        return super.onCreateDialog(savedInstanceState);
    }

    protected int getMsgResId() {
        return R.string.profile_delete_confirm;
    }

    protected void onConfirm() {
        EventBus.getDefault().post(new CharactorDeleteEvent(charactor));
    }

}
