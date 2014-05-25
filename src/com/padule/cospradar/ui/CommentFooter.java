package com.padule.cospradar.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.padule.cospradar.R;
import com.padule.cospradar.util.KeyboardUtils;
import com.padule.cospradar.util.TextUtils;

public class CommentFooter extends LinearLayout implements TextWatcher {

    @InjectView(R.id.btn_comment) Button mBtnComment;
    @InjectView(R.id.edit_comment) EditText mEditComment;

    private FooterCommentListener listener;
    private Context context;

    public interface FooterCommentListener {
        public void onClickBtnComment(String text);
    }

    public CommentFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ui_comment_footer, this, true);
        ButterKnife.inject(this);

        mEditComment.addTextChangedListener(this);
    }

    public void setListener(FooterCommentListener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.btn_comment)
    public void onClickSendComment() {
        if (listener != null) {
            listener.onClickBtnComment(mEditComment.getText().toString());
        }
        clearText();
    }

    private void clearText() {
        mEditComment.setText("");
        KeyboardUtils.hide(context, mEditComment);
    }

    @Override
    public void afterTextChanged(Editable s) {
        mBtnComment.setEnabled(!TextUtils.isEmpty(mEditComment));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //
    }

}
