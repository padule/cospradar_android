package com.padule.cospradar.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.padule.cospradar.R;
import com.padule.cospradar.fragment.SearchListFragment;
import com.padule.cospradar.fragment.SearchRadarFragment;

public class SearchPagerAdapter extends FragmentPagerAdapter {
    private static final int POS_RADAR = 0;
    private static final int POS_LIST = 1;
    private static final int PAGE_SIZE = 2;

    private Context context;

    public SearchPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }



    @Override
    public Fragment getItem(int pos) {
        switch(pos) {
        case POS_RADAR:
            return new SearchRadarFragment();
        case POS_LIST:
            return new SearchListFragment();
        default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_SIZE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
        case POS_RADAR:
            return context.getString(R.string.search_radar_title);
        case POS_LIST:
            return context.getString(R.string.search_list_title);
        default:
            return "";
        }
    }
}
