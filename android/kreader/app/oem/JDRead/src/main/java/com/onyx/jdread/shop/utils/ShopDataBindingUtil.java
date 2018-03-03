package com.onyx.jdread.shop.utils;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.ui.view.AutoPagedWebView;
import com.onyx.jdread.shop.adapter.AllCategoryTopAdapter;
import com.onyx.jdread.shop.adapter.BannerSubjectAdapter;
import com.onyx.jdread.shop.adapter.BatchDownloadChaptersAdapter;
import com.onyx.jdread.shop.adapter.BookCommentsAdapter;
import com.onyx.jdread.shop.adapter.BookRankAdapter;
import com.onyx.jdread.shop.adapter.BuyReadVipAdapter;
import com.onyx.jdread.shop.adapter.CategoryBookListAdapter;
import com.onyx.jdread.shop.adapter.RecommendAdapter;
import com.onyx.jdread.shop.adapter.ShopMainConfigAdapter;
import com.onyx.jdread.shop.adapter.SubjectAdapter;
import com.onyx.jdread.shop.adapter.SubjectListAdapter;
import com.onyx.jdread.shop.adapter.SubjectWithVipAdapter;
import com.onyx.jdread.shop.adapter.TitleSubjectAdapter;
import com.onyx.jdread.shop.adapter.VipReadAdapter;
import com.onyx.jdread.shop.common.ManageImageCache;
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
            if (items.size() > Constants.SHOP_MAIN_INDEX_FOUR) {
                items = items.subList(0, Constants.SHOP_MAIN_INDEX_FOUR);
            }
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
            if (items.size() > Constants.SHOP_MAIN_INDEX_TWO) {
                items = items.subList(0, Constants.SHOP_MAIN_INDEX_TWO);
            }
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"categoryItems"})
    public static void setAllCategoryItems(PageRecyclerView recyclerView, List items) {
        CategoryBookListAdapter adapter = (CategoryBookListAdapter) recyclerView.getAdapter();
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
        htmlTextView.setMaxLineCount(ResManager.getInteger(R.integer.book_detail_info_lines));
        if (!StringUtils.isNullOrEmpty(content)) {
            htmlTextView.setHtml(title + content);
        } else {
            String emptyCOntent = ResManager.getString(R.string.book_detail_empty_introduce);
            htmlTextView.setHtml(title + emptyCOntent);
        }
    }

    @BindingAdapter({"htmlContentNoTitle"})
    public static void setHtmlContentNoTitle(HtmlTextView htmlTextView, String content) {
        htmlTextView.setMaxLineCount(ResManager.getInteger(R.integer.item_subject_list_info_lines));
        if (!StringUtils.isNullOrEmpty(content)) {
            htmlTextView.setHtml(content);
        } else {
            String emptyCOntent = ResManager.getString(R.string.book_detail_empty_introduce);
            htmlTextView.setHtml(emptyCOntent);
        }
    }

    @BindingAdapter({"bookInfoWebView"})
    public static void setBookInfoDialog(AutoPagedWebView webView, String content) {
        if (!StringUtils.isNullOrEmpty(content)) {
            webView.loadData(content, "text/html; charset=UTF-8", null);
        }
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
            adapter.setDatas(items);
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
            adapter.setDatas(items);
        }
    }

    @BindingAdapter({"goodList"})
    public static void setVipGoodList(RecyclerView recyclerView, List items) {
        BuyReadVipAdapter adapter = (BuyReadVipAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter({"chaptersItems"})
    public static void setChaptersItems(RecyclerView recyclerView, List items) {
        BatchDownloadChaptersAdapter adapter = (BatchDownloadChaptersAdapter) recyclerView.getAdapter();
        if (adapter != null && items != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }
}
