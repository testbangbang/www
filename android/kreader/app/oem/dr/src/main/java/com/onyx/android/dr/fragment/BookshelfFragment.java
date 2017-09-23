package com.onyx.android.dr.fragment;

import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.action.LibraryGotoPageAction;
import com.onyx.android.dr.adapter.BookListAdapter;
import com.onyx.android.dr.adapter.BookshelfGroupAdapter;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.BackToMainViewEvent;
import com.onyx.android.dr.event.EBookListEvent;
import com.onyx.android.dr.event.HideLoadingProgressEvent;
import com.onyx.android.dr.event.ShowLoadingProgressEvent;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.BookshelfView;
import com.onyx.android.dr.presenter.BookshelfPresenter;
import com.onyx.android.dr.reader.view.DisableScrollGridManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.dr.view.PageIndicator;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.LibraryDataModel;
import com.onyx.android.sdk.data.LibraryViewInfo;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by hehai on 17-7-11.
 */

public class BookshelfFragment extends BaseFragment implements BookshelfView {
    @Bind(R.id.image_view_back)
    ImageView imageViewBack;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.menu_back)
    LinearLayout menuBack;
    @Bind(R.id.title_bar_title)
    TextView titleBarTitle;
    @Bind(R.id.title_bar_right_menu)
    TextView titleBarRightMenu;
    @Bind(R.id.title_bar_right_icon_one)
    ImageView share;
    @Bind(R.id.bookshelf_book_search)
    ImageView bookshelfBookSearch;
    @Bind(R.id.bookshelf_type_toggle)
    TextView bookshelfTypeToggle;
    @Bind(R.id.bookshelf_groups_recycler)
    SinglePageRecyclerView bookshelfGroupsRecycler;
    @Bind(R.id.bookshelf_tab)
    TabLayout bookshelfTab;
    @Bind(R.id.bookshelf_tab_title)
    LinearLayout bookshelfTabTitle;
    @Bind(R.id.page_panel)
    LinearLayout pagePanel;
    @Bind(R.id.page_indicator_layout)
    RelativeLayout pageIndicatorLayout;
    private BookshelfGroupAdapter adapter;
    private String mode;
    private BookshelfPresenter bookshelfPresenter;
    private LibraryDataHolder dataHolder;
    private BookListAdapter listAdapter;
    private List<Library> libraryList;
    private List<String> languageList;
    private View titleBar;
    private PageIndicator pageIndicator;

    @Override
    protected void initListener() {
        bookshelfTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadDataWithMode(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        bookshelfGroupsRecycler.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
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

    @Override
    protected void initView(View rootView) {
        adapter = new BookshelfGroupAdapter(getActivity());
        bookshelfGroupsRecycler.setLayoutManager(new DisableScrollGridManager(DRApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(DRApplication.getInstance(), DividerItemDecoration.VERTICAL);
        bookshelfGroupsRecycler.addItemDecoration(dividerItemDecoration);
        bookshelfGroupsRecycler.setAdapter(adapter);
        menuBack = (LinearLayout) rootView.findViewById(R.id.menu_back);

        listAdapter = new BookListAdapter(getActivity(), getDataHolder());
        initPageIndicator(pageIndicatorLayout);
        listAdapter.setShowName(true);
        listAdapter.setCanChecked(true);
        titleBar = rootView.findViewById(R.id.bookshelf_title_bar);
        share.setImageResource(R.drawable.ic_reader_share);
        share.setVisibility(View.VISIBLE);
    }

    @Override
    protected void loadData() {
        if (bookshelfPresenter == null) {
            bookshelfPresenter = new BookshelfPresenter(this);
        }
        mode = DRPreferenceManager.getBookshelfType(getActivity(), Constants.LANGUAGE_BOOKSHELF);
        loadTabWithMode(mode);
    }

    private void loadTabWithMode(String mode) {
        switch (mode) {
            case Constants.LANGUAGE_BOOKSHELF:
                bookshelfPresenter.getLanguageList();
                break;
            case Constants.GRADED_BOOKSHELF:
                bookshelfPresenter.getLibraryList();
                break;
        }
    }

    private void loadDataWithMode(int position) {
        switch (mode) {
            case Constants.LANGUAGE_BOOKSHELF:
                loadBookshelf(languageList.get(position));
                break;
            case Constants.GRADED_BOOKSHELF:
                loadLibrary(position);
                break;
        }
        EventBus.getDefault().post(new ShowLoadingProgressEvent());
    }

    private void loadBookshelf(String language) {
        if (titleBarTitle != null) {
            titleBarTitle.setText(String.format(getString(R.string.bookshelf_format), language));
            bookshelfPresenter.getBookshelf(language, getDataHolder());
        }
    }

    private void loadLibrary(int position) {
        if (!CollectionUtils.isNullOrEmpty(libraryList)) {
            Library library = libraryList.get(position);
            if (library != null) {
                if (titleBarTitle != null) {
                    titleBarTitle.setText(library.getName());
                }
                QueryArgs queryArgs = getDataHolder().getCloudViewInfo().buildLibraryQuery(library.getIdString());
                queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
                queryArgs.conditionGroup.and(CloudMetadata_Table.nativeAbsolutePath.isNotNull());
                bookshelfPresenter.getLibrary(queryArgs);
            }
        }
    }

    @Override
    protected int getRootView() {
        return R.layout.fragment_bookshelf;
    }

    @Override
    public boolean onKeyBack() {
        back();
        return true;
    }

    private void back() {
        if (bookshelfGroupsRecycler.getAdapter() instanceof BookListAdapter) {
            bookshelfGroupsRecycler.setAdapter(adapter);
            pageIndicatorLayout.setVisibility(View.GONE);
            titleBar.setVisibility(View.GONE);
            bookshelfTabTitle.setVisibility(View.VISIBLE);
            listAdapter.setShowCheckbox(false);
        } else {
            EventBus.getDefault().post(new BackToMainViewEvent());
        }
    }

    @OnClick({R.id.menu_back, R.id.bookshelf_book_search, R.id.bookshelf_type_toggle, R.id.title_bar_right_icon_one})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_back:
                back();
                break;
            case R.id.bookshelf_book_search:
                search();
                break;
            case R.id.bookshelf_type_toggle:
                toggleBookshelfMode();
                break;
            case R.id.title_bar_right_icon_one:
                listAdapter.getSelectedMetadata();
                break;
        }
    }

    private void toggleBookshelfMode() {
        if (mode.equals(Constants.LANGUAGE_BOOKSHELF)) {
            mode = Constants.GRADED_BOOKSHELF;
            bookshelfTypeToggle.setText(getString(R.string.grade_bookshelf));
        } else {
            mode = Constants.LANGUAGE_BOOKSHELF;
            bookshelfTypeToggle.setText(getString(R.string.language_bookshelf));
        }
        loadTabWithMode(mode);
        DRPreferenceManager.saveBookshelfType(DRApplication.getInstance(), mode);
    }

    private void search() {
        ActivityManager.startSearchBookActivity(getActivity());
    }

    @Override
    public void setBooks(List<Metadata> result) {
        bookshelfGroupsRecycler.setAdapter(listAdapter);
        pageIndicatorLayout.setVisibility(View.VISIBLE);
        QueryResult<Metadata> queryResult = new QueryResult<>();
        queryResult.list = result;
        queryResult.count = result.size();
        Map<String, CloseableReference<Bitmap>> bitmaps = DataManagerHelper.loadCloudThumbnailBitmapsWithCache(DRApplication.getInstance(), DRApplication.getCloudStore().getCloudManager(), queryResult.list);
        updateContentView(getLibraryDataModel(queryResult, bitmaps));
        EventBus.getDefault().post(new HideLoadingProgressEvent());
    }

    @Override
    public void setLanguageCategory(Map<String, List<Metadata>> map) {
        EventBus.getDefault().post(new HideLoadingProgressEvent());
        adapter.setMap(map);
    }

    @Override
    public void setLibraryList(List<Library> list) {
        EventBus.getDefault().post(new HideLoadingProgressEvent());
        this.libraryList = list;
        bookshelfTab.removeAllTabs();
        for (Library lib : libraryList) {
            bookshelfTab.addTab(bookshelfTab.newTab().setText(lib.getName()));
        }
        int selectedTabPosition = bookshelfTab.getSelectedTabPosition();
        loadLibrary(selectedTabPosition);
    }

    @Override
    public void setLanguageList(List<String> languageList) {
        this.languageList = languageList;
        bookshelfTab.removeAllTabs();
        for (String language : languageList) {
            bookshelfTab.addTab(bookshelfTab.newTab().setText(language));
        }
        int selectedTabPosition = bookshelfTab.getSelectedTabPosition();
        loadBookshelf(languageList.get(selectedTabPosition));
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getActivity());
            dataHolder.setCloudManager(DRApplication.getCloudStore().getCloudManager());
        }
        return dataHolder;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEBookListEvent(EBookListEvent event) {
        titleBar.setVisibility(View.VISIBLE);
        bookshelfTabTitle.setVisibility(View.GONE);
        if (mode.equals(Constants.LANGUAGE_BOOKSHELF)) {
            titleBarTitle.setText(languageList.get(bookshelfTab.getSelectedTabPosition()) + "/" + event.getLanguage());
        } else {
            titleBarTitle.setText(libraryList.get(bookshelfTab.getSelectedTabPosition()).getName() + "/" + event.getLanguage());
        }
        bookshelfPresenter.getBooks(event.getLanguage());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private LibraryDataModel getLibraryDataModel(QueryResult<Metadata> result, Map<String, CloseableReference<Bitmap>> map) {
        return LibraryViewInfo.buildLibraryDataModel(result, map);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataWithMode(bookshelfTab.getSelectedTabPosition());
    }

    private void updateContentView(LibraryDataModel libraryDataModel) {
        if (isContentViewInvalid()) {
            return;
        }
        listAdapter.updateContentView(libraryDataModel);
        updatePageIndicator();
    }

    private boolean isContentViewInvalid() {
        return bookshelfGroupsRecycler == null || pageIndicator == null;
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        getPagination().resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), totalCount);
        pageIndicator.resetGPaginator(getPagination());
        pageIndicator.updateTotal(totalCount);
        pageIndicator.updateCurrentPage(totalCount);
    }

    private int getTotalCount() {
        LibraryDataModel dataModel = dataHolder.getCloudViewInfo().getLibraryDataModel();
        return dataModel.bookCount + dataModel.libraryCount;
    }

    private QueryPagination getPagination() {
        return dataHolder.getCloudViewInfo().getQueryPagination();
    }

    private void initPageIndicator(ViewGroup parentView) {
        if (parentView == null) {
            return;
        }
        initPagination();
        pageIndicator = new PageIndicator(parentView.findViewById(R.id.page_indicator_layout), getPagination());
        pageIndicator.showRefresh(false);
        pageIndicator.setTotalFormat(getString(R.string.total_format));
        pageIndicator.setPageChangedListener(new PageIndicator.PageChangedListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }

            @Override
            public void gotoPage(int page) {
                showGotoPageAction(page);
            }
        });
        pageIndicator.setDataRefreshListener(new PageIndicator.DataRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    private void initPagination() {
        QueryPagination pagination = getPagination();
        pagination.resize(listAdapter.getRowCount(), listAdapter.getColumnCount(), 0);
        pagination.setCurrentPage(0);
    }

    private void prevPage() {
        if (!getPagination().prevPage()) {
            return;
        }
        final CloudContentListRequest listRequest = new CloudContentListRequest(dataHolder.getCloudViewInfo().prevPage());
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, new BaseCallback() {
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
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, null);
    }

    private void nextPage() {
        if (!getPagination().nextPage()) {
            return;
        }
        QueryArgs queryArgs = dataHolder.getCloudViewInfo().nextPage();
        final CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, new BaseCallback() {
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
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, null);
    }

    private void showGotoPageAction(int currentPage) {
        final LibraryGotoPageAction gotoPageAction = new LibraryGotoPageAction(getActivity(), currentPage,
                getPagination().pages());
        gotoPageAction.execute(dataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                int newPage = gotoPageAction.getSelectPage();
                gotoPageImpl(newPage);
            }
        });
    }

    private void gotoPageImpl(int page) {
        final int originPage = getPagination().getCurrentPage();
        loadData(dataHolder.getCloudViewInfo().gotoPage(page), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    getPagination().setCurrentPage(originPage);
                    return;
                }
                CloudContentListRequest listRequest = (CloudContentListRequest) request;
                QueryResult<Metadata> result = listRequest.getProductResult();
                updateContentView(getLibraryDataModel(result, listRequest.getThumbnailMap()));
                preLoadPrev();
                preloadNext();
            }
        });
    }

    private void loadData(QueryArgs queryArgs, BaseCallback callback) {
        CloudContentListRequest listRequest = new CloudContentListRequest(queryArgs);
        DRApplication.getCloudStore().submitRequestToSingle(DRApplication.getInstance(), listRequest, callback);
    }
}
