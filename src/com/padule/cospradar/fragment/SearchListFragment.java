package com.padule.cospradar.fragment;

import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.padule.cospradar.activity.CommentActivity;
import com.padule.cospradar.adapter.SearchListAdapter;
import com.padule.cospradar.base.BaseFragment;
import com.padule.cospradar.base.EndlessScrollListener;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.mock.MockFactory;

public class SearchListFragment extends BaseFragment {

    @InjectView(R.id.listview_search) ListView mListView;
    @InjectView(R.id.container_empty) View mContainerEmpty;
    @InjectView(R.id.loading) View mLoading;

    SearchListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);
        aq = new AQuery(getActivity());
        ButterKnife.inject(this, view);

        initListView();

        return view;
    }

    private void initListView() {
        adapter = new SearchListAdapter(getActivity());
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
                final Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra(Charactor.class.getName(), adapter.getItem(pos));
                startActivityForResult(intent, Constants.REQ_ACTIVITY_CHAT);
            }
        });
    }

    private void loadData(final int page) {
        aq.ajax(AppUrls.getCharactorsIndex(page), JSONArray.class, new AjaxCallback<JSONArray>() {
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
        List<Charactor> charactors;
        if (json != null) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            Type collectionType = new TypeToken<List<Charactor>>() {}.getType();
            charactors = gson.fromJson(json.toString(), collectionType);
        } else {
            // FIXME implement using mock.
            charactors = MockFactory.getCharactors();
        }

        if (charactors != null && !charactors.isEmpty() && adapter != null) {
            adapter.addAll(charactors);
        }
    }

}
