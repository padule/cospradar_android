package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.base.BaseArrayAdapter;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.event.CommentCharactorIconClickedEvent;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.TimeUtils;

import de.greenrobot.event.EventBus;

public class CommentsAdapter extends BaseArrayAdapter<CharactorComment> {

    private Charactor chatBoardCharactor;
    private List<Charactor> userCharactors;

    public CommentsAdapter(Context context, Charactor charactor, List<Charactor> charactors) {
        this(context, new ArrayList<CharactorComment>(), charactor, charactors);
    }

    public CommentsAdapter(Context context, List<CharactorComment> comments, 
            Charactor charactor, List<Charactor> charactors) {
        super(context, 0, comments);
        this.chatBoardCharactor = charactor;
        this.userCharactors = charactors;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final ViewHolder holder;

        final CharactorComment comment = (CharactorComment)getItem(pos);

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            holder = new ViewHolder(view, comment.isUserCharactor(userCharactors));
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
            holder.setIsCurrentCharactor(comment.isUserCharactor(userCharactors));
        }

        bindData(comment, holder);
        initListener(comment, holder);

        return view;
    }

    private void bindData(final CharactorComment comment, ViewHolder holder) {
        if (comment != null) {
            Charactor charactor = comment.getCommentCharactor();
            if (charactor.getImageUrl() != null) {
                ImageUtils.displayRoundedImage(charactor.getImageUrl(), holder.mImgCharactor);
            } else {
                holder.mImgCharactor.setImageResource(R.drawable.ic_no_user_rounded);
            }
            holder.mTxtComment.setText(comment.getText());
            holder.mTxtCharactorName.setText(charactor.getNameAndTitle());
            holder.mTxtDate.setText(TimeUtils.getDisplayDate(comment.getCreatedAt(), context));

            switchCommentColor(charactor, holder);
        } else {
            holder.mRoot.setVisibility(View.GONE);
        }
    }

    private void initListener(final CharactorComment comment, ViewHolder holder) {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CommentCharactorIconClickedEvent(comment.getCommentCharactor()));
            }
        };
        holder.mImgCharactorLeft.setOnClickListener(onClickListener);
        holder.mImgCharactorRight.setOnClickListener(onClickListener);
    }

    private void switchCommentColor(Charactor charactor, ViewHolder holder) {
        if (chatBoardCharactor.getId() == charactor.getId()) {
            holder.mTxtCommentLeft.setBackgroundResource(R.drawable.bg_theme_rounded);
            holder.mImgTriangleLeft.setImageResource(R.drawable.ic_triangle_theme_left);
            holder.mTxtCommentLeft.setTextColor(context.getResources().getColor(R.color.text_white));
        } else {
            holder.mTxtCommentLeft.setBackgroundResource(R.drawable.bg_white_rounded);
            holder.mImgTriangleLeft.setImageResource(R.drawable.ic_triangle_left);
            holder.mTxtCommentLeft.setTextColor(context.getResources().getColor(android.R.color.black));
        }
    }

    static class ViewHolder {
        @InjectView(R.id.container_root) View mRoot;
        @InjectView(R.id.container_comment_left) View mContainerCommentLeft;
        @InjectView(R.id.img_charactor_left) ImageView mImgCharactorLeft;
        @InjectView(R.id.txt_comment_left) TextView mTxtCommentLeft;
        @InjectView(R.id.txt_charactor_name_left) TextView mTxtCharactorNameLeft;
        @InjectView(R.id.img_triangle_left) ImageView mImgTriangleLeft;
        @InjectView(R.id.txt_date_left) TextView mTxtDateLeft;

        @InjectView(R.id.container_comment_right) View mContainerCommentRight;
        @InjectView(R.id.img_charactor_right) ImageView mImgCharactorRight;
        @InjectView(R.id.txt_comment_right) TextView mTxtCommentRight;
        @InjectView(R.id.txt_charactor_name_right) TextView mTxtCharactorNameRight;
        @InjectView(R.id.txt_date_right) TextView mTxtDateRight;

        View mContainerComment;
        ImageView mImgCharactor;
        TextView mTxtComment;
        TextView mTxtCharactorName;
        TextView mTxtDate;

        public ViewHolder(View view, boolean isCurrentCharactor) {
            ButterKnife.inject(this, view);

            setIsCurrentCharactor(isCurrentCharactor);
        }

        public void setIsCurrentCharactor(boolean isCurrentCharactor) {
            switchVisiblity(isCurrentCharactor);
            setViews(isCurrentCharactor);
        }

        private void switchVisiblity(boolean isCurrentCharactor) {
            if (isCurrentCharactor) {
                mContainerCommentLeft.setVisibility(View.GONE);
                mContainerCommentRight.setVisibility(View.VISIBLE);
            } else {
                mContainerCommentLeft.setVisibility(View.VISIBLE);
                mContainerCommentRight.setVisibility(View.GONE);
            }
        }

        private void setViews(boolean isCurrentCharactor) {
            if (isCurrentCharactor) {
                mContainerComment = mContainerCommentRight;
                mImgCharactor = mImgCharactorRight;
                mTxtComment = mTxtCommentRight;
                mTxtCharactorName = mTxtCharactorNameRight;
                mTxtDate = mTxtDateRight;
            } else {
                mContainerComment = mContainerCommentLeft;
                mImgCharactor = mImgCharactorLeft;
                mTxtComment = mTxtCommentLeft;
                mTxtCharactorName = mTxtCharactorNameLeft;
                mTxtDate = mTxtDateLeft;
            }
        }

    }

}
