package com.padule.cospradar.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import butterknife.InjectView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.ProfileCharactorListAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.ui.ProfileHeader;
import com.padule.cospradar.util.AppUtils;

public class ProfileActivity extends BaseActivity {

    @InjectView(R.id.listview_charactors) ListView mListView;

    private View mLoading;
    private ProfileCharactorListAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User)getIntent().getSerializableExtra(User.class.getName());
        setContentView(R.layout.activity_profile);
    }

    public static void start(Context context, User user) {
        if (user != null) {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(User.class.getName(), user);
            context.startActivity(intent);
        }
    }

    @Override
    protected void initView() {
        initActionBar();
        initListView();
    }

    private void initListView() {
        initLoading();
        adapter = new ProfileCharactorListAdapter(this);
        mListView.addHeaderView(new ProfileHeader(this, user));
        mListView.addHeaderView(mLoading);
        mListView.setAdapter(adapter);
        initListViewListener();
        loadData(1);
    }

    private void initLoading() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoading = inflater.inflate(R.layout.ui_loading, null);
    }

    private void hideLoading() {
        mListView.removeHeaderView(mLoading);
    }

    private void initListViewListener() {
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadData(page);
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                // TODO
            }
        });
    }

    private void loadData(final int page) {
        aq.ajax(AppUrls.getCharactorsIndex(page, user.getId()), JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                hideLoading();
                loadCallback(json);
            }
        });
    }

    private void loadCallback(JSONArray json) {
        List<Charactor> charactors = new ArrayList<Charactor>();
        if (json != null) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            Type collectionType = new TypeToken<List<Charactor>>() {}.getType();
            charactors = gson.fromJson(json.toString(), collectionType);
        } else {
            if (AppUtils.isMockMode()) {
                // FIXME implement using mock.
                charactors = MockFactory.getCharactors();
            }
        }

        if (charactors != null && !charactors.isEmpty() && adapter != null) {
            adapter.addAll(charactors);
        }
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getString(R.string.profile));
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
