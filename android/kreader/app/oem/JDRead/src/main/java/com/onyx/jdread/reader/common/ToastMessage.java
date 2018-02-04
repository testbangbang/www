package com.onyx.jdread.reader.common;

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

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;

/**
 * Created by huxiaomao on 17/11/13.
 */

public class ToastMessage {
    public static void showMessage(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static void showMessageCenter(Context context,String message){
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        View view = toast.getView();
        final float textSize = JDReadApplication.getInstance().getResources().getDimension(R.dimen.level_three_heading_font);
        final int left = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.reader_toast_view_padding_left_and_right);
        final int top = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.reader_toast_view_padding_top_and_bottom);
        final int right = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.reader_toast_view_padding_left_and_right);
        final int bottom = JDReadApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.reader_toast_view_padding_top_and_bottom);
        final float radius = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_radius);
        final float dx = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_dx);
        final float dy = JDReadApplication.getInstance().getResources().getInteger(R.integer.toast_view_shadow_dy);
        setBackground(view, getDrawable(context, R.drawable.rectangle_stroke));
        TextView textView = (TextView) view.findViewById(android.R.id.message);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(left, top, right, bottom);
        textView.setShadowLayer(radius, dx, dy, Color.TRANSPARENT);
        toast.show();
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
