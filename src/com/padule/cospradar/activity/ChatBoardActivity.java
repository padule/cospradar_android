package com.padule.cospradar.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

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

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.CommentsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.ReverseScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.fragment.EditSuggestDialogFragment;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.ui.CommentFooter;
import com.padule.cospradar.ui.CommentFooter.FooterCommentListener;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

public class ChatBoardActivity extends BaseActivity implements FooterCommentListener {

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
    }

    public static void start(BaseActivity activity, Charactor charactor) {
        if (AppUtils.getCharactor() == null) {
            EditSuggestDialogFragment.show(activity);
        } else {
            final Intent intent = new Intent(activity, ChatBoardActivity.class);
            intent.putExtra(Charactor.class.getName(), charactor);
            activity.startActivityForResult(intent, Constants.REQ_ACTIVITY_CHAT_BOARD);
        }
    }

    @Override
    protected void initView() {
        setCharactor();
        initActionBar(charactor);
        initFooter();
        initListView();
    }

    private void initFooter() {
        mFooter.setListener(this);
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
        if (page == 1) {
            mLoading.setVisibility(View.GONE);
        }
        String url = AppUrls.getCharactorCommentsIndex(charactor.getId(), page);
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                loadCallback(json, status, page, shouldClearAll);
            }
        });
    }

    private void loadCallback(JSONArray json, AjaxStatus status, int page, boolean shouldClearAll) {
        List<CharactorComment> comments = new ArrayList<CharactorComment>();
        if (json != null) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            Type collectionType = new TypeToken<List<CharactorComment>>() {}.getType();
            comments = gson.fromJson(json.toString(), collectionType);
        } else {
            if (AppUtils.isMockMode()) {
                comments = MockFactory.getComments(charactor);
            } else {
                Log.e(TAG, "load_error_message: " + status.getMessage());
            }
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
        MainApplication.imageLoader.loadImage(charactor.getImageUrl(), new ImageLoadingListener() {
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickBtnComment(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        CharactorComment comment = CharactorComment.createTmp(charactor.getId(), text);
        adapter.add(comment);
        uploadComment(comment);
    }

    private void uploadComment(final CharactorComment comment) {
        final String url = AppUrls.getCharactorCommentsCreate();
        Map<String, Object> params = createParams(comment);
        Log.d(TAG, "create_params: " + params.toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d(TAG, "create_url: " + url);
                showComment(json, comment, status);
            }
        });
    }

    private void showComment(JSONObject json, final CharactorComment oldComment, AjaxStatus status) {
        if (json != null) {
            Log.d(TAG, "create_json: " + json.toString());
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            CharactorComment comment = gson.fromJson(json.toString(), CharactorComment.class);
            if (comment != null) {
                mContainerEmpty.setVisibility(View.GONE);
                adapter.remove(oldComment);
                adapter.add(comment);
                scrollToBottom();
            }
        } else {
            Log.e(TAG, "create_error_message: " + status.getMessage());
            if (!AppUtils.isMockMode()) {
                adapter.remove(oldComment);
            }
            AppUtils.showToast(getString(R.string.comment_send_failed), this);
        }
    }

    private Map<String, Object> createParams(CharactorComment comment) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AppUrls.PARAM_TEXT, comment.getText());
        params.put(AppUrls.PARAM_COMMENT_CHARACTOR_ID, comment.getCommentCharactorId());
        params.put(AppUrls.PARAM_CHARACTOR_ID, comment.getCharactorId());
        return params;
    }

}
