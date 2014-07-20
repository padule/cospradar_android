package com.padule.cospradar.dialog;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.activity.CharactorChooserActivity;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.PrefUtils;

public class CharactorSetSuggestDialogFragment extends DialogFragment {

    @InjectView(R.id.check_never_display_msg) CheckBox mCheckNeverDisplayMsg;

    public static void show(BaseActivity activity) {
        if (AppUtils.shouldSuggestCharactorSetting()) {
            CharactorSetSuggestDialogFragment fragment = new CharactorSetSuggestDialogFragment();
            fragment.show(activity.getSupportFragmentManager(), 
                    CharactorSetSuggestDialogFragment.class.getName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_charactor_set_suggest, null);

        ButterKnife.inject(this, view);
        builder.setView(view);

        return builder.create();
    }

    @OnClick(R.id.btn_cancel)
    void onClickBtnClose() {
        dismiss();
    }

    @OnClick(R.id.btn_ok)
    void onClickBtnOk() {
        CharactorChooserActivity.start(getActivity());
        dismiss();
    }

    @Override
    public void dismiss() {
        if (mCheckNeverDisplayMsg.isChecked()) {
            PrefUtils.put(PrefUtils.KEY_SUGGEST_CHARACTOR_SETTTING_TIME, new Date());
        }
        super.dismiss();
    }

}
