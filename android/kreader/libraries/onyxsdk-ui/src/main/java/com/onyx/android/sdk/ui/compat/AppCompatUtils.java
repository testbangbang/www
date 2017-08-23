package com.onyx.android.sdk.ui.compat;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/12/17.
 */
public class AppCompatUtils {
    private static boolean isColorSupport = false;

    public static List<ImageView> getChildImageViews(ViewGroup parent) {
        List<ImageView> imageViewList = new ArrayList<>();
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            if (view instanceof ImageView) {
                imageViewList.add((ImageView) view);
            } else if (view instanceof ViewGroup) {
                imageViewList.addAll(getChildImageViews((ViewGroup) view));
            }
        }
        return imageViewList;
    }

    public static void processImageViewsLayoutEvenPosition(List<ImageView> imageViewList) {
        if (imageViewList.isEmpty()) {
            return;
        }
        for (ImageView imageView : imageViewList) {
            if (imageView == null) {
                continue;
            }
            processViewLayoutEvenPosition(imageView);
        }
    }

    public static boolean isColorDevice(Context context) {
        return isColorSupport ||
                ("pl107".equalsIgnoreCase(Build.MODEL) &&
                        context.getResources().getConfiguration().smallestScreenWidthDp == 960); // Temporarily reserved
    }

    public static void setColorSupport(boolean support) {
        isColorSupport = support;
    }

    public static float calculateEvenDigital(float value) {
        if (value % 2 != 0) {
            value++;
        }
        return value;
    }

    public static void forceViewReLayout(View view) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
        AppCompatUtils.processViewLayoutEvenPosition(view);
    }

    public static void processViewLayoutEvenPosition(View view) {
        int[] locationOnScreen = new int[2];
        view.getLocationOnScreen(locationOnScreen);
        int top = view.getTop();
        int left = view.getLeft();

        if (locationOnScreen[0] % 2 != 0) {
            left++;
        }

        if (locationOnScreen[1] % 2 != 0) {
            top++;
        }
        view.layout(left, top, left + view.getWidth(), top + view.getHeight());
    }
}
