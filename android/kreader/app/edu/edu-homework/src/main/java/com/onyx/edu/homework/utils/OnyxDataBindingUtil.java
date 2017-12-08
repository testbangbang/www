package com.onyx.edu.homework.utils;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.flexbox.FlexboxLayout;

/**
 * Created by solskjaer49 on 2016/11/26 10:31.
 */

public class OnyxDataBindingUtil {

    @BindingAdapter({"layoutColumns"})
    public static void setLayoutColumns(View view, int layoutColumns) {
        if (layoutColumns == 0){
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent instanceof FlexboxLayout) {
            FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams)view.getLayoutParams();
            float flexBasisPercent = (float) 1/layoutColumns;
            lp.setFlexBasisPercent(flexBasisPercent);
            view.setLayoutParams(lp);
        }
    }

    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        imageView.setImageResource(resource);
    }

    @BindingAdapter("android:layout_width")
    public static void setLayoutWidth(View view, int width) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width;
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter({"imageState"})
    public static void setImageViewState(ImageView imageView, int[] value) {
        imageView.setImageState(value, true);
    }

    @BindingAdapter({"thumbnail"})
    public static void setImageViewBitmap(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @BindingAdapter("android:layout_centerInParent")
    public static void setCenterInParent(View view, boolean centerInParent) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (centerInParent) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            } else {
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            }
        }
        view.setLayoutParams(layoutParams);
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setLayoutMarginTop(View view, float topMargin) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (topMargin > 0) {
            layoutParams.setMargins(layoutParams.leftMargin, (int) topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
        }
        view.setLayoutParams(layoutParams);
    }
}
