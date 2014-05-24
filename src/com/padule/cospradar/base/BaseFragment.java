package com.padule.cospradar.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

import com.androidquery.AQuery;
import com.padule.cospradar.util.AppUtils;

public abstract class BaseFragment extends Fragment {
    protected AQuery aq;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        aq = new AQuery(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (aq != null) {
            aq.clear();
        }
    }

    protected void showToast(int resId) {
        AppUtils.showToast(getString(resId), getActivity());
    }

}
