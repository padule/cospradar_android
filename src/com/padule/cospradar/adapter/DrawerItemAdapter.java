package com.padule.cospradar.adapter;

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
import com.padule.cospradar.data.DrawerItem;

public class DrawerItemAdapter extends ArrayAdapter<DrawerItem> {

    private Context context;

    public DrawerItemAdapter(Context context, List<DrawerItem> drawerItems) {
        super(context, R.layout.item_drawer_item, drawerItems);
        this.context = context;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_drawer_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        final DrawerItem drawerItem = (DrawerItem)getItem(pos);

        if (drawerItem != null) {
            holder.mTxtDrawerTitle.setText(drawerItem.getTitle());
            holder.mImgDrawerIcon.setImageResource(drawerItem.getIconResId());
        }

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.img_drawer_icon) ImageView mImgDrawerIcon;
        @InjectView(R.id.txt_drawer_title) TextView mTxtDrawerTitle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


}
