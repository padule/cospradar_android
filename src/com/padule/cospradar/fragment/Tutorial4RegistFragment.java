package com.padule.cospradar.fragment;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import android.app.Dialog;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.MainApplication;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseTutorialFragment;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.event.PhotoChosenEvent;
import com.padule.cospradar.event.TutorialPageMoveEvent;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

import de.greenrobot.event.EventBus;

public class Tutorial4RegistFragment extends BaseTutorialFragment {

    private static final String TAG = Tutorial4RegistFragment.class.getName();
    public static final int POS = 3;

    @InjectView(R.id.img_charactor) ImageView mImgCharactor;
    @InjectView(R.id.edit_name) EditText mEditName;
    @InjectView(R.id.edit_title) EditText mEditTitle;

    private Charactor charactor;

    @OnClick(R.id.btn_regist)
    void onClickBtnRegist() {
        if (validate()) {
            KeyboardUtils.hide(getActivity());
            saveCharactor();
        }
    }

    @OnClick(R.id.btn_after)
    void onClickBtnAfter() {
        EventBus.getDefault().post(new TutorialPageMoveEvent(Tutorial6FinishFragment.POS));
    }

    @Override
    protected int getContentLayoutResId() {
        return R.layout.fragment_tutorial4_regist;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        bindData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void bindData() {
        charactor = new Charactor();
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
        ImageUtils.showChooserDialog(getActivity());
    }

    @OnClick(R.id.root)
    void onClickRoot() {
        KeyboardUtils.hide(getActivity());
    }

    public void onEvent(PhotoChosenEvent event) {
        setCharactorImage(event.uri);
    }

    private void setCharactorImage(Uri uri) {
        String path = ImageUtils.getPath(getActivity(), uri);
        this.charactor.setImage(path);
        initCharactorImage(ImageUtils.convertToValidUrl(path));
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mEditName)) {
            mEditName.setError(getString(R.string.charactor_validate_name));
            return false;
        }
        if (TextUtils.isEmpty(mEditTitle)) {
            mEditTitle.setError(getString(R.string.charactor_validate_title));
            return false;
        }
        return true;
    }

    private void saveCharactor() {
        final Dialog dialog = AppUtils.makeSendingDialog(getActivity());
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
                AppUtils.showToast(R.string.charactor_edit_failed, getActivity());
            }

            @Override
            public void success(Charactor charactor, Response response) {
                dialog.dismiss();
                Tutorial4RegistFragment.this.charactor = charactor;
                AppUtils.setCharactor(charactor);
                AppUtils.showToast(R.string.charactor_create_succeeded, getActivity());
                EventBus.getDefault().post(new TutorialPageMoveEvent(4));
            }
        });
    }

}
