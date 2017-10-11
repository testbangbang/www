package com.onyx.einfo.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.onyx.einfo.R;

/**
 * Created by suicheng on 2016/11/2.
 */

public class CustomWebView extends WebView {

    int maxHeight = -1;
    int maxWidth = -1;

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarkdownView_style);
        maxWidth = a.getDimensionPixelSize(R.styleable.MarkdownView_style_boundedWidth, 0);
        maxHeight = a.getDimensionPixelSize(R.styleable.MarkdownView_style_boundedHeight, 0);
        a.recycle();
    }

    public void setMaxHeight(int height) {
        maxHeight = height;
        requestLayout();
    }

    public void setMaxWidth(int width) {
        maxWidth = width;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Adjust width as necessary
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if (maxWidth > 0 && maxWidth < measuredWidth) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(maxWidth, measureMode);
        }
        // Adjust height as necessary
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (maxHeight > 0 && maxHeight < measuredHeight) {
            int measureMode = MeasureSpec.getMode(heightMeasureSpec);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, measureMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
