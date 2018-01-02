package com.onyx.jdread.setting.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.PageAdapter;
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
}
