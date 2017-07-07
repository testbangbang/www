package com.onyx.android.dr.fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.view.ImageTextButton;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-6-29.
 */

public class MainViewFragment extends BaseFragment {
    @Bind(R.id.now_reading_book_cover)
    ImageView nowReadingBookCover;
    @Bind(R.id.now_reading_text_progress)
    TextView nowReadingTextProgress;
    @Bind(R.id.now_reading_progressBar)
    ProgressBar nowReadingProgressBar;
    @Bind(R.id.now_reading_book_name)
    TextView nowReadingBookName;
    @Bind(R.id.english_bookshelf)
    ImageTextButton englishBookshelf;
    @Bind(R.id.chinese_bookshelf)
    ImageTextButton chineseBookshelf;
    @Bind(R.id.small_language_bookshelf)
    ImageTextButton smallLanguageBookshelf;
    @Bind(R.id.book_name_search)
    ImageTextButton bookNameSearch;
    @Bind(R.id.author_search)
    ImageTextButton authorSearch;
    @Bind(R.id.image_next)
    ImageView imageNext;
    @Bind(R.id.page_count)
    TextView pageCount;
    @Bind(R.id.recent_reading_recycler)
    PageRecyclerView recentReadingRecycler;
    private BookListAdapter libraryAdapter;
    private String idString;
    private LibraryDataHolder dataHolder;

    @Override
    protected void initListener() {

    }

    @Override
    protected void initView(View rootView) {
        libraryAdapter = new BookListAdapter(getActivity(), getDataHolder());
        recentReadingRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        recentReadingRecycler.addItemDecoration(dividerItemDecoration);
        recentReadingRecycler.setAdapter(libraryAdapter);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_main_view;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @OnClick({R.id.now_reading_book_cover, R.id.english_bookshelf, R.id.chinese_bookshelf, R.id.small_language_bookshelf, R.id.book_name_search, R.id.author_search, R.id.image_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.now_reading_book_cover:
                break;
            case R.id.english_bookshelf:
                break;
            case R.id.chinese_bookshelf:
                break;
            case R.id.small_language_bookshelf:
                break;
            case R.id.book_name_search:
                break;
            case R.id.author_search:
                break;
            case R.id.image_next:
                break;
        }
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getActivity());
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

}
