package com.onyx.jdread.shop.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.adapter.BannerSubjectAdapter;
import com.onyx.jdread.shop.adapter.BookCommentsAdapter;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.adapter.SubjectAdapter;
import com.onyx.jdread.shop.common.ManageImageCache;
import com.onyx.jdread.shop.view.HtmlTextView;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class ShopDatabingUtil {

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

    @BindingAdapter({"bannerSubjectItems"})
    public static void setBannerItems(PageRecyclerView recyclerView, List items) {
        BannerSubjectAdapter adapter = (BannerSubjectAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"categorySubjectItems"})
    public static void setCategoryItems(PageRecyclerView recyclerView, List items) {
        CategorySubjectAdapter adapter = (CategorySubjectAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"recommendItems"})
    public static void setRecommendItems(PageRecyclerView recyclerView, List items) {
        RecommendAdapter adapter = (RecommendAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"commentItems"})
    public static void setCommentItems(PageRecyclerView recyclerView, List items) {
        BookCommentsAdapter adapter = (BookCommentsAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"htmlContent"})
    public static void setHtmlContent(HtmlTextView htmlTextView, String content) {
        if (!StringUtils.isNullOrEmpty(content)){
            htmlTextView.setHtml(content);
        }
    }
}
