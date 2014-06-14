package com.padule.cospradar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.activity.CharactorSettingActivity;
import com.padule.cospradar.base.BaseActivity;

public class EditSuggestDialogFragment extends DialogFragment {

    public static void show(BaseActivity activity) {
        EditSuggestDialogFragment fragment = new EditSuggestDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), 
                EditSuggestDialogFragment.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_suggest, null);

        ButterKnife.inject(this, view);
        builder.setView(view);
        return builder.create();
    }

    @OnClick(R.id.btn_ok)
    void onClickBtnOk() {
        CharactorSettingActivity.start(getActivity());
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    void onClickBtnCancel() {
        dismiss();
    }

}
