package com.padule.cospradar.fragment;

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
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.adapter.ChooserCharactorsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.event.CommentCharactorSelectedEvent;
import com.padule.cospradar.util.AppUtils;

import de.greenrobot.event.EventBus;

public class CharactorSelectDialogFragment extends DialogFragment {

    @InjectView(R.id.grid_charactors) GridView mGridView;

    private ChooserCharactorsAdapter adapter;

    public static void show(BaseActivity activity, ArrayList<Charactor> charactors) {
        CharactorSelectDialogFragment fragment = new CharactorSelectDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Charactor.class.getName(), charactors);
        fragment.setArguments(args);
        fragment.show(activity.getSupportFragmentManager(), 
                CharactorSelectDialogFragment.class.getName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_charactor_select, null);

        ButterKnife.inject(this, view);
        builder.setView(view);

        initView();

        return builder.create();
    }

    private void initView() {
        List<Charactor> charactors = getCharactorsFromArgs();
        initGridView(charactors);
    }

    private void initGridView(List<Charactor> charactors) {
        adapter = new ChooserCharactorsAdapter(getActivity());
        mGridView.setAdapter(adapter);

        initGridViewListener();

        adapter.addAll(charactors);
    }

    private void initGridViewListener() {
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                selectCharactor(adapter.getItem(pos));
            }
        });
    }

    private void selectCharactor(Charactor charactor) {
        final int size = adapter.getCount();
        for (int i = 0; i < size; i++) {
            Charactor c = adapter.getItem(i);
            if (c == null) continue;

            if (c.getId() == charactor.getId()) {
                adapter.remove(c);
                c.setIsEnabled(true);
                adapter.insert(c, i);
            } else {
                c.setIsEnabled(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private Charactor getSelectedCharactor() {
        final int size = adapter.getCount();
        for (int i = 0; i < size; i++) {
            Charactor c = adapter.getItem(i);
            if (c != null && c.isEnabled()) {
                return c;
            }
        }
        return null;
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

    @OnClick(R.id.btn_cancel)
    void onClickBtnCancel() {
        dismiss();
    }

    @OnClick(R.id.btn_ok)
    void onClickBtnOk() {
        Charactor charactor = getSelectedCharactor();
        if (charactor != null) {
            AppUtils.showToast(getString(R.string.comment_dialog_selected, charactor.getNameAndTitle()), getActivity());
            EventBus.getDefault().post(new CommentCharactorSelectedEvent(charactor));
            dismiss();
        } else {
            AppUtils.showToast(R.string.charactor_chooser_not_selected, getActivity());
        }
    }

}
