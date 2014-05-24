package com.padule.cospradar.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.Constants;
import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseFragment;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.util.AppUtils;
import com.padule.cospradar.util.ImageUtils;

public class CharactorEditFragment extends BaseFragment {

    @InjectView(R.id.img_charactor) ImageView mImgCharactor;
    @InjectView(R.id.edit_name) EditText mEditName;
    @InjectView(R.id.edit_title) EditText mEditTitle;

    private Charactor charactor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_charactor_edit, container, false);
        ButterKnife.inject(this, view);

        initCharactor();
        initView();

        return view;
    }

    private void initCharactor() {
        charactor = AppUtils.getCharactor();
        if (charactor == null) {
            charactor = new Charactor();
        }
    }

    private void initView() {
        initCharactorImage(charactor.getImage());
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
        ImageUtils.showChooserDialog(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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
            if (resultCode == Activity.RESULT_OK) {
                String path = ImageUtils.getPath(getActivity(), uri);
                initCharactorImage(path);
            }
            break;
        case Constants.REQ_ACTIVITY_AVIARY_GALLERY:
            if (resultCode == Activity.RESULT_OK) {
                String path = ImageUtils.getPath(getActivity(), uri);
                initCharactorImage(path);
            }
            break;
        }
    }

}
