package com.onyx.jdread.reader.utils;

import android.databinding.BindingAdapter;

import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.jdread.main.common.PageAdapter;

import java.util.List;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class ReaderDataBindingUtil {
    @BindingAdapter("bookmarks")
    public static void setBookmarks(PageRecyclerView recyclerView, List items) {
        PageAdapter adapter = (PageAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }

    @BindingAdapter("notes")
    public static void setNotes(PageRecyclerView recyclerView, List items) {
        PageAdapter adapter = (PageAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setRawData(items, recyclerView.getContext());
        }
    }
}
