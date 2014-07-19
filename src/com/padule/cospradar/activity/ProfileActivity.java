package com.padule.cospradar.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.InjectView;
import butterknife.OnClick;

import com.google.android.gms.ads.AdView;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.adapter.ProfileCharactorsAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.Result;
import com.padule.cospradar.data.User;
import com.padule.cospradar.event.CharactorDeleteEvent;
import com.padule.cospradar.event.CurrentCharactorSelectedEvent;
import com.padule.cospradar.fragment.CharactorDeleteDialogFragment;
import com.padule.cospradar.fragment.CharactorEnableConfirmDialogFragment;
import com.padule.cospradar.service.ApiService;
import com.padule.cospradar.util.AdmobUtils;
import com.padule.cospradar.util.AppUtils;

import de.greenrobot.event.EventBus;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = ProfileActivity.class.getName();
    private static final int POS_MENU_CHARACTOR_EDIT = 0;
    private static final int POS_MENU_CHARACTOR_DELETE = 1;
    private static final int POS_MENU_CANCEL = 2;

    @InjectView(R.id.listview_charactors) ListView mListView;
    @InjectView(R.id.container_admob) RelativeLayout mContainerAdmob;
    @InjectView(R.id.container_empty) View mContainerEmpty;

    private AdView adView;
    private View mLoading;
    private ProfileCharactorsAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User)getIntent().getSerializableExtra(User.class.getName());
        setContentView(R.layout.activity_profile);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    public static void start(Context context, User user) {
        if (user != null) {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(User.class.getName(), user);
            context.startActivity(intent);
        }
    }

    public static void start(final Context context, int userId) {
        if (AppUtils.isLoginUser(userId)) {
            start(context, AppUtils.getUser());
            return;
        }

        final Dialog dialog = AppUtils.makeLoadingDialog(context);
        dialog.show();

        MainApplication.API.getUsers(userId, new Callback<User>() {
            @Override
            public void failure(RetrofitError e) {
                dialog.dismiss();
                AppUtils.showToast(R.string.error_raised, context);
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(User user, Response response) {
                dialog.dismiss();
                start(context, user);
            }
        });
    }

    @Override
    protected void initView() {
        initActionBar();
        initListView();
        initAdmob();
    }

    private void initAdmob() {
        adView = AdmobUtils.createAdViewInProfileFooter(this);
        AdmobUtils.loadBanner(adView, mContainerAdmob);
    }

    private void initListView() {
        initLoading();
        adapter = new ProfileCharactorsAdapter(this);
        mListView.addHeaderView(mLoading);
        mListView.setAdapter(adapter);
        initListViewListener();
        loadData(0);
    }

    private void initLoading() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoading = inflater.inflate(R.layout.ui_loading, null);
    }

    private void hideLoading() {
        mListView.removeHeaderView(mLoading);
    }

    private void initListViewListener() {
        // TODO fix server count.
        // mListView.setOnScrollListener(new EndlessScrollListener() {
        //     @Override
        //     public void onLoadMore(int page, int totalItemsCount) {
        //         loadData(page);
        //     }
        // });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                switch (view.getId()) {
                case R.id.img_charactor_clicker:
                    PhotoPreviewActivity.start(ProfileActivity.this, adapter.getItem(pos));
                    break;
                case R.id.img_menu:
                    showMenu(adapter.getItem(pos));
                    break;
                case R.id.img_chat:
                    ChatBoardActivity.start(ProfileActivity.this, adapter.getItem(pos));
                    break;
                case R.id.txt_charactor_enabled_clicker:
                    if (AppUtils.isLoginUser(user)) {
                        CharactorEnableConfirmDialogFragment.show(ProfileActivity.this, adapter.getItem(pos));
                    }
                    break;
                }
            }
        });
    }

    private void showMenu(final Charactor charactor) {
        String[] items = getResources().getStringArray(R.array.profile_charactor_menu);

        new AlertDialog.Builder(this)
        .setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                case POS_MENU_CHARACTOR_EDIT:
                    CharactorSettingActivity.start(ProfileActivity.this, charactor);
                    break;
                case POS_MENU_CHARACTOR_DELETE:
                    CharactorDeleteDialogFragment.show(ProfileActivity.this, charactor);
                    break;
                case POS_MENU_CANCEL:
                    dialog.dismiss();
                    break;
                default:
                    break;
                }
            }
        }).create().show();
    }

    public void onEvent(CharactorDeleteEvent event) {
        deleteCharactor(event.charactor);
    }

    public void onEvent(CurrentCharactorSelectedEvent event) {
        updateCharactor(event.charactor);
    }

    private void deleteCharactor(final Charactor charactor) {
        final Dialog dialog = AppUtils.makeLoadingDialog(this);
        dialog.show();

        MainApplication.API.deleteCharactors(charactor.getId(), 
                new Callback<Result>() {
            @Override
            public void failure(RetrofitError e) {
                Log.d(TAG, "delete_error: " + e.getMessage());
                dialog.dismiss();
                AppUtils.showToast(R.string.charactor_delete_failed, ProfileActivity.this);
            }

            @Override
            public void success(Result result, Response response) {
                dialog.dismiss();
                AppUtils.showToast(R.string.charactor_delete_succeeded, ProfileActivity.this);
                if (AppUtils.isCurrentCharactor(charactor)) {
                    AppUtils.setCharactor(null);
                }
                ProfileActivity.this.adapter.remove(charactor);
            }
        });
    }

    private void loadData(int page) {
        MainApplication.API.getCharactors(createParams(user.getId(), page), 
                new Callback<List<Charactor>>() {
            @Override
            public void failure(RetrofitError e) {
                Log.e(TAG, e.getMessage() + "");
            }

            @Override
            public void success(List<Charactor> charactors, Response response) {
                hideLoading();
                renderView(charactors);
            }
        });
    }

    private Map<String, String> createParams(int userId, int page) {
        Map<String, String> params = new HashMap<String, String>();
        if (userId > 0) {
            params.put(ApiService.PARAM_USER_ID, userId + "");
            params.put(ApiService.PARAM_DESC, ApiService.PARAM_IS_ENABLED);
        }
        params.put(ApiService.PARAM_PAGE, page + "");
        params.put(ApiService.PARAM_LATITUDE, AppUtils.getLatitude() + "");
        params.put(ApiService.PARAM_LONGITUDE, AppUtils.getLongitude() + "");
        params.put(ApiService.PARAM_LIMIT, "300"); // FIXME 

        return params;
    }

    private void renderView(List<Charactor> charactors) {
        if (charactors != null && !charactors.isEmpty() && adapter != null) {
            adapter.addAll(charactors);
            mListView.setVisibility(View.VISIBLE);
            mContainerEmpty.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            mContainerEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(getString(R.string.profile));
        bar.setIcon(R.drawable.ic_launcher);
        bar.setTitle(user.getScreenName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        case R.id.item_setting:
            SettingActivity.start(this);
            break;
        case R.id.item_add:
            CharactorSettingActivity.start(this);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (AppUtils.isLoginUser(user)) {
            getMenuInflater().inflate(R.menu.activity_profile, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void updateCharactor(Charactor charactor) {
        if (charactor == null) return;

        final Dialog dialog = AppUtils.makeLoadingDialog(this);
        dialog.show();

        int updateIsEnabled = charactor.isEnabled() ? 0 : 1;
        MainApplication.API.putCharactors(charactor.getId(), updateIsEnabled, 
                new Callback<Charactor>() {
            @Override
            public void failure(RetrofitError e) {
                Log.d(TAG, "update_error: " + e.getMessage());
                dialog.dismiss();
                AppUtils.showToast(R.string.error_raised, ProfileActivity.this);
            }

            @Override
            public void success(Charactor charactor, Response response) {
                dialog.dismiss();
                if (charactor.isEnabled()) {
                    AppUtils.setCharactor(charactor);
                }
                updateView(charactor);
            }
        });
    }

    private void updateView(Charactor currentCharactor) {
        final int size = adapter.getCount();
        for (int i = 0; i < size; i++) {
            Charactor c = adapter.getItem(i);
            if (c == null) return;

            if (c.getId() == currentCharactor.getId()) {
                c.setIsEnabled(currentCharactor.isEnabled());
            } else {
                c.setIsEnabled(false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.txt_charactor_add)
    void onClickTxtCharactorAdd() {
        CharactorSettingActivity.start(this);
    }

}
