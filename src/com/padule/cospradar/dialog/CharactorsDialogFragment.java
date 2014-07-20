package com.padule.cospradar.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.activity.ProfileActivity;
import com.padule.cospradar.adapter.CharactorsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;

public class CharactorsDialogFragment extends DialogFragment {

    @InjectView(R.id.txt_title) TextView mTxtTitle;
    @InjectView(R.id.listview_charactors) ListView mListView;

    private CharactorsAdapter adapter;

    public static void show(BaseActivity activity, ArrayList<Charactor> charactors) {
        CharactorsDialogFragment fragment = new CharactorsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Charactor.class.getName(), charactors);
        fragment.setArguments(args);
        fragment.show(activity.getSupportFragmentManager(), 
                CharactorsDialogFragment.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_charactors, null);

        ButterKnife.inject(this, view);
        builder.setView(view);

        initView();

        return builder.create();
    }

    private void initView() {
        List<Charactor> charactors = getCharactorsFromArgs();
        mTxtTitle.setText(getString(R.string.search_dialog_count, charactors.size()));
        initListView(charactors);
    }

    private void initListView(List<Charactor> charactors) {
        adapter = new CharactorsAdapter(getActivity());
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                ProfileActivity.start(CharactorsDialogFragment.this.getActivity(), 
                        adapter.getItem(pos).getUser());
            }
        });

        adapter.addAll(charactors);
    }

    @SuppressWarnings("unchecked")
    private List<Charactor> getCharactorsFromArgs() {
        Bundle args = getArguments();
        if (args != null && args.containsKey(Charactor.class.getName())) {
            return (List<Charactor>)args.getSerializable(Charactor.class.getName());
        } else {
            return new ArrayList<Charactor>();
        }
    }

    @OnClick(R.id.btn_close)
    void onClickBtnClose() {
        dismiss();
    }

}
