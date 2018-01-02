package com.onyx.jdread.personal.utils;

import android.databinding.BindingAdapter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.onyx.jdread.R;
import com.onyx.jdread.shop.common.ManageImageCache;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class PersonalDatabingUtil {

    @BindingAdapter({"backgroundImage"})
    public static void setImageResource(ImageView imageView,boolean showPassword) {
        if (imageView != null) {
            imageView.setImageResource(showPassword ? R.mipmap.ic_password_visible : R.mipmap.ic_password_invisible);
        }
    }

    @BindingAdapter({"isShowPassword"})
    public static void setShowPassword(EditText editText, boolean showPassword) {
        if (editText != null) {
            if (showPassword) {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    @BindingAdapter({"personalImage"})
    public static void setImageUrl(ImageView view, String url) {
        if (view != null) {
            ManageImageCache.loadUrl(url, view, R.drawable.book_default_cover);
        }
    }
}
