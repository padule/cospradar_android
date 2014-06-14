package com.padule.cospradar.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.padule.cospradar.R;

public class ResizableImageView extends ImageView {
    private float ratio;

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ResizableImageView);
        try {
            ratio = a.getFloat(R.styleable.ResizableImageView_image_ratio, 2.0f);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        double propotionalHeight;

        propotionalHeight = parentWidth / ratio;

        if (propotionalHeight < getSuggestedMinimumHeight()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(parentWidth, (int) propotionalHeight);
        }
    }

    public void setImageRatio(float ratio) {
        this.ratio = ratio;
        requestLayout();
    }
}
