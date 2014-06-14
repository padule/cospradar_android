package com.padule.cospradar.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

public class CharactorSettingActivity extends BaseActivity {

    private static final String TAG = CharactorSettingActivity.class.getName();

    @InjectView(R.id.img_charactor_left) ImageView mImgCharactor;
    @InjectView(R.id.edit_name) EditText mEditName;
    @InjectView(R.id.edit_title) EditText mEditTitle;

    private Charactor charactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charactor_setting);
    }

    public static void start(Activity activity) {
        final Intent intent = new Intent(activity, CharactorSettingActivity.class);
        activity.startActivityForResult(intent, Constants.REQ_ACTIVITY_CHARACTOR_SETTING);
    }

    @Override
    protected void initView() {
        initCharactor();
        initActionBar();
        bindData();
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(getString(R.string.charactor_setting));
    }

    private void initCharactor() {
        charactor = AppUtils.getCharactor();
        if (charactor == null) {
            charactor = new Charactor();
        }
    }

    private void bindData() {
        mEditName.setText(charactor.getName());
        mEditTitle.setText(charactor.getTitle());
        initCharactorImage(charactor.getImageUrl());
    }

    private void initCharactorImage(String url) {
        if (url != null) {
            ImageUtils.displayRoundedImage(url, mImgCharactor);
        } else {
            mImgCharactor.setImageResource(R.drawable.ic_no_user_rounded);
        }
    }

    @OnClick(R.id.clicker_img_charactor)
    void onClickClickerImgCharactor() {
        KeyboardUtils.hide(this);
        ImageUtils.showChooserDialog(this);
    }

    @OnClick(R.id.root)
    void onClickRoot() {
        KeyboardUtils.hide(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent == null) {
            return;
        }

        Uri uri = intent.getData();

        switch (requestCode) {
        case Constants.REQ_ACTIVITY_CAMERA:
            if (resultCode == Activity.RESULT_OK) {
                Intent i = ImageUtils.createAviaryIntent(this, uri);
                startActivityForResult(i, Constants.REQ_ACTIVITY_AVIARY_CAMERA);
            }
            break;
        case Constants.REQ_ACTIVITY_GALLERY:
            if (resultCode == Activity.RESULT_OK) {
                Intent i = ImageUtils.createAviaryIntent(this, uri);
                startActivityForResult(i, Constants.REQ_ACTIVITY_AVIARY_GALLERY);
            }
            break;
        case Constants.REQ_ACTIVITY_AVIARY_CAMERA:
        case Constants.REQ_ACTIVITY_AVIARY_GALLERY:
            if (resultCode == Activity.RESULT_OK) {
                setCharactorImage(uri);
            }
            break;
        }
    }

    private void setCharactorImage(Uri uri) {
        String path = ImageUtils.getPath(this, uri);
        this.charactor.setImage(path);
        initCharactorImage(ImageUtils.convertToValidUrl(path));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_charactor_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.item_ok:
            if (validate()) {
                KeyboardUtils.hide(this);
                saveCharactor();
            }
            break;
        case android.R.id.home:
            if (isEditing()) {
            } else {
                finish();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCharactor() {
        String url = AppUrls.getCharactorsCreate();
        Dialog dialog = AppUtils.makeSendingDialog(this);
        final Map<String, Object> params = createParams();
        Log.d(TAG, "create_params: " + params.toString());

        aq.progress(dialog).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d(TAG, "create_url: " + url);
                saveCharactorCallBack(json, status);
            }
        });
    }

    private void saveCharactorCallBack(JSONObject json, AjaxStatus status) {
        if (json != null) {
            Log.d(TAG, json.toString());
            Gson gson = new GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create();
            Charactor charactor = gson.fromJson(json.toString(), Charactor.class);
            this.charactor = charactor;
            AppUtils.setCharactor(charactor);
            showToast(R.string.charactor_edit_succeeded);
        } else {
            if (AppUtils.isMockMode()) {
                // FIXME Remove me. Just implement for test.
                this.charactor = MockFactory.createCharactor(mEditName.getText().toString(), 
                        mEditTitle.getText().toString(), charactor.getImageUrl());
                AppUtils.setCharactor(charactor);
                showToast(R.string.charactor_edit_succeeded);
            } else {
                Log.e(TAG, "create_error_message: " + status.getMessage());
                showToast(R.string.charactor_edit_failed);
            }
        }
    }

    private void showToast(int resId) {
        AppUtils.showToast(getString(resId), this);
    }

    private Map<String, Object> createParams() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AppUrls.PARAM_NAME, mEditName.getText().toString());
        params.put(AppUrls.PARAM_TITLE, mEditTitle.getText().toString());
        params.put(AppUrls.PARAM_USER_ID, AppUtils.getUser().getId());

        if (charactor.getImageUrl() != null) {
            params.put(AppUrls.PARAM_IMAGE, new File(charactor.getImage()));
        }

        return params;
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mEditName)) {
            mEditName.setError(getString(R.string.charactor_validate_name));
            showToast(R.string.invalidate_input);
            return false;
        }
        if (TextUtils.isEmpty(mEditTitle)) {
            mEditTitle.setError(getString(R.string.charactor_validate_title));
            showToast(R.string.invalidate_input);
            return false;
        }
        if (!isEditing()) {
            showToast(R.string.not_changed_input);
            return false;
        }
        return true;
    }

    private boolean isEditing() {
        return !charactor.getName().equals(mEditName.getText().toString())
                || !charactor.getTitle().equals(mEditTitle.getText().toString())
                || !(AppUtils.getCharactor() != null && AppUtils.getCharactor().getImageUrl().equals(charactor.getImageUrl()));
    }

}
