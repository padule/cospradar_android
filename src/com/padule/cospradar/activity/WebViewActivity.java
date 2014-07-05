package com.padule.cospradar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;

public class WebViewActivity extends BaseActivity {

    private static final String EXTRA_URL = "url";
    private static final String EXTRA_TITLE = "title";

    @InjectView(R.id.webview) WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
    }

    public static void start(Context context, String url, String title) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(title)) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(EXTRA_URL, url);
            intent.putExtra(EXTRA_TITLE, title);
            context.startActivity(intent);
        }
    }

    @Override
    protected void initView() {
        initActionBar();
        initWebView();
    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        String url = getIntent().getStringExtra(EXTRA_URL);
        mWebView.loadUrl(url);
    }

    private void initActionBar() {
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
