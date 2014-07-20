package com.padule.cospradar.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.CommentsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.ReverseScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.data.UnreadGcmCounts;
import com.padule.cospradar.event.CommentCharactorIconClickedEvent;
import com.padule.cospradar.event.CommentCharactorSelectedEvent;
import com.padule.cospradar.event.CommentCloseEvent;
import com.padule.cospradar.event.CommentSentEvent;
import com.padule.cospradar.event.SendBtnClickedEvent;
import com.padule.cospradar.fragment.CharactorSelectDialogFragment;
import com.padule.cospradar.fragment.ChatBoardDismissDialogFragment;
import com.padule.cospradar.fragment.EditSuggestDialogFragment;
import com.padule.cospradar.fragment.ShareDialogFragment;
import com.padule.cospradar.service.ApiService;
import com.padule.cospradar.ui.CommentFooter;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

import de.greenrobot.event.EventBus;

public class ChatBoardActivity extends BaseActivity {

    private static final String TAG = ChatBoardActivity.class.getName();

    @InjectView(R.id.listview_chat) PullToRefreshListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;
    @InjectView(R.id.loading) View mLoading;
    @InjectView(R.id.footer_comment) CommentFooter mFooter;

    private CommentsAdapter adapter;
    private Charactor charactor;
    private Charactor commentCharactor;
    private List<Charactor> userCharactors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardUtils.initHidden(this);
        setContentView(R.layout.activity_chat_board);

        clearUnreadGcmCount();
        EventBus.getDefault().register(this);
    }

    private void clearUnreadGcmCount() {
        if (charactor != null) {
            UnreadGcmCounts.getInstance().clearFromChatBoard(charactor.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (mFooter.hasCommentInInput()) {
            ChatBoardDismissDialogFragment.show(this);
        } else {
            super.onBackPressed();
        }
    }

    public void onEvent(SendBtnClickedEvent event) {
        onClickSendBtn(event.text);
    }

    public void onEvent(CommentCharactorIconClickedEvent event) {
        ProfileActivity.start(this, event.charactor.getUserId());
    }

    public void onEvent(CommentCharactorSelectedEvent event) {
        this.commentCharactor = event.charactor;
    }

    public void onEvent(CommentCloseEvent event) {
        finish();
    }
    private void onClickSendBtn(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (commentCharactor == null) {
            loadUserCharactors();
            return;
        }
        CharactorComment comment = new CharactorComment(charactor.getId(), text, commentCharactor);
        adapter.add(comment);
        adapter.notifyDataSetChanged();
        mFooter.clearText();
        uploadComment(comment);
    }

    public static void start(BaseActivity activity, Charactor charactor) {
        Intent intent = new Intent(activity, ChatBoardActivity.class);
        intent.putExtra(Charactor.class.getName(), charactor);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void start(final BaseActivity activity, final int charactorId) {
        if (charactorId <= 0) return;

        MainApplication.API.getCharactor(charactorId, new Callback<Charactor>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, "load_error_message: " + e.getMessage());
                AppUtils.showToast(MainApplication.getContext().getString(R.string.error_raised), 
                        MainApplication.getContext());
            }

            @Override
            public void success(Charactor charactor, Response response) {
                start(activity, charactor);
            }
        });
    }

    @Override
    protected void initView() {
        loadUserCharactors();
        setCharactor();
        initActionBar(charactor);
    }

    @OnClick(R.id.root_chat)
    public void onClickListViewChat() {
        KeyboardUtils.hide(this);
    }

    private void initListView(List<Charactor> charactors) {
        adapter = new CommentsAdapter(this, charactor, charactors);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mListView.setAdapter(adapter);
        loadData(1);
        initListViewListener();
    }

    private void initListViewListener() {
        mListView.setOnScrollListener(new ReverseScrollListener() {
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
    }

    private void loadData(int page) {
        loadData(page, false);
    }

    private void loadData(final int page, final boolean shouldClearAll) {
        MainApplication.API.getCharactorComments(charactor.getId(), page, 
                new Callback<List<CharactorComment>>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, "load_error_message: " + e.getMessage());
            }

            @Override
            public void success(List<CharactorComment> comments, Response response) {
                renderView(comments, page, shouldClearAll);
            }
        });
    }

    private void renderView(List<CharactorComment> comments, int page, boolean shouldClearAll) {
        if (page == 1) {
            mLoading.setVisibility(View.GONE);
        }

        if (comments != null && !comments.isEmpty()) {
            if (shouldClearAll) {
                adapter.clear();
                mListView.onRefreshComplete();
            }
            if (adapter.isEmpty()) {
                adapter.addAll(comments);
                mListView.getRefreshableView().setSelection(adapter.getCount()+1);
                scrollToBottom();
            } else {
                ListIterator<CharactorComment> itr = comments.listIterator(comments.size());
                while (itr.hasPrevious()) {
                    adapter.insert(itr.previous(), 0);
                }
                mListView.getRefreshableView().setSelection(comments.size()+1);
            }
        } else {
            int visibility = page == 1 ? View.VISIBLE : View.GONE;
            mContainerEmpty.setVisibility(visibility);
        }
    }

    private void scrollToBottom() {
        if (mListView != null) {
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        mListView.getRefreshableView().setSelection(adapter.getCount()+1);
                    }
                }
            });
        }
    }

    @OnClick(R.id.container_empty)
    public void onClickContainerEmpty() {
        KeyboardUtils.hide(this);
    }

    private void setCharactor() {
        this.charactor = (Charactor)getIntent()
                .getSerializableExtra(Charactor.class.getName());
    }

    private void initActionBar(Charactor charactor) {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(charactor.getNameAndTitle());

        ImageUtils.setActionBarIcon(this, bar, charactor.getImageUrl());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
            if (mFooter.hasCommentInInput()) {
                ChatBoardDismissDialogFragment.show(this);
            } else {
                finish();
            }
            break;
        case R.id.item_share:
            ShareDialogFragment.show(this);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat_board, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void uploadComment(final CharactorComment orgComment) {
        MainApplication.API.postCharactorComments(orgComment, new Callback<CharactorComment>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, "create_error_message: " + e.getMessage());
                AppUtils.showToast(ChatBoardActivity.this.getString(R.string.comment_send_failed), ChatBoardActivity.this);
            }

            @Override
            public void success(CharactorComment comment, Response response) {
                renderView(comment, orgComment);
                EventBus.getDefault().post(new CommentSentEvent(charactor));
            }
        });
    }

    private void renderView(CharactorComment comment, CharactorComment orgComment) {
        if (comment != null) {
            mContainerEmpty.setVisibility(View.GONE);
            adapter.remove(orgComment);
            adapter.add(comment);
            adapter.notifyDataSetChanged();
            charactor.setLatestComment(comment);
            scrollToBottom();
        }
    }

    private void loadUserCharactors() {
        if (AppUtils.getUser() == null) return;

        if (userCharactors != null && !userCharactors.isEmpty()) {
            checkCommentCharactor(userCharactors);
            return;
        }

        final Dialog dialog = AppUtils.makeLoadingDialog(this);
        dialog.show();

        MainApplication.API.getCharactors(createParams(AppUtils.getUser().getId()), 
                new Callback<List<Charactor>>() {
            @Override
            public void failure(RetrofitError e) {
                dialog.dismiss();
                AppUtils.showToast(R.string.error_raised, ChatBoardActivity.this);
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(List<Charactor> charactors, Response response) {
                dialog.dismiss();
                ChatBoardActivity.this.userCharactors = charactors;
                checkCommentCharactor(charactors);
                initListView(charactors);
            }
        });
    }

    private void checkCommentCharactor(List<Charactor> charactors) {
        if (charactors.isEmpty()) {
            EditSuggestDialogFragment.show(this);
        } else if (charactors.size() == 1) {
            this.commentCharactor = charactors.get(0);
        } else {
            CharactorSelectDialogFragment.show(this, new ArrayList<Charactor>(charactors));
        }
    }

    private Map<String, String> createParams(int userId) {
        Map<String, String> params = new HashMap<String, String>();
        if (userId > 0) {
            params.put(ApiService.PARAM_USER_ID, userId + "");
            params.put(ApiService.PARAM_DESC, "is_enabled");
        }
        params.put(ApiService.PARAM_LATITUDE, AppUtils.getLatitude() + "");
        params.put(ApiService.PARAM_LONGITUDE, AppUtils.getLongitude() + "");

        return params;
    }

}
