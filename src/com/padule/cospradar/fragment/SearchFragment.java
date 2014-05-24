package com.padule.cospradar.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.only_search, menu);

        initSearchView(menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initSearchView(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.item_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(onQueryTextListener);

        EditText edit = (EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        int textColorResId = getResources().getColor(R.color.text_white);
        edit.setTextColor(textColorResId);
        edit.setHintTextColor(textColorResId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.item_search == item.getItemId()) {
            //
        }
        return super.onOptionsItemSelected(item);
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

    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }
    };

}
