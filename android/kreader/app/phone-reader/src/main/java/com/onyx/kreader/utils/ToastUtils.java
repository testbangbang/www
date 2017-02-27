package com.onyx.kreader.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by suicheng on 2017/2/16.
 */
public class ToastUtils {

    public static void showLongToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showShortToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, int resId) {
        showLongToast(context, context.getString(resId));
    }

    public static void showShortToast(Context context, int resId) {
        showShortToast(context, context.getString(resId));
    }

    public static void showToast(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }
}
