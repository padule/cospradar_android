package com.padule.cospradar.base;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import butterknife.ButterKnife;

import com.androidquery.AQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.service.APIService;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.KeyboardUtils;

public abstract class BaseActivity extends ActionBarActivity {
    protected AQuery aq;
    protected APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGooglePlayServicesAvailable();
        this.setTheme(R.style.AppTheme);
        aq = new AQuery(this);
        initApiService();
    }
    
    private void initApiService() {
        Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
        RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint(Constants.APP_URL)
        .setConverter(new GsonConverter(gson)).build();
        apiService = restAdapter.create(APIService.class);

    }

    private void checkGooglePlayServicesAvailable() {
        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
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
    protected void onDestroy() {
        super.onDestroy();
        aq.clear();
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
