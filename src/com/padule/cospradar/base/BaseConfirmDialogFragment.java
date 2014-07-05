package com.padule.cospradar.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;

public abstract class BaseConfirmDialogFragment extends DialogFragment {

    @InjectView(R.id.img_icon) ImageView mImgIcon;
    @InjectView(R.id.txt_confirm_title) TextView mTxtConfirmTitle;
    @InjectView(R.id.txt_confirm_msg) TextView mTxtConfirmMsg;
    @InjectView(R.id.btn_ok) TextView mBtnOk;
    @InjectView(R.id.btn_cancel) TextView mBtnCancel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_base_confirm, null);

        ButterKnife.inject(this, view);
        bindData();

        builder.setView(view);
        return builder.create();
    }

    private void bindData() {
        mImgIcon.setImageResource(getImgIconResId());
        mTxtConfirmTitle.setText(getString(getTitleResId()));
        mTxtConfirmMsg.setText(getString(getMsgResId()));
        mBtnOk.setText(getString(getOkBtnTxtResId()));
        mBtnCancel.setText(getString(getCancelBtnTxtResId()));
    }

    protected int getImgIconResId() {
        return R.drawable.ic_launcher;
    }

    protected int getTitleResId() {
        return R.string.confirmation;
    }

    protected int getMsgResId() {
        return R.string.confirm;
    }

    protected int getOkBtnTxtResId() {
        return R.string.ok;
    }

    protected int getCancelBtnTxtResId() {
        return R.string.cancel;
    }

    protected abstract void onConfirm();

    @OnClick(R.id.btn_ok)
    void onClickBtnOk() {
        onConfirm();
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    void onClickBtnCancel() {
        dismiss();
    }

}
