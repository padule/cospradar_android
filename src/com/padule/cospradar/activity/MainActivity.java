package com.padule.cospradar.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import butterknife.InjectView;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.CharactorsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.service.APIService;
import com.padule.cospradar.service.LocationService;
import com.padule.cospradar.ui.SearchHeader;
import com.padule.cospradar.ui.SearchHeader.SearchListener;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.KeyboardUtils;

public class MainActivity extends BaseActivity implements SearchListener {

    private static final String TAG = MainActivity.class.getName();

    @InjectView(R.id.listview_search) ListView mListView;

    private CharactorsAdapter adapter;
    private SearchHeader header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, LocationService.class));
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        initActionBar();
        initListView();
    }

    private void initListView() {
        adapter = new CharactorsAdapter(this);
        header = new SearchHeader(this, this);
        mListView.addHeaderView(header);
        mListView.setAdapter(adapter);
        initListViewListener();
    }

    private void initListViewListener() {
        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // TODO implement paging.
                // loadData(page);
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                pos -= mListView.getHeaderViewsCount();
                ProfileActivity.start(MainActivity.this, adapter.getItem(pos).getUser());
            }
        });
    }

    private void loadData(final int page, String title) {
        MainApplication.API.getCharactors(createParams(title), 
                new Callback<List<Charactor>>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(List<Charactor> charactors, Response response) {
                renderView(charactors);
            }
        });
    }

    private Map<String, String> createParams(String title) {
        Map<String, String> params = new HashMap<String, String>();
        if (title != null && !"".equals(title)) {
            params.put(APIService.PARAM_TITLE, title);
        }
        params.put(APIService.PARAM_LATITUDE, AppUtils.getLatitude() + "");
        params.put(APIService.PARAM_LONGITUDE, AppUtils.getLongitude() + "");
        params.put(APIService.PARAM_LIMIT, "300");

        return params;
    }

    private void renderView(List<Charactor> charactors) {
        if (charactors != null && !charactors.isEmpty() && adapter != null) {
            adapter.addAll(charactors);
            refreshHeader(charactors);
        } else {
            refreshHeader(new ArrayList<Charactor>());
        }
    }

    private void refreshHeader(List<Charactor> charactors) {
        if (header != null) {
            header.refresh(charactors);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.item_chat_list:
            ChatBoardListActivity.start(this);
            break;
        case R.id.item_profile:
            ProfileActivity.start(this, AppUtils.getUser());
            break;
        case android.R.id.home:
            mListView.setSelection(0);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter = null;
        }
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(false);
        bar.setHomeButtonEnabled(true);
    }

    @Override
    public void onClickBtnReload(String searchText) {
        AppUtils.vibrate(100, this);
        clearListView();
        header.startSearching();
        loadData(0, searchText);
        KeyboardUtils.hide(this);
    }

    private void clearListView() {
        if (adapter != null) {
            adapter.clear();
        }
    }

}
