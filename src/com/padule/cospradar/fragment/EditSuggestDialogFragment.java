package com.padule.cospradar.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import butterknife.ButterKnife;

import com.padule.cospradar.R;
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
        builder.setTitle(getString(R.string.charactor_edit_suggest_title));;
        builder.setMessage(getString(R.string.charactor_edit_suggest_message));;
        builder.setPositiveButton(getString(R.string.charactor_edit_suggest_ok),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                ((MainActivity)getActivity()).showEditFragment();
            }
        });
        builder.setNegativeButton(getString(R.string.charactor_edit_suggest_cancel),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        Dialog dialog = builder.create();
        return dialog;
    }

}
