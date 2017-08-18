package com.onyx.android.dr.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.DividerItemDecoration;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.SearchBookView;
import com.onyx.android.dr.presenter.SearchBookPresenter;
import com.onyx.android.dr.view.CustomSearchView;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-8-14.
 */

public class SearchBookActivity extends BaseActivity implements SearchBookView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.custom_search_view)
    CustomSearchView customSearchView;
    @Bind(R.id.search_book_recycler)
    SinglePageRecyclerView searchBookRecycler;
    private SearchBookPresenter searchBookPresenter;
    private LibraryDataHolder dataHolder;
    private BookListAdapter bookListAdapter;
    private int row = DRApplication.getInstance().getResources().getInteger(R.integer.book_list_recycler_row);
    private int col = DRApplication.getInstance().getResources().getInteger(R.integer.book_list_recycler_column);
    private String type = Constants.NAME_SEARCH;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_search_book;
    }

    @Override
    protected void initConfig() {
        Intent intent = getIntent();
        type = intent.getStringExtra(Constants.SEARCH_TYPE);
        searchBookPresenter = new SearchBookPresenter(this, type);
    }

    @Override
    protected void initView() {
        bookListAdapter = new BookListAdapter(this, getDataHolder());
        searchBookRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        searchBookRecycler.addItemDecoration(dividerItemDecoration);
        searchBookRecycler.setAdapter(bookListAdapter);
        initPagination();
        searchBookRecycler.setFocusable(true);
        searchBookRecycler.setFocusableInTouchMode(true);
        customSearchView.setSearchViewListener(new CustomSearchView.SearchViewListener() {
            @Override
            public void onRefreshAutoComplete(String text) {
                if (StringUtils.isNullOrEmpty(text)) {
                    searchBookPresenter.getHistory(type);
                } else {
                    searchBookPresenter.searchBook(getDataHolder(), text, false);
                }
            }

            @Override
            public void onSearch(String text) {
                if (StringUtils.isNotBlank(text)) {
                    searchBookPresenter.searchBook(getDataHolder(), text, true);
                    searchBookPresenter.insertHistory(text, type);
                }
                searchBookRecycler.requestFocus();
            }

            @Override
            public void clearHistory() {
                searchBookPresenter.clearHistory(type);
            }
        });

        searchBookRecycler.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }
        });
    }

    private String getSearchTitle() {
        String title = "";
        switch (type) {
            case Constants.NAME_SEARCH:
                title = getString(R.string.book_name_search);
                break;
            case Constants.AUTHOR_SEARCH:
                title = getString(R.string.author_search);
                break;
        }
        return title;
    }

    @Override
    protected void initData() {
        titleBarTitle.setText(getSearchTitle());
        customSearchView.setEditTextHint(getSearchTitle());
    }

    @OnClick(R.id.menu_back)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                finish();
                break;
        }
    }

    @Override
    public void setResult(QueryResult<Metadata> result) {
        Map<String, CloseableReference<Bitmap>> bitmaps = null;
        if (result.list != null && result.list.size() > 0) {
            bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(this, DRApplication.getCloudStore().getCloudManager(), result.list);
        }
        updateContentView(getLibraryDataModel(result, bitmaps));
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    @Override
    public void setHint(List<String> books) {
        customSearchView.setSearchResult(books);
    }

    @Override
    public void setHistory(List<String> books) {
        customSearchView.setSearchHistory(books);
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(this);
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(row, col, 0);
        pagination.setCurrentPage(0);
    }

    private void prevPage() {
        if (!getPagination().prevPage()) {
            return;
        }
        QueryArgs queryArgs = dataHolder.getCloudViewInfo().prevPage();
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(this, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                preLoadPrev();
            }
        });
    }


    private void preLoadPrev() {
        int preLoadPage = getPagination().getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        CloudContentListRequest listRequest = new CloudContentListRequest(dataHolder.getCloudViewInfo().pageQueryArgs(preLoadPage));
        DRApplication.getCloudStore().submitRequestToSingle(this, listRequest, null);
    }

    private void nextPage() {
        if (!getPagination().nextPage()) {
            return;
        }
        QueryArgs queryArgs = dataHolder.getCloudViewInfo().nextPage();
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(this, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                preloadNext();
            }
        });
    }


    private void preloadNext() {
        int preLoadPage = getPagination().getCurrentPage() + 1;
        if (preLoadPage >= getPagination().pages()) {
            return;
        }
        QueryArgs queryArgs = dataHolder.getCloudViewInfo().pageQueryArgs(preLoadPage);
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(this, listRequest, null);
    }

    private QueryPagination getPagination() {
        return dataHolder.getCloudViewInfo().getQueryPagination();
    }

    private void updateContentView(LibraryDataModel libraryDataModel) {
        if (isContentViewInvalid()) {
            return;
        }
        bookListAdapter.updateContentView(libraryDataModel);
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(row, col, totalCount);
    }

    private int getTotalCount() {
        LibraryDataModel dataModel = dataHolder.getCloudViewInfo().getLibraryDataModel();
        return dataModel.bookCount;
    }

    private boolean isContentViewInvalid() {
        return searchBookRecycler == null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (customSearchView.getHintListStatus()) {
                customSearchView.showHintList(false);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
