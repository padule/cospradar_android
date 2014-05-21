package com.padule.cospradar.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.astuetz.PagerSlidingTabStrip;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.SearchPagerAdapter;
import com.padule.cospradar.base.BaseFragment;

public class SearchFragment extends BaseFragment {

    @InjectView(R.id.tabstirp_search) PagerSlidingTabStrip mTabStrip;
    @InjectView(R.id.pager_search) ViewPager mViewPager;
    @InjectView(R.id.root_search) LinearLayout mRoot;

    private SearchPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.inject(this, view);
        initPager();

        return view;
    }

    private void initPager() {
        adapter = new SearchPagerAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(adapter);

        mTabStrip.setViewPager(mViewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mViewPager != null) {
            mViewPager.setAdapter(null);
            mViewPager = null;
        }
        if (adapter != null) {
            adapter = null;
        }
    }

}
