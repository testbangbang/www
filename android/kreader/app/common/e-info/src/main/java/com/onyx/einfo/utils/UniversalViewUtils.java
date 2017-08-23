package com.onyx.einfo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.einfo.R;

/**
 * Created by suicheng on 2017/8/18.
 */
public class UniversalViewUtils {

    public static void initNormalView(final Activity activity, int layoutResId, int textResId, int imageResId, final Intent intent) {
        ViewGroup viewGroup = (ViewGroup) activity.findViewById(layoutResId);
        TextView textView = (TextView) viewGroup.findViewById(R.id.textView_category_text);
        textView.setText(textResId);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.imageView_category_image);
        imageView.setImageResource(imageResId);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNormalViewClick(activity, intent);
            }
        });
    }

    private static void processNormalViewClick(Context context, Intent intent) {
        ActivityUtil.startActivitySafely(context, intent);
    }
}
