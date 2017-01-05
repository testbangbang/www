package com.onyx.android.sdk.ui.compat;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/12/20.
 */
public class AppCompatImageViewCollection {
    public static boolean isPl107Device = false;

    private List<ImageView> imageViewList = new ArrayList<>();
    private boolean alwaysLayoutImageView = false;

    private boolean isPL107Device() {
        return isPl107Device;
    }

    public AppCompatImageViewCollection(Context context, AttributeSet parentAttrs) {
        initCustomAttributeSet(context, parentAttrs);
    }

    private void initCustomAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppCompatLayout);
        alwaysLayoutImageView = a.getBoolean(R.styleable.AppCompatLayout_alwaysLayoutImageView, false);
        a.recycle();
    }

    public void collect(Context context, ViewGroup viewGroup) {
        if (!isPL107Device()) {
            return;
        }
        if (imageViewList.isEmpty()) {
            imageViewList.addAll(AppCompatUtils.getChildImageViews(viewGroup));
        }
    }

    public void adjustAllImageView(boolean layoutChanged) {
        if (!isPL107Device()) {
            return;
        }
        if (alwaysLayoutImageView || !layoutChanged) {
            AppCompatUtils.processImageViewsLayoutEvenPosition(imageViewList);
        }
    }

    public void setAlwaysLayoutImageView(boolean alwaysLayoutImageView) {
        this.alwaysLayoutImageView = alwaysLayoutImageView;
    }
}
