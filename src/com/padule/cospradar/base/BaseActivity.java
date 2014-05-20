package com.padule.cospradar.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import butterknife.ButterKnife;

import com.androidquery.AQuery;

public abstract class BaseActivity extends ActionBarActivity {
    protected AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);
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

    protected abstract void initView();

}
