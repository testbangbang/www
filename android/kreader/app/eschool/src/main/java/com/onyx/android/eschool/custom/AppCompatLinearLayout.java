package com.onyx.android.eschool.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.onyx.android.eschool.utils.AppCompatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/12/20.
 */

public class AppCompatLinearLayout extends LinearLayout {

    private List<ImageView> imageViewList = new ArrayList<>();

    public AppCompatLinearLayout(Context context) {
        this(context, null);
    }

    public AppCompatLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppCompatLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (imageViewList.isEmpty()) {
            imageViewList.addAll(AppCompatUtils.getChildImageViews(this));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!changed) {
            AppCompatUtils.processImageViewsLayoutEvenPosition(imageViewList);
        }
    }
}
