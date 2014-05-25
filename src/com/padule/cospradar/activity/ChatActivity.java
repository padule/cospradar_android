package com.padule.cospradar.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.fragment.ChatFragment;

public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected void initView() {
        Charactor charactor = (Charactor)getIntent()
                .getSerializableExtra(Charactor.class.getName());
        initActionBar(charactor);
        showFragment(ChatFragment.newInstance(charactor), R.id.content_frame);
    }

    private void initActionBar(Charactor charactor) {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(charactor.getNameAndTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
