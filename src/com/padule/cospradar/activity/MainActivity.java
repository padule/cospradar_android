package com.padule.cospradar.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.adapter.DrawerItemListAdapter;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseLocationListener;
import com.padule.cospradar.data.DrawerItem;
import com.padule.cospradar.fragment.CharactorEditFragment;
import com.padule.cospradar.fragment.ChatListFragment;
import com.padule.cospradar.fragment.CommentFragment;
import com.padule.cospradar.fragment.SearchFragment;
import com.padule.cospradar.ui.DrawerHeader;
import com.padule.cospradar.util.AppUtils;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.drawer_list) ListView mDrawerListView;
    private DrawerHeader mHeader;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerItemListAdapter adapter;
    private CountDownTimer keyEventTimer;
    private boolean backKeyPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BaseLocationListener listener = new BaseLocationListener(this);
        listener.start();
    }

    @Override
    protected void initView() {
        initActionBar();
        initDrawer();
        initBackButton();
        showFragment(new SearchFragment(), R.id.content_frame);
    }

    private void initBackButton() {
        keyEventTimer = new CountDownTimer(1000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                //
            }
            @Override
            public void onFinish() {
                backKeyPressed = false;
            }
        };
    }

    @Override  
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override  
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setHomeButtonEnabled(true);
    }

    public void initDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }
            @Override
            public void onDrawerClosed(View view) {
                replaceFragment(mDrawerListView.getCheckedItemPosition());
                super.onDrawerClosed(view);
            }
            @Override
            public void onDrawerSlide(View view, float offset) {
                super.onDrawerSlide(view, offset);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
        createDrawerList();
    }

    private void replaceFragment(int pos) {
        if (pos <= 0) {
            showEditFragment();
        } else {
            DrawerItem drawerItem = (DrawerItem)mDrawerListView.getItemAtPosition(pos);
            showFragment(drawerItem.getFragmentPackage(), R.id.content_frame);
            setActionBarTitle(drawerItem.getTitle());
        }
    }

    public void showEditFragment() {
        showFragment(CharactorEditFragment.class.getName(), R.id.content_frame);
        setActionBarTitle(getString(R.string.charactor_edit_actionbar));
    }

    private void setActionBarTitle(String title) {
        ActionBar bar = getSupportActionBar();
        bar.setTitle(title);
    }

    private void createDrawerList() {
        if (mDrawerListView.getHeaderViewsCount() > 0) {
            mDrawerListView.removeHeaderView(mHeader);
        }
        mHeader = new DrawerHeader(this, AppUtils.getCharactor());
        mDrawerListView.addHeaderView(mHeader);

        List<DrawerItem> list = new ArrayList<DrawerItem>();
        list.add(new DrawerItem(getString(R.string.drawer_search), R.drawable.ic_drawer_search, SearchFragment.class.getName()));
        list.add(new DrawerItem(getString(R.string.drawer_my_chat), R.drawable.ic_drawer_my_chat, CommentFragment.class.getName()));
        list.add(new DrawerItem(getString(R.string.drawer_comment_chat), R.drawable.ic_drawer_comment_chat, ChatListFragment.class.getName()));

        adapter = new DrawerItemListAdapter(this, list);
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, final int pos, long id) {
                mDrawerListView.setItemChecked(pos, true);
                mDrawerLayout.closeDrawer(mDrawerListView);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (!backKeyPressed) {
                keyEventTimer.cancel();
                keyEventTimer.start();
                AppUtils.showToast(getString(R.string.back_btn_msg), this, Toast.LENGTH_SHORT);
                backKeyPressed = true;
                return false;
            }
            return super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

}
