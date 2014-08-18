package com.padule.cospradar.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;

import com.padule.cospradar.R;

public abstract class BaseTutorialFragment extends BaseFragment {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_base_tutorial;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(getLayoutResId(), container, false);

        RelativeLayout containerTutorial = (RelativeLayout)view.findViewById(R.id.container_tutorial);
        inflater.inflate(getContentLayoutResId(), containerTutorial);

        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    protected abstract int getContentLayoutResId();

}
