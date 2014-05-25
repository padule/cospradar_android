package com.padule.cospradar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.padule.cospradar.R;
import com.padule.cospradar.data.Charactor;
import com.padule.cospradar.data.CharactorComment;
import com.padule.cospradar.util.ImageUtils;
import com.padule.cospradar.util.TimeUtils;

public class CommentListAdapter extends ArrayAdapter<CharactorComment> {

    private Context context;

    public CommentListAdapter(Context context) {
        this(context, new ArrayList<CharactorComment>());
    }

    public CommentListAdapter(Context context, List<CharactorComment> comments) {
        super(context, 0, comments);
        this.context = context;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final ViewHolder holder;

        final CharactorComment comment = (CharactorComment)getItem(pos);

        if (view == null) {
            int layout = comment.isCurrentCharactor() ? R.layout.item_comment_right : R.layout.item_comment_left;
            view = LayoutInflater.from(context).inflate(layout, parent, false);

            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        bindData(comment, holder);

        return view;
    }

    private void bindData(CharactorComment comment, ViewHolder holder) {
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
        } else {
            holder.mRoot.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        @InjectView(R.id.container_root) View mRoot;
        @InjectView(R.id.img_charactor) ImageView mImgCharactor;
        @InjectView(R.id.txt_comment) TextView mTxtComment;
        @InjectView(R.id.txt_charactor_name) TextView mTxtCharactorName;
        @InjectView(R.id.txt_date) TextView mTxtDate;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
