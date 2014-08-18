package com.padule.cospradar.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.service.GooglePlayEvaluationTimerService;
import com.padule.cospradar.util.AppUtils;

public class RatingDialogFragment extends DialogFragment {

    @InjectView(R.id.txt_title) TextView mTxtTitle;
    @InjectView(R.id.txt_msg) TextView mTxtMsg;
    @InjectView(R.id.rating_bar) RatingBar mRatingBar;

    public static void show(BaseActivity activity) {
        RatingDialogFragment fragment = new RatingDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), 
                RatingDialogFragment.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rating, null);

        ButterKnife.inject(this, view);
        bindData();

        builder.setView(view);
        return builder.create();
    }

    private void bindData() {
        String appName = getString(R.string.app_name);
        mTxtTitle.setText(getString(R.string.rating_title, appName));
        mTxtMsg.setText(getString(R.string.rating_msg, appName));
    }

    @OnClick(R.id.btn_ok)
    void onClickBtnOk() {
        sendRating();
    }

    @OnClick(R.id.btn_cancel)
    void onClickBtnCancel() {
        dismiss();
    }

    private void sendRating() {
        float rating = mRatingBar.getRating();
        if (rating == mRatingBar.getMax()) {
            GooglePlayEvaluationTimerService.start(getActivity());
        }
        AppUtils.showToast(R.string.rating_after_msg, getActivity());
        dismiss();
    }

}
