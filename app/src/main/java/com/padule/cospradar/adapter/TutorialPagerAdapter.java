package com.padule.cospradar.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.padule.cospradar.fragment.Tutorial1WelcomeFragment;
import com.padule.cospradar.fragment.Tutorial2QuestionFragment;
import com.padule.cospradar.fragment.Tutorial3RegistReadyFragment;
import com.padule.cospradar.fragment.Tutorial4RegistFragment;
import com.padule.cospradar.fragment.Tutorial5AfterRegistFragment;
import com.padule.cospradar.fragment.Tutorial6FinishFragment;

public class TutorialPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = TutorialPagerAdapter.class.getName();
    private static final int PAGE_COUNT = 6;

    public TutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int pos) {
        switch(pos) {
        case Tutorial1WelcomeFragment.POS:
            return new Tutorial1WelcomeFragment();
        case Tutorial2QuestionFragment.POS:
            return new Tutorial2QuestionFragment();
        case Tutorial3RegistReadyFragment.POS:
            return new Tutorial3RegistReadyFragment();
        case Tutorial4RegistFragment.POS:
            return new Tutorial4RegistFragment();
        case Tutorial5AfterRegistFragment.POS:
            return new Tutorial5AfterRegistFragment();
        case Tutorial6FinishFragment.POS:
            return new Tutorial6FinishFragment();
        default:
            Log.e(TAG, "UnsupportedPage: " + pos);
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
