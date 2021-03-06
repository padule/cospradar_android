package com.padule.cospradar.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import butterknife.ButterKnife;

import com.padule.cospradar.R;
import com.padule.cospradar.util.AnalyticsUtils;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.KeyboardUtils;

public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGooglePlayServicesAvailable();
        this.setTheme(R.style.AppTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        AnalyticsUtils.activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AnalyticsUtils.activityStop(this);
    }

    private void checkGooglePlayServicesAvailable() {
        if (!AppUtils.isGooglePlayServicesAvailable(this)) {
            AppUtils.showToast(getString(R.string.google_play_service_unavailable), this);
            finish();
        }
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        ButterKnife.inject(this);
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        KeyboardUtils.hide(this);
        return super.onOptionsItemSelected(item);
    }

    protected void showFragment(String fragmentPackage, int resId) {
        Fragment fragment = Fragment.instantiate(this, fragmentPackage);
        showFragment(fragment, resId);
    }

    public void showFragment(Fragment fragment, int resId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(resId, fragment, fragment.getTag()).commit();
    }

    protected abstract void initView();

}
