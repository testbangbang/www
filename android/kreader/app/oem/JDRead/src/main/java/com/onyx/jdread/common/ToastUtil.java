package com.onyx.jdread.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.android.sdk.ui.R;

/**
 * Created by jackdeng on 2017/12/7.
 */

public class ToastUtil {

    private static String oldMsg;
    protected static Toast toast = null;
    private static int left = 15, top = 10, right = 15, bottom = 10;
    private static float radius = 0, dx = 0, dy = 0;
    private static int color = 0;
    private static int textSize = 20;

    public static void showToast(Context appContext, String message) {
        if (toast == null) {
            toast = Toast.makeText(appContext.getApplicationContext(), message, Toast.LENGTH_SHORT);
            View view = toast.getView();
            setBackground(view, getDrawable(appContext, R.drawable.toast_white_background));
            TextView textView = (TextView) view.findViewById(android.R.id.message);
            textView.setTextColor(Color.BLACK);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
			textView.setGravity(Gravity.CENTER);
            textView.setPadding(left, top, right, bottom);
            textView.setShadowLayer(radius, dx, dy, color);
            toast.show();
        }
        else {
            if (message.equals(oldMsg)) {
                if (!toast.getView().isShown()) {
                    toast.show();
                }
            }
            else {
                oldMsg = message;
                toast.setText(message);
                toast.show();
            }
        }
    }


    public static void showToast(Context appContext, int resId) {
        showToast(appContext, appContext.getString(resId));
    }

    private static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }
        else {
            view.setBackgroundDrawable(drawable);
        }
    }

    private static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        }
        else {
            return context.getResources().getDrawable(id);
        }
    }
}
