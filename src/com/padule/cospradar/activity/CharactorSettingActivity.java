package com.padule.cospradar.activity;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

import com.padule.cospradar.Constants;
import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

public class CharactorSettingActivity extends BaseActivity {

    private static final String TAG = CharactorSettingActivity.class.getName();
    private enum Mode { CREATE, EDIT };

    @InjectView(R.id.img_charactor) ImageView mImgCharactor;
    @InjectView(R.id.edit_name) EditText mEditName;
    @InjectView(R.id.edit_title) EditText mEditTitle;

    private Charactor charactor;
    private Mode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charactor_setting);
    }

    public static void start(Context context, Charactor charactor) {
        final Intent intent = new Intent(context, CharactorSettingActivity.class);
        intent.putExtra(Charactor.class.getName(), charactor);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        initMode();
        initActionBar();
        bindData();
    }

    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle(getString(R.string.charactor_setting));
        bar.setIcon(R.drawable.ic_launcher);
    }

    private void initMode() {
        charactor = (Charactor)getIntent().getSerializableExtra(Charactor.class.getName());
        mode = Mode.EDIT;
        if (charactor == null) {
            charactor = new Charactor();
            mode = Mode.CREATE;
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
                if (mode == Mode.CREATE) {
                    saveCharactor();
                } else {
                    updateCharactor();
                }
            }
            break;
        case android.R.id.home:
            finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCharactor() {
        final Dialog dialog = AppUtils.makeLoadingDialog(this);
        dialog.show();

        TypedFile file = null;
        if (charactor.getImageUrl() != null) {
            file = new TypedFile("image/*", new File(charactor.getImage()));
        }

        MainApplication.API.putCharactors(charactor.getId(), 
                new TypedString(mEditName.getText().toString()), 
                new TypedString(mEditTitle.getText().toString()), 
                new TypedString(AppUtils.getUser().getId() + ""), 
                file, 
                new Callback<Charactor>() {
            @Override
            public void failure(RetrofitError e) {
                Log.d(TAG, "update_error: " + e.getMessage());
                dialog.dismiss();
                AppUtils.showToast(R.string.charactor_edit_failed, CharactorSettingActivity.this);
            }

            @Override
            public void success(Charactor charactor, Response response) {
                dialog.dismiss();
                CharactorSettingActivity.this.charactor = charactor;
                AppUtils.showToast(R.string.charactor_edit_succeeded, CharactorSettingActivity.this);
                AppUtils.setCharactor(charactor);
            }
        });
    }

    private void saveCharactor() {
        final Dialog dialog = AppUtils.makeSendingDialog(this);
        dialog.show();

        TypedFile file = null;
        if (charactor.getImageUrl() != null) {
            file = new TypedFile("image/*", new File(charactor.getImage()));
        }

        MainApplication.API.postCharactors(new TypedString(mEditName.getText().toString()), 
                new TypedString(mEditTitle.getText().toString()), 
                new TypedString(AppUtils.getUser().getId() + ""), 
                file, 
                new Callback<Charactor>() {
            @Override
            public void failure(RetrofitError e) {
                Log.d(TAG, "create_error: " + e.getMessage());
                dialog.dismiss();
                AppUtils.showToast(R.string.charactor_edit_failed, CharactorSettingActivity.this);
            }

            @Override
            public void success(Charactor charactor, Response response) {
                dialog.dismiss();
                CharactorSettingActivity.this.charactor = charactor;
                AppUtils.setCharactor(charactor);
                AppUtils.showToast(R.string.charactor_create_succeeded, CharactorSettingActivity.this);
            }
        });
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mEditName)) {
            mEditName.setError(getString(R.string.charactor_validate_name));
            AppUtils.showToast(R.string.invalidate_input, this);
            return false;
        }
        if (TextUtils.isEmpty(mEditTitle)) {
            mEditTitle.setError(getString(R.string.charactor_validate_title));
            AppUtils.showToast(R.string.invalidate_input, this);
            return false;
        }
        if (!isEditing()) {
            AppUtils.showToast(R.string.not_changed_input, this);
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
