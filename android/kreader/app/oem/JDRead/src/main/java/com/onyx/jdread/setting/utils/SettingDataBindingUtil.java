package com.onyx.jdread.setting.utils;

import android.databinding.BindingAdapter;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.PageAdapter;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.common.ManageImageCache;

import java.util.List;

/**
 * Created by li on 2017/12/20.
 */

public class SettingDataBindingUtil {

    @BindingAdapter({"settingCover"})
    public static void setSettingImage(ImageView imageView, int res) {
        imageView.setImageResource(res);
    }

    @BindingAdapter({"cover"})
    public static void setImageResource(ImageView imageView, String imageUrl) {
        if (imageUrl != null) {
            ManageImageCache.loadUrl(imageUrl, imageView, R.drawable.book_default_cover);
        }
    }

    @SuppressWarnings("unchecked")
    @BindingAdapter("items")
    public static void setItems(PageRecyclerView recyclerView, List items) {
        PageAdapter adapter = (PageAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"eyeBackgroundImage"})
    public static void setEyeImageResource(ImageView imageView,boolean showPassword) {
        if (imageView != null) {
            imageView.setImageResource(showPassword ? R.mipmap.ic_password_visible : R.mipmap.ic_password_invisible);
        }
    }

    @BindingAdapter({"isShowPwd"})
    public static void setShowPwd(EditText editText, boolean showPassword) {
        if (editText != null) {
            if (showPassword) {
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            String s = editText.getText().toString();
            if (StringUtils.isNotBlank(s)) {
                editText.setSelection(s.length());
            }
        }
    }

    @BindingAdapter({"sureEnable"})
    public static void setSureEnable(TextView textView, boolean sureEnable){
        if(textView!=null){
            textView.setEnabled(sureEnable);
            textView.setTextColor(sureEnable? ResManager.getColor(android.R.color.black):ResManager.getColor(R.color.gray));
        }
    }
}
