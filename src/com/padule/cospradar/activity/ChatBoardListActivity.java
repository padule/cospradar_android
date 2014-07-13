package com.padule.cospradar.activity;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.InjectView;

import com.google.android.gms.ads.AdView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.ChatBoardsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.event.CommentSentEvent;
import com.padule.cospradar.fragment.EditSuggestDialogFragment;
import com.padule.cospradar.util.AdmobUtils;
import com.padule.cospradar.util.AppUtils;

import de.greenrobot.event.EventBus;

public class ChatBoardListActivity extends BaseActivity {

    private static final String TAG = ChatBoardListActivity.class.getName();
    @InjectView(R.id.listview_chat_list) PullToRefreshListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;
    @InjectView(R.id.loading) View mLoading;
    @InjectView(R.id.container_admob) RelativeLayout mContainerAdmob;

    private AdView adView;
    private ChatBoardsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_board_list);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(CommentSentEvent event) {
        updateItem(event.charactor);
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
        initAdmob();
    }

    private void initAdmob() {
        adView = AdmobUtils.createAdViewInChatBoardListFooter(this);
        AdmobUtils.loadBanner(adView, mContainerAdmob);
    }

    @Override
    public void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) adView.resume();
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

    private void updateItem(Charactor newCharactor) {
        if (adapter != null && newCharactor != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Charactor orgCharactor = adapter.getItem(i);
                if (orgCharactor != null && orgCharactor.getId() == newCharactor.getId()) {
                    adapter.remove(orgCharactor);
                    adapter.insert(newCharactor, i);
                    return;
                }
            }
        }
    }

    private void loadData(int page) {
        loadData(page, false);
    }

    private void loadData(final int page, final boolean shouldClearAll) {
        MainApplication.API.getCharactorCommentsCommentList(AppUtils.getUser().getId(), page, 
                new Callback<List<Charactor>>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, "load_error_message: " + e.getMessage());
            }

            @Override
            public void success(List<Charactor> charactors, Response response) {
                renderView(charactors, page, shouldClearAll);
            }
        });
    }

    private void renderView(List<Charactor> charactors, int page, boolean shouldClearAll) {
        if (page == 1) {
            mLoading.setVisibility(View.GONE);
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
