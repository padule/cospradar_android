package com.padule.cospradar.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.event.LogoutBtnClickedEvent;
import com.padule.cospradar.event.UserDeleteEvent;
import com.padule.cospradar.fragment.LogoutDialogFragment;
import com.padule.cospradar.fragment.RatingDialogFragment;
import com.padule.cospradar.fragment.ShareDialogFragment;
import com.padule.cospradar.fragment.UserDeleteDialogFragment;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.PrefUtils;

import de.greenrobot.event.EventBus;

public class SettingActivity extends BaseActivity {

    @InjectView(R.id.txt_version) TextView mTxtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context) {
        final Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        initActionBar();
        initVersion();
    }

    private void initVersion() {
        mTxtVersion.setText(AppUtils.getVersionName(this));
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(getString(R.string.setting));
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

    @OnClick(R.id.txt_item_charactor_add)
    void onClickItemCharactorAdd() {
        CharactorSettingActivity.start(this, null);
    }

    @OnClick(R.id.txt_item_charactor_set)
    void onClickItemCharactorSet() {
        CharactorChooserActivity.start(this);
    }

    @OnClick(R.id.txt_item_evaluate)
    void onClickItemEvaluate() {
        RatingDialogFragment.show(this);
    }

    @OnClick(R.id.txt_item_share)
    void onClickItemShare() {
        ShareDialogFragment.show(this);
    }

    @OnClick(R.id.txt_item_tutorial)
    void onClickItemTutorial() {
        PrefUtils.put(PrefUtils.KEY_TUTORIAL_SHOWN, false);
        MainActivity.start(this);
    }

    @OnClick(R.id.txt_item_inquery)
    void onClickItemInquery() {
        showInqueryMenu();
    }

    @OnClick(R.id.txt_item_terms)
    void onClickItemTerms() {
        WebViewActivity.start(this, Constants.TERMS_URL, getString(R.string.setting_terms));
    }

    @OnClick(R.id.txt_item_privacy)
    void onClickItemPrivacy() {
        WebViewActivity.start(this, Constants.PRIVACY_URL, getString(R.string.setting_privacy));
    }

    @OnClick(R.id.txt_item_account_delete)
    void onClickItemAccountDelete() {
        UserDeleteDialogFragment.show(this);
    }

    @OnClick(R.id.btn_logout)
    void onClickBtnLogout() {
        LogoutDialogFragment.show(this);
    }

    public void onEvent(LogoutBtnClickedEvent event) {
        AppUtils.logout(this);
        finish();
    }

    public void onEvent(UserDeleteEvent event) {
        AppUtils.logout(this);
        finish();
    }

    private void openMailChooser(String subject) {
        int userId = AppUtils.getUser().getId();
        String version = AppUtils.getVersionName(this);
        String text = getString(R.string.setting_inquery_message, userId, version);
        String[] mails = new String[] { Constants.INQUERY_MAIL };

        AppUtils.openMailChooser(this, text, mails, subject);
    }

    private void showInqueryMenu() {
        final String[] items = getResources().getStringArray(R.array.inquery_menu);

        new AlertDialog.Builder(this)
        .setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = items[which];
                String subject = SettingActivity.this.getString(R.string.setting_inquery_subject, title);
                openMailChooser(subject);
            }
        }).create().show();
    }

}
