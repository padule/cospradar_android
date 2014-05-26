package com.padule.cospradar.fragment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.padule.cospradar.AppUrls;
import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.activity.MainActivity;
import com.padule.cospradar.base.BaseFragment;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.mock.MockFactory;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

public class CharactorEditFragment extends BaseFragment {

    private static final String TAG = CharactorEditFragment.class.getName();

    @InjectView(R.id.img_charactor) ImageView mImgCharactor;
    @InjectView(R.id.edit_name) EditText mEditName;
    @InjectView(R.id.edit_title) EditText mEditTitle;

    private Charactor charactor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_charactor_edit, container, false);
        ButterKnife.inject(this, view);
        aq = new AQuery(getActivity());

        initCharactor();
        initView();
        setHasOptionsMenu(true);

        return view;
    }

    private void initCharactor() {
        charactor = AppUtils.getCharactor();
        if (charactor == null) {
            charactor = new Charactor();
        }
    }

    private void initView() {
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
        KeyboardUtils.hide(getActivity());
        ImageUtils.showChooserDialog(this);
    }

    @OnClick(R.id.root)
    void onClickRoot() {
        KeyboardUtils.hide(getActivity());
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
                Intent i = ImageUtils.createAviaryIntent(getActivity(), uri);
                startActivityForResult(i, Constants.REQ_ACTIVITY_AVIARY_CAMERA);
            }
            break;
        case Constants.REQ_ACTIVITY_GALLERY:
            if (resultCode == Activity.RESULT_OK) {
                Intent i = ImageUtils.createAviaryIntent(getActivity(), uri);
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
        String path = ImageUtils.getPath(getActivity(), uri);
        this.charactor.setImage(path);
        initCharactorImage(ImageUtils.convertToValidUrl(path));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.only_ok, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.item_ok == item.getItemId()) {
            if (validate()) {
                KeyboardUtils.hide(getActivity());
                saveCharactor();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCharactor() {
        String url = AppUrls.getCharactorsCreate();
        Dialog dialog = AppUtils.makeSendingDialog(getActivity());
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
            ((MainActivity)getActivity()).initDrawer();
        } else {
            if (AppUtils.isMockMode()) {
                // FIXME Remove me. Just implement for test.
                this.charactor = MockFactory.createCharactor(mEditName.getText().toString(), 
                        mEditTitle.getText().toString(), charactor.getImageUrl());
                AppUtils.setCharactor(charactor);
                showToast(R.string.charactor_edit_succeeded);
                ((MainActivity)getActivity()).initDrawer();
            } else {
                Log.e(TAG, "create_error_message: " + status.getMessage());
                showToast(R.string.charactor_edit_failed);
            }
        }
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
        if (charactor.getName().equals(mEditName.getText().toString())
                && charactor.getTitle().equals(mEditTitle.getText().toString())
                && (AppUtils.getCharactor() != null && AppUtils.getCharactor().getImageUrl().equals(charactor.getImageUrl()))) {
            showToast(R.string.not_changed_input);
            return false;
        }
        return true;
    }

}
