package com.padule.cospradar.base;

import java.util.Collection;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {

    protected Context context;

    public BaseArrayAdapter(Context context, int resId, List<T> objects) {
        super(context, resId, objects);
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    @SuppressLint("NewApi")
    @Override
    public void addAll(@SuppressWarnings("rawtypes") Collection collection) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            for (Object object : collection) {
                super.add((T)object);
            }
        }
    }

    @Override
    public abstract View getView(int pos, View view, ViewGroup parent);

}
