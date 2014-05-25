package com.padule.cospradar.fragment;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.CommentListAdapter;
import com.padule.cospradar.base.BaseFragment;
import com.padule.cospradar.base.ReverseScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.ui.CommentFooter;
import com.padule.cospradar.ui.CommentFooter.FooterCommentListener;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

public class CommentFragment extends BaseFragment implements FooterCommentListener {

    @InjectView(R.id.listview_chat) ListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;
    @InjectView(R.id.loading) View mLoading;
    @InjectView(R.id.footer_comment) CommentFooter mFooter;

    CommentListAdapter adapter;
    Charactor charactor;

    public static CommentFragment newInstance(Charactor charactor) {
        CommentFragment fragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Charactor.class.getName(), charactor);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        ButterKnife.inject(this, view);
        aq = new AQuery(getActivity());

        setCharactor();
        initListView();
        mFooter.setListener(this);

        return view;
    }

    private void setCharactor() {
        if (getArguments() != null) {
            charactor = (Charactor)getArguments().getSerializable(Charactor.class.getName());
        } else {
            charactor = AppUtils.getCharactor();
        }
    }

    @OnClick(R.id.root_chat)
    public void onClickListViewChat() {
        KeyboardUtils.hide(getActivity());
    }

    @OnClick(R.id.container_empty)
    public void onClickContainerEmpty() {
        KeyboardUtils.hide(getActivity());
    }

    private void initListView() {
        adapter = new CommentListAdapter(getActivity());
        mListView.setAdapter(adapter);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
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
    }

    private void loadData(final int page) {
        String url = AppUrls.getCharactorCommentsIndex(charactor.getId(), page);
        aq.ajax(url, JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (page == 1) {
                    mLoading.setVisibility(View.GONE);
                }
                loadCallback(json);
            }
        });
    }

    private void loadCallback(JSONArray json) {
        List<CharactorComment> comments;
        if (json != null) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            Type collectionType = new TypeToken<List<CharactorComment>>() {}.getType();
            comments = gson.fromJson(json.toString(), collectionType);
        } else {
            // FIXME implement using mock.
            comments = MockFactory.getComments(charactor);
        }

        if (comments != null && !comments.isEmpty() && adapter != null) {
            if (adapter.isEmpty()) {
                adapter.addAll(comments);
                mListView.setSelection(adapter.getCount());
            } else {
                ListIterator<CharactorComment> itr = comments.listIterator(comments.size());
                while (itr.hasPrevious()) {
                    adapter.insert(itr.previous(), 0);
                }
                mListView.setSelection(comments.size() + 1);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        final String url = AppUrls.getCharactorCommentsCreate(charactor.getId());
        aq.ajax(url, createParams(comment), JSONObject.class, new AjaxCallback<JSONObject>() {
            public void callback(String url, JSONObject json, AjaxStatus status) {
                showComment(json, comment);
            }
        });
    }

    private void showComment(JSONObject json, final CharactorComment oldComment) {
        if (json != null) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            CharactorComment comment = gson.fromJson(json.toString(), CharactorComment.class);
            if (comment != null) {
                adapter.remove(oldComment);
                adapter.add(comment);
            }
        }
    }

    private Map<String, Object> createParams(CharactorComment comment) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AppUrls.PARAM_TEXT, comment.getText());
        params.put(AppUrls.PARAM_COMMENT_CHARACTOR_ID, comment.getText());
        params.put(AppUrls.PARAM_CHARACTOR_ID, comment.getCharactorId());
        return params;
    }

}
