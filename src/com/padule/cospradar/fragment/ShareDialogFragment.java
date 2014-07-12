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
import com.padule.cospradar.base.BaseActivity;

public class ShareDialogFragment extends DialogFragment {

    public static void show(BaseActivity activity) {
        ShareDialogFragment fragment = new ShareDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), 
                ShareDialogFragment.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_share, null);

        ButterKnife.inject(this, view);
        builder.setView(view);

        initView();

        return builder.create();
    }

    private void initView() {
        //
    }

    @OnClick(R.id.btn_close)
    void onClickBtnClose() {
        dismiss();
    }

}
