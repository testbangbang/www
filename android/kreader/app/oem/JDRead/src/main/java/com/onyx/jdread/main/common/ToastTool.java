package com.onyx.jdread.main.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.onyx.jdread.R;

/**
 * Created by lmb on 2018/3/18.
 */

public class ToastTool {

    private Toast mToast;
    private static float textSize = ResManager.getDimension(R.dimen.level_one_heading_font);
    private static int left = ResManager.getDimensionPixelSize(R.dimen.toast_view_padding_left_and_right);
    private static int top = ResManager.getDimensionPixelSize(R.dimen.toast_view_padding_top_and_bottom);
    private static int right = ResManager.getDimensionPixelSize(R.dimen.toast_view_padding_left_and_right);
    private static int bottom = ResManager.getDimensionPixelSize(R.dimen.toast_view_padding_top_and_bottom);
    private static float radius = ResManager.getInteger(R.integer.toast_view_shadow_radius);
    private static float dx = ResManager.getInteger(R.integer.toast_view_shadow_dx);
    private static float dy = ResManager.getInteger(R.integer.toast_view_shadow_dy);

    private ToastTool(Context context, CharSequence text, int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        View v = LayoutInflater.from(context).inflate(R.layout.toast_info, null);
        setBackground(v, getDrawable(context, R.drawable.rectangle_stroke));
        TextView textView = (TextView) v.findViewById(R.id.tv_info);
        textView.setText(text);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        textView.setHeight(ResManager.getDimensionPixelSize(R.dimen.toast_height));
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setMaxLines(20);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setPadding(left, 0, right, 0);
        textView.setShadowLayer(radius, dx, dy, Color.TRANSPARENT);
        mToast.setGravity(Gravity.BOTTOM, 0, ResManager.getDimensionPixelSize(R.dimen.library_manage_margin_right));
        mToast.setView(v);
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

    public static ToastTool showToast(Context context, CharSequence text, int duration) {
        return new ToastTool(context, text, duration);
    }

    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }
}
