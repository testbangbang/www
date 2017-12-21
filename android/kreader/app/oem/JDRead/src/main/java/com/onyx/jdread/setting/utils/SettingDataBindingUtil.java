package com.onyx.jdread.setting.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * Created by li on 2017/12/20.
 */

public class SettingDataBindingUtil {

    @BindingAdapter({"cover"})
    public static void setSettingImage(ImageView imageView, int res) {
        imageView.setImageResource(res);
    }
}
