package com.padule.cospradar.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.padule.cospradar.R;

public class HoldableViewPager extends ViewPager {
    private boolean isSwipeHold = false;

    public HoldableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.HoldableViewPager);
        try {
            isSwipeHold = a.getBoolean(R.styleable.HoldableViewPager_swipe_hold, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSwipeHold) return false;
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)  {
        if (isSwipeHold) return false;
        return super.onInterceptTouchEvent(event);
    }
}
