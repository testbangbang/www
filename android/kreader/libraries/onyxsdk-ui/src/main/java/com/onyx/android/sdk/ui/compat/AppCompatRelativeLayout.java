package com.onyx.android.sdk.ui.compat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by suicheng on 2016/12/20.
 */
public class AppCompatRelativeLayout extends RelativeLayout {

    private AppCompatImageViewCollection imageViewCollection;

    public AppCompatRelativeLayout(Context context) {
        this(context, null);
    }

    public AppCompatRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageViewCollection = new AppCompatImageViewCollection(context, attrs);
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        imageViewCollection.collect(getContext(), this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        imageViewCollection.adjustAllImageView(changed);
    }
}
