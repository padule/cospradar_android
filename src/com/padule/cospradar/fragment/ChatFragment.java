package com.padule.cospradar.fragment;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONArray;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;

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
import com.padule.cospradar.util.AppUtils;

public class ChatFragment extends BaseFragment {

    @InjectView(R.id.listview_chat) ListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;
    @InjectView(R.id.loading) View mLoading;

    CommentListAdapter adapter;
    Charactor charactor;

    public static ChatFragment newInstance(Charactor charactor) {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Charactor.class.getName(), charactor);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.inject(this, view);
        aq = new AQuery(getActivity());

        setCharactor();
        initListView();

        return view;
    }

    private void setCharactor() {
        if (getArguments() != null) {
            charactor = (Charactor)getArguments().getSerializable(Charactor.class.getName());
        } else {
            charactor = AppUtils.getCharactor();
        }
    }

    private void initListView() {
        adapter = new CommentListAdapter(getActivity());
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
                scrollToBottom();
            } else {
                ListIterator<CharactorComment> itr = comments.listIterator(comments.size());
                while (itr.hasPrevious()) {
                    adapter.insert(itr.previous(), 0);
                }
                mListView.setSelection(comments.size() + 1);
            }

        }
    }

    private void scrollToBottom() {
        if (mListView != null) {
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null) {
                        mListView.setSelection(adapter.getCount());
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
