package com.onyx.jdread.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

/**
 * Created by jackdeng on 2017/12/7.
 */

public class ToastUtil {

    private static String oldMsg;
    private static Toast toast = null;
    private static int left = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_left_and_right);
    private static int top = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_top_and_bottom);
    private static int right = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_left_and_right);
    private static int bottom = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toast_view_padding_top_and_bottom);
    private static float radius = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_radius);
    private static float dx = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_dx);
    private static float dy = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_dy);
    private static float textSize = JDReadApplication.getInstance().getResources().getDimension(R.dimen.level_three_heading_font);

    public static void showToast(Context appContext, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(appContext.getApplicationContext(), message, Toast.LENGTH_SHORT);
            View view = toast.getView();
            setBackground(view, getDrawable(appContext, R.drawable.rectangle_stroke));
            TextView textView = (TextView) view.findViewById(android.R.id.message);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(left, top, right, bottom);
            textView.setShadowLayer(radius, dx, dy, Color.TRANSPARENT);
            toast.show();
        } else {
            if (message.equals(oldMsg)) {
                if (!toast.getView().isShown()) {
                    toast.show();
                }
            } else {
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
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    private static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
