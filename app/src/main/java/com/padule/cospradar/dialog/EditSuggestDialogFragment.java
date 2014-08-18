package com.padule.cospradar.dialog;

import com.padule.cospradar.R;
import com.padule.cospradar.activity.CharactorChooserActivity;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseConfirmDialogFragment;

public class EditSuggestDialogFragment extends BaseConfirmDialogFragment {

    public static void show(BaseActivity activity) {
        EditSuggestDialogFragment fragment = new EditSuggestDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), 
                EditSuggestDialogFragment.class.getName());
    }

    @Override
    protected void onConfirm() {
        CharactorChooserActivity.start(getActivity());
    }

    @Override
    protected int getTitleResId() {
        return R.string.setting_suggest_title;
    }

    @Override
    protected int getMsgResId() {
        return R.string.setting_suggest_message;
    }

    @Override
    protected int getOkBtnTxtResId() {
        return R.string.setting_suggest_ok;
    }

    @Override
    protected int getCancelBtnTxtResId() {
        return R.string.setting_suggest_cancel;
    }

}
