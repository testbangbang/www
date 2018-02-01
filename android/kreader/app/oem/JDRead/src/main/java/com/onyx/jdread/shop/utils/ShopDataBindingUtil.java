package com.onyx.jdread.shop.utils;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.adapter.AllCategoryTopAdapter;
import com.onyx.jdread.shop.adapter.BannerSubjectAdapter;
import com.onyx.jdread.shop.adapter.BookCommentsAdapter;
import com.onyx.jdread.shop.adapter.BookRankAdapter;
import com.onyx.jdread.shop.adapter.CategorySubjectAdapter;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.adapter.ShopMainConfigAdapter;
import com.onyx.jdread.shop.adapter.SubjectAdapter;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.adapter.SubjectWithVipAdapter;
import com.onyx.jdread.shop.adapter.TitleSubjectAdapter;
import com.onyx.jdread.shop.adapter.VipReadAdapter;
import com.onyx.jdread.shop.common.ManageImageCache;
import com.onyx.jdread.shop.view.AutoPagedWebView;
import com.onyx.jdread.shop.view.HtmlTextView;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/12.
 */

public class ShopDataBindingUtil {

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

    @BindingAdapter({"vipItems"})
    public static void setVIpItems(PageRecyclerView recyclerView, List items) {
        SubjectWithVipAdapter adapter = (SubjectWithVipAdapter) recyclerView.getAdapter();
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

    @BindingAdapter({"categoryItems"})
    public static void setAllCategoryItems(PageRecyclerView recyclerView, List items) {
        CategorySubjectAdapter adapter = (CategorySubjectAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"categoryTopItems"})
    public static void setAllCategoryTopItems(PageRecyclerView recyclerView, List items) {
        AllCategoryTopAdapter adapter = (AllCategoryTopAdapter) recyclerView.getAdapter();
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
        String title = ResManager.getString(R.string.book_detail_text_view_content_introduce) + ":";
        if (!StringUtils.isNullOrEmpty(content)){
            htmlTextView.setHtml(title + content);
        } else {
            String emptyCOntent = ResManager.getString(R.string.book_detail_empty_introduce);
            htmlTextView.setHtml(title + emptyCOntent);
        }
    }

    @BindingAdapter({"htmlContentDialog"})
    public static void setHtmlContentDialog(HtmlTextView htmlTextView, String content) {
        if (!StringUtils.isNullOrEmpty(content)){
            htmlTextView.setHtml(content);
        } else {
            String emptyCOntent = ResManager.getString(R.string.book_detail_empty_introduce);
            htmlTextView.setHtml(emptyCOntent);
        }
    }

    @BindingAdapter({"bookInfoWebView"})
    public static void setBookInfoDialog(AutoPagedWebView webView, String content) {
        if (StringUtils.isNullOrEmpty(content)){
            content = ResManager.getString(R.string.book_detail_empty_introduce);
        }
        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    @BindingAdapter({"subjectList"})
    public static void setSubjectList(PageRecyclerView recyclerView, List items) {
        SubjectListAdapter adapter = (SubjectListAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"subjectModels"})
    public static void setSubjectModels(PageRecyclerView recyclerView, List items) {
        BookRankAdapter adapter = (BookRankAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"vipSubjectItems"})
    public static void setVipSubjectItems(PageRecyclerView recyclerView, List items) {
        VipReadAdapter adapter = (VipReadAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"titleSubjectItems"})
    public static void setTitleItems(PageRecyclerView recyclerView, List items) {
        TitleSubjectAdapter adapter = (TitleSubjectAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"shopConfigSubjects"})
    public static void setConfigSubjects(RecyclerView recyclerView, List items) {
        ShopMainConfigAdapter adapter = (ShopMainConfigAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items);
        }
    }
}
