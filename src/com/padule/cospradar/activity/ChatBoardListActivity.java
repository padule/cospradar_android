package com.padule.cospradar.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.ChatBoardsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.fragment.EditSuggestDialogFragment;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.util.AppUtils;

public class ChatBoardListActivity extends BaseActivity {

    private static final String TAG = ChatBoardListActivity.class.getName();
    @InjectView(R.id.listview_chat_list) PullToRefreshListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;
    @InjectView(R.id.loading) View mLoading;

    private ChatBoardsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_board_list);
    }

    public static void start(BaseActivity activity) {
        if (AppUtils.getCharactor() == null) {
            EditSuggestDialogFragment.show(activity);
        } else {
            final Intent intent = new Intent(activity, ChatBoardListActivity.class);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void initView() {
        initActionBar();
        initListView();
    }

    private void initListView() {
        adapter = new ChatBoardsAdapter(this);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setAdapter(adapter);
        initListViewListener();
        loadData(1);
    }

    private void initListViewListener() {
        int headerCount = mListView.getRefreshableView().getHeaderViewsCount();
        mListView.setOnScrollListener(new EndlessScrollListener(-headerCount) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (adapter != null && page > 1) {
                    loadData(page);
                }
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1, true);
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                pos -= mListView.getRefreshableView().getHeaderViewsCount();
                ChatBoardActivity.start(ChatBoardListActivity.this, adapter.getItem(pos));
            }
        });
    }

    private void loadData(int page) {
        loadData(page, false);
    }

    private void loadData(final int page, final boolean shouldAllClear) {
        String url = AppUrls.getCharactorCommentsCommentList(AppUtils.getCharactor().getId(), page);
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (page == 1) {
                    mLoading.setVisibility(View.GONE);
                }
                loadCallback(json, status, page, shouldAllClear);
            }
        });
    }

    private void loadCallback(JSONArray json, AjaxStatus status, int page, boolean shouldClearAll) {
        List<Charactor> charactors = new ArrayList<Charactor>();
        if (json != null) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            Type collectionType = new TypeToken<List<Charactor>>() {}.getType();
            charactors = gson.fromJson(json.toString(), collectionType);
        } else {
            Log.e(TAG, status.getMessage() + "");
            if (AppUtils.isMockMode()) {
                // FIXME implement using mock.
                charactors = MockFactory.getCharactors();
            }
        }

        if (charactors != null && !charactors.isEmpty()) {
            if (page > 1 && adapter.isEmpty()) {
                return;
            }
            if (shouldClearAll) {
                adapter.clear();
                mListView.onRefreshComplete();
                initListViewListener();
            }
            adapter.addAll(charactors);
        } else {
            int visibility = page == 1 ? View.VISIBLE : View.GONE;
            mContainerEmpty.setVisibility(visibility);
        }
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getString(R.string.comment_chats));
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
