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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import butterknife.InjectView;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.ChooserCharactorsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.User;
import com.padule.cospradar.service.ApiService;
import com.padule.cospradar.util.AppUtils;

public class CharactorChooserActivity extends BaseActivity {

    private static final String TAG = CharactorChooserActivity.class.getName();

    @InjectView(R.id.grid_charactors) GridView mGridView;
    @InjectView(R.id.loading)View mLoading; 

    private ChooserCharactorsAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User)getIntent().getSerializableExtra(User.class.getName());
        setContentView(R.layout.activity_charactor_chooser);
    }

    public static void start(Context context) {
        User user = AppUtils.getUser();
        if (user!= null) {
            Intent intent = new Intent(context, CharactorChooserActivity.class);
            intent.putExtra(User.class.getName(), user);
            context.startActivity(intent);
        }
    }

    @Override
    protected void initView() {
        initActionBar();
        initGridView();
    }

    private void initGridView() {
        adapter = new ChooserCharactorsAdapter(this);
        mGridView.setAdapter(adapter);
        initGridViewListener();

        loadData(1);
    }

    private void addCreateCharactorIconItem() {
        adapter.add(null);
    }

    private void initGridViewListener() {
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadData(page);
            }
        });
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Log.d(TAG, "pos: " + pos + "");
                if (pos == 0) {
                    CharactorSettingActivity.start(CharactorChooserActivity.this,
                            CharactorChooserActivity.this.adapter.getItem(pos));
                } else {
                    // TODO implement
                }
            }
        });
    }

    private void loadData(final int page) {
        MainApplication.API.getCharactors(createParams(user.getId(), page), 
                new Callback<List<Charactor>>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(List<Charactor> charactors, Response response) {
                if (page == 1) {
                    mLoading.setVisibility(View.GONE);
                    addCreateCharactorIconItem();
                }
                renderView(charactors);
            }
        });
    }

    private void renderView(List<Charactor> charactors) {
        if (charactors != null && !charactors.isEmpty() && adapter != null) {
            adapter.addAll(charactors);
        }
    }

    private Map<String, String> createParams(int userId, int page) {
        Map<String, String> params = new HashMap<String, String>();
        if (userId > 0) {
            params.put(ApiService.PARAM_USER_ID, userId + "");
            params.put(ApiService.PARAM_DESC, "is_enabled");
        }
        params.put(ApiService.PARAM_PAGE, page + "");
        params.put(ApiService.PARAM_LATITUDE, AppUtils.getLatitude() + "");
        params.put(ApiService.PARAM_LONGITUDE, AppUtils.getLongitude() + "");

        return params;
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getString(R.string.charactor_chooser));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        case R.id.item_done:
            if (validate()) {
                updateCharactor();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_charactor_chooser, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean validate() {
        // TODO implement
        return false;
    }

    private void updateCharactor() {
        // TODO implement
    }

}