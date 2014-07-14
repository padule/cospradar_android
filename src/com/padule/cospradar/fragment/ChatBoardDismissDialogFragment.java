package com.padule.cospradar.fragment;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseActivity;
import com.padule.cospradar.base.BaseConfirmDialogFragment;
import com.padule.cospradar.event.CommentCloseEvent;

import de.greenrobot.event.EventBus;

public class ChatBoardDismissDialogFragment extends BaseConfirmDialogFragment {

    public static void show(BaseActivity activity) {
        ChatBoardDismissDialogFragment fragment = new ChatBoardDismissDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), 
                ChatBoardDismissDialogFragment.class.getName());
    }

    @Override
    protected int getTitleResId() {
        return R.string.cancel;
    }

    @Override
    protected int getMsgResId() {
        return R.string.comment_close_confirm;
    }

    @Override
    protected void onConfirm() {
        EventBus.getDefault().post(new CommentCloseEvent());
    }

}
