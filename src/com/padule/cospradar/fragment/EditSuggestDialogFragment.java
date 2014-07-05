package com.padule.cospradar.fragment;

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

    protected int getTitleResId() {
        return R.string.setting_suggest_title;
    }

    protected int getMsgResId() {
        return R.string.setting_suggest_message;
    }

    protected int getOkBtnTxtResId() {
        return R.string.setting_suggest_ok;
    }

    protected int getCancelBtnTxtResId() {
        return R.string.setting_suggest_cancel;
    }

}
