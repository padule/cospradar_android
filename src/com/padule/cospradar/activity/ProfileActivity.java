package com.padule.cospradar.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import butterknife.InjectView;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.ProfileCharactorsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;
import com.padule.cospradar.service.APIService;
import com.padule.cospradar.ui.ProfileHeader;
import com.padule.cospradar.util.AppUtils;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = ProfileActivity.class.getName();

    @InjectView(R.id.listview_charactors) ListView mListView;

    private View mLoading;
    private ProfileCharactorsAdapter adapter;
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
        adapter = new ProfileCharactorsAdapter(this);
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
                switch (view.getId()) {
                case R.id.img_charactor_clicker:
                    PhotoPreviewActivity.start(ProfileActivity.this, 
                            adapter.getItem(pos).getImageUrl());
                    break;
                case R.id.img_menu:
                    // TODO
                    break;
                }
            }
        });
    }

    private void loadData(int page) {
        MainApplication.API.getCharactors(createParams(user.getId(), page), 
                new Callback<List<Charactor>>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(List<Charactor> charactors, Response response) {
                hideLoading();
                renderView(charactors);
            }
        });
    }

    private Map<String, String> createParams(int userId, int page) {
        Map<String, String> params = new HashMap<String, String>();
        if (userId > 0) {
            params.put(APIService.PARAM_USER_ID, userId + "");
            params.put(APIService.PARAM_DESC, "is_enabled");
        }
        params.put(APIService.PARAM_PAGE, page + "");
        params.put(APIService.PARAM_LATITUDE, AppUtils.getLatitude() + "");
        params.put(APIService.PARAM_LONGITUDE, AppUtils.getLongitude() + "");

        return params;
    }

    private void renderView(List<Charactor> charactors) {
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
