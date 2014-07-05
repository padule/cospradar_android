package com.padule.cospradar.activity;

import java.util.List;
import java.util.ListIterator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.CommentsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.ReverseScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.event.CommentCloseEvent;
import com.padule.cospradar.event.CommentSentEvent;
import com.padule.cospradar.event.SendBtnClickedEvent;
import com.padule.cospradar.fragment.ChatBoardDismissDialogFragment;
import com.padule.cospradar.fragment.EditSuggestDialogFragment;
import com.padule.cospradar.ui.CommentFooter;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

import de.greenrobot.event.EventBus;

public class ChatBoardActivity extends BaseActivity {

    private static final String TAG = ChatBoardActivity.class.getName();
    private static final int ICON_SIZE = 100;

    @InjectView(R.id.listview_chat) PullToRefreshListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;
    @InjectView(R.id.loading) View mLoading;
    @InjectView(R.id.footer_comment) CommentFooter mFooter;

    private CommentsAdapter adapter;
    private Charactor charactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardUtils.initHidden(this);
        setContentView(R.layout.activity_chat_board);
        EventBus.getDefault().register(this);
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

    public void onEvent(CommentCloseEvent event) {
        finish();
    }
    private void onClickSendBtn(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        CharactorComment comment = CharactorComment.createTmp(charactor.getId(), text);
        adapter.add(comment);
        uploadComment(comment);
    }

    public static void start(BaseActivity activity, Charactor charactor) {
        if (AppUtils.getCharactor() == null) {
            EditSuggestDialogFragment.show(activity);
        } else {
            final Intent intent = new Intent(activity, ChatBoardActivity.class);
            intent.putExtra(Charactor.class.getName(), charactor);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void initView() {
        setCharactor();
        initActionBar(charactor);
        initListView();
    }

    @OnClick(R.id.root_chat)
    public void onClickListViewChat() {
        KeyboardUtils.hide(this);
    }

    private void initListView() {
        adapter = new CommentsAdapter(this, charactor);
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

        setActionBarIcon(bar, charactor);
    }

    private void setActionBarIcon(final ActionBar bar, Charactor charactor) {
        MainApplication.IMAGE_LOADER.loadImage(charactor.getImageUrl(), new ImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bmp) {
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, ICON_SIZE, ICON_SIZE, false);
                bar.setIcon(new BitmapDrawable(getResources(), scaledBmp));
            }
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                setEmptyIcon();
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason reason) {
                setEmptyIcon();
            }
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                setEmptyIcon();
            }

            private void setEmptyIcon() {
                bar.setIcon(new BitmapDrawable(getResources(), 
                        ImageUtils.createEmptyIconBmp(ChatBoardActivity.this, ICON_SIZE)));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mFooter.hasCommentInInput()) {
                ChatBoardDismissDialogFragment.show(this);
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishWithValidation() {

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
            charactor.setLatestComment(comment);
            scrollToBottom();
        }
    }

}
