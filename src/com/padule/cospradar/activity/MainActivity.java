package com.padule.cospradar.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

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

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.CharactorListAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.service.LocationService;
import com.padule.cospradar.ui.SearchHeader;
import com.padule.cospradar.util.AppUtils;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.listview_search) ListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;

    private CharactorListAdapter adapter;

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
        adapter = new CharactorListAdapter(this);
        mListView.addHeaderView(new SearchHeader(this));
        mListView.setAdapter(adapter);
        loadData(1);
        initListViewListener();
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
                CommentActivity.start(MainActivity.this, adapter.getItem(pos));
            }
        });
    }

    private void loadData(final int page) {
        Log.d("Cospradar", AppUrls.getCharactorsIndex(page) + "");
        aq.ajax(AppUrls.getCharactorsIndex(page), JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.item_chat_list:
            final Intent intent = new Intent(this, CommentListActivity.class);
            startActivity(intent);
            break;
        case R.id.item_profile:
            break;
        case android.R.id.home:
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
    }

}
