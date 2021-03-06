package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseArrayAdapter;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.data.UnreadGcmCounts;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.TimeUtils;

public class ChatBoardsAdapter extends BaseArrayAdapter<Charactor> {

    public ChatBoardsAdapter(Context context) {
        this(context, new ArrayList<Charactor>());
    }

    public ChatBoardsAdapter(Context context, List<Charactor> charactors) {
        super(context, R.layout.item_comment_charactor, charactors);
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_comment_charactor, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        Charactor charactor = getItem(pos);
        holder.bindData(charactor, context);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.root) View mRoot;
        @InjectView(R.id.img_icon) ImageView mImgIcon;
        @InjectView(R.id.txt_name) TextView mTxtName;
        @InjectView(R.id.txt_title) TextView mTxtTitle;
        @InjectView(R.id.txt_user_name) TextView mTxtUserName;
        @InjectView(R.id.txt_latest_comment_time) TextView mTxtLatestCommentTime;
        @InjectView(R.id.txt_latest_comment) TextView mTxtLatestComment;
        @InjectView(R.id.txt_charactor_enabled) TextView mTxtCharactorEnabled;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        void bindData(Charactor charactor, Context context) {
            if (charactor != null) {
                ImageUtils.displayRoundedImage(charactor.getImageUrl(), mImgIcon);
                mTxtName.setText(charactor.getName());
                mTxtTitle.setText(charactor.getTitle());
                bindUserName(charactor);
                bindLatestComment(charactor, context);
                bindCharactorEnabled(charactor);
                bindUnreadBackground(charactor);
            }
        }

        void bindUnreadBackground(Charactor charactor) {
            Map<Integer, Integer> unreads = UnreadGcmCounts.getInstance().getChatBoardMap();
            int resId = unreads.containsKey(charactor.getId()) 
                    ? R.drawable.bg_accent_alfa : R.drawable.bg_white;
            mRoot.setBackgroundResource(resId);
        }

        void bindCharactorEnabled(Charactor charactor) {
            int visibility = charactor.isEnabled() ? View.VISIBLE : View.INVISIBLE;
            mTxtCharactorEnabled.setVisibility(visibility);
        }

        void bindUserName(Charactor charactor) {
            if (charactor.getUser() != null) {
                mTxtUserName.setText(charactor.getUser().getScreenName());
            }
        }

        void bindLatestComment(Charactor charactor, Context context) {
            CharactorComment comment = charactor.getLatestComment();
            if (comment != null) {
                mTxtLatestComment.setText(comment.getText());
                mTxtLatestCommentTime.setText(TimeUtils.getDisplayDate(comment.getCreatedAt(), context));
            }
        }

    }

}
