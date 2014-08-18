package com.padule.cospradar.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.padule.cospradar.R;

public class ResizableImageView extends ImageView {
    private float ratio;
    private boolean autoScale;

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ResizableImageView);
        try {
            ratio = a.getFloat(R.styleable.ResizableImageView_image_ratio, 2.0f);
            autoScale = a.getBoolean(R.styleable.ResizableImageView_auto_scale, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (autoScale) {
            Drawable drawable = getDrawable();
            if (drawable != null) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int)Math.ceil((float)width 
                        * (float)drawable.getIntrinsicHeight() / (float)drawable.getIntrinsicWidth());
                setMeasuredDimension(width, height);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

        } else {
            int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            double propotionalHeight;
            propotionalHeight = parentWidth / ratio;

            if (propotionalHeight < getSuggestedMinimumHeight()) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else {
                setMeasuredDimension(parentWidth, (int) propotionalHeight);
            }
        }
    }

    public void setImageRatio(float ratio) {
        this.ratio = ratio;
        requestLayout();
    }
}
