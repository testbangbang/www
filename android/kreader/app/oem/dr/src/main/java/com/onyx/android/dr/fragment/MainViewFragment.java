package com.onyx.android.dr.fragment;

import android.graphics.Bitmap;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.event.BookshelfEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.view.ImageTextButton;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.CloudDataProvider;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

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
    private ImageTextButton englishBookshelf;
    private ImageTextButton chineseBookshelf;
    private ImageTextButton smallLanguageBookshelf;

    @Override
    protected void initListener() {
        englishBookshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new BookshelfEvent("english"));
            }
        });
        chineseBookshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new BookshelfEvent("chinese"));
            }
        });
        smallLanguageBookshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new BookshelfEvent("other"));
            }
        });
    }

    @Override
    protected void initView(View rootView) {
        libraryAdapter = new BookListAdapter(getActivity(), getDataHolder());
        libraryAdapter.setRowAndCol(2, 3);
        recentReadingRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        recentReadingRecycler.addItemDecoration(dividerItemDecoration);
        recentReadingRecycler.setAdapter(libraryAdapter);
        englishBookshelf = (ImageTextButton) rootView.findViewById(R.id.english_bookshelf);
        chineseBookshelf = (ImageTextButton) rootView.findViewById(R.id.chinese_bookshelf);
        smallLanguageBookshelf = (ImageTextButton) rootView.findViewById(R.id.small_language_bookshelf);
    }

    @Override
    protected void loadData() {
        CloudDataProvider localDataProvider = new CloudDataProvider(DRApplication.getCloudStore().getCloudConf());
        QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.CreationTime, SortOrder.Desc);
        QueryResult<Metadata> queryResult = localDataProvider.findMetadataResultByQueryArgs(getActivity(), queryArgs);
        Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(getActivity(), DRApplication.getCloudStore().getCloudManager(), queryResult.list);
        libraryAdapter.updateContentView(getLibraryDataModel(queryResult, bitmaps));
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_main_view;
    }

    @Override
    public boolean onKeyBack() {
        return false;
    }

    @OnClick({R.id.now_reading_book_cover, R.id.book_name_search, R.id.author_search, R.id.image_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.now_reading_book_cover:
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

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }
}
