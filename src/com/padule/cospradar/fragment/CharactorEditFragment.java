package com.padule.cospradar.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseFragment;

public class CharactorEditFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_charactor_edit, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

}
