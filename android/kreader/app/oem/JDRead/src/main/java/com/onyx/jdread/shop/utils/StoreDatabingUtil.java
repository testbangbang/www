package com.onyx.jdread.shop.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.adapter.SubjectAdapter;
import com.onyx.jdread.shop.common.ManageImageCache;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class StoreDatabingUtil {

    @BindingAdapter({"cover"})
    public static void setImageResource(ImageView imageView, String imageUrl) {
        if (imageUrl != null) {
            ManageImageCache.loadUrl(imageUrl, imageView, R.drawable.book_default_cover);
        }
    }

    @BindingAdapter({"subjectItems"})
    public static void setItems(PageRecyclerView recyclerView, List items) {
        SubjectAdapter adapter = (SubjectAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

}
