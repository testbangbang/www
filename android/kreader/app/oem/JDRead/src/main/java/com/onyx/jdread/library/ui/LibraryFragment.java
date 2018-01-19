package com.onyx.jdread.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.event.ItemLongClickEvent;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentLibraryBinding;
import com.onyx.jdread.library.action.LibraryDeleteAction;
import com.onyx.jdread.library.action.LibraryMoveToAction;
import com.onyx.jdread.library.action.LibraryRenameAction;
import com.onyx.jdread.library.action.MetadataDeleteAction;
import com.onyx.jdread.library.action.RxMetadataLoadAction;
import com.onyx.jdread.library.adapter.ModelAdapter;
import com.onyx.jdread.library.event.BookDetailEvent;
import com.onyx.jdread.library.event.DeleteBookEvent;
import com.onyx.jdread.library.event.LibraryBackEvent;
import com.onyx.jdread.library.event.LibraryDeleteEvent;
import com.onyx.jdread.library.event.LibraryDeleteIncludeBookEvent;
import com.onyx.jdread.library.event.LibraryManageEvent;
import com.onyx.jdread.library.event.LibraryMenuEvent;
import com.onyx.jdread.library.event.LibraryRenameEvent;
import com.onyx.jdread.library.event.MoveToLibraryEvent;
import com.onyx.jdread.library.event.MyBookEvent;
import com.onyx.jdread.library.event.SearchBookEvent;
import com.onyx.jdread.library.event.SortByNameEvent;
import com.onyx.jdread.library.event.SortByTimeEvent;
import com.onyx.jdread.library.event.WifiPassBookEvent;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.model.LibraryViewDataModel;
import com.onyx.jdread.library.model.PageIndicatorModel;
import com.onyx.jdread.library.view.MenuPopupWindow;
import com.onyx.jdread.library.view.SingleItemManageDialog;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.event.ModifyLibraryDataEvent;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.common.OpenBookHelper;
import com.onyx.jdread.shop.ui.BookDetailFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class LibraryFragment extends BaseFragment {

    private LibraryDataBundle libraryDataBundle;
    private FragmentLibraryBinding libraryBinding;
    private SinglePageRecyclerView contentView;
    private ModelAdapter modelAdapter;
    private int row = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_row);
    private int col = JDReadApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_col);
    private GPaginator pagination;
    private PageIndicatorModel pageIndicatorModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        libraryBinding = FragmentLibraryBinding.inflate(inflater, container, false);
        libraryBinding.setView(this);
        initDataBundle();
        initPageRecyclerView();
        initPageIndicator();
        initData();
        return libraryBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(libraryBuildQueryArgs(), false);
        getEventBus().register(this);
    }

    private EventBus getEventBus() {
        return libraryDataBundle.getEventBus();
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        loadData(libraryBuildQueryArgs());
    }

    private void loadData(QueryArgs queryArgs) {
        loadData(queryArgs, true);
    }

    private void loadData(QueryArgs queryArgs, boolean loadFromCache) {
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(queryArgs);
        loadAction.setLoadFromCache(loadFromCache);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        preloadNext();
    }

    private void updateContentView() {
        SinglePageRecyclerView contentPageView = getContentView();
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        int totalCount = getTotalCount();
        pagination.resize(row, col, totalCount);
        pageIndicatorModel.updateCurrentPage(totalCount);
        pageIndicatorModel.setTotalFormat(libraryDataBundle.getLibraryViewDataModel().libraryPathList.size() == 0 ? getString(R.string.total) : getString(R.string.bosom));
        pageIndicatorModel.updateTotal(totalCount);
        libraryDataBundle.getLibraryViewDataModel().updateDeletePage();
    }

    private int getTotalCount() {
        return libraryDataBundle.getLibraryViewDataModel().count.get();
    }

    private QueryArgs libraryBuildQueryArgs() {
        QueryArgs args = libraryDataBundle.getLibraryViewDataModel().libraryQuery();
        QueryBuilder.andWith(args.conditionGroup, null);
        return args;
    }

    private void initPageRecyclerView() {
        SinglePageRecyclerView contentPageView = getContentView();
        contentPageView.setLayoutManager(new DisableScrollGridManager(getContext().getApplicationContext()));
        modelAdapter = new ModelAdapter();
        modelAdapter.setRowAndCol(row, col);
        contentPageView.setAdapter(modelAdapter);
        contentPageView.setOnChangePageListener(new SinglePageRecyclerView.OnChangePageListener() {
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

    private void initPageIndicator() {
        pagination = libraryDataBundle.getLibraryViewDataModel().getQueryPagination();
        pagination.setCurrentPage(0);
        pageIndicatorModel = new PageIndicatorModel(pagination, new PageIndicatorModel.PageChangedListener() {
            @Override
            public void prev() {
                prevPage();
            }

            @Override
            public void next() {
                nextPage();
            }

            @Override
            public void gotoPage(int currentPage) {

            }

            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        libraryBinding.setIndicatorModel(pageIndicatorModel);
    }

    private void nextPage() {
        QueryArgs queryArgs;
        if (!pagination.nextPage()) {
            queryArgs = libraryDataBundle.getLibraryViewDataModel().firstPage();
        } else {
            queryArgs = libraryDataBundle.getLibraryViewDataModel().nextPage();
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(queryArgs, false);
        loadAction.setLoadFromCache(true);
        loadAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        preloadNext();
    }

    private void preloadNext() {
        int preLoadPage = pagination.getCurrentPage() + 1;
        if (preLoadPage >= pagination.pages()) {
            preLoadPage = 0;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(libraryDataBundle.getLibraryViewDataModel().pageQueryArgs(preLoadPage), false);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(libraryDataBundle, null);
    }

    private boolean isLoadMetadata() {
        return true;
    }

    private void prevPage() {
        QueryArgs queryArgs;
        if (!pagination.prevPage()) {
            queryArgs = libraryDataBundle.getLibraryViewDataModel().lastPage();
        } else {
            queryArgs = libraryDataBundle.getLibraryViewDataModel().prevPage();
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(queryArgs, false);
        loadAction.setLoadFromCache(true);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        preloadPrev();
    }

    private void preloadPrev() {
        int preLoadPage = pagination.getCurrentPage() - 1;
        if (preLoadPage < 0) {
            preLoadPage = pagination.lastPage();
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(libraryDataBundle.getLibraryViewDataModel().pageQueryArgs(preLoadPage), false);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(libraryDataBundle, null);
    }

    private void initDataBundle() {
        libraryDataBundle = LibraryDataBundle.getInstance();
        libraryDataBundle.setLibraryViewDataModel(LibraryViewDataModel.create(libraryDataBundle.getEventBus(), row, col));
        libraryBinding.setLibraryModel(libraryDataBundle.getLibraryViewDataModel());
    }

    public SinglePageRecyclerView getContentView() {
        return libraryBinding.contentPageView;
    }

    private boolean processBackRequest() {
        if (!CollectionUtils.isNullOrEmpty(libraryDataBundle.getLibraryViewDataModel().libraryPathList)) {
            removeLastParentLibrary();
            loadData(libraryBuildQueryArgs(), false);
            return false;
        }
        if (isMultiSelectionMode()) {
            quitMultiSelectionMode();
            loadData(libraryBuildQueryArgs(), false);
            return false;
        }
        return true;
    }

    private void removeLastParentLibrary() {
        libraryDataBundle.getLibraryViewDataModel().libraryPathList.remove(libraryDataBundle.getLibraryViewDataModel().libraryPathList.size() - 1);
        setTitle();
        loadData();
    }

    private void setTitle() {
        int size = libraryDataBundle.getLibraryViewDataModel().libraryPathList.size();
        if (size > 0) {
            libraryDataBundle.getLibraryViewDataModel().title.set(libraryDataBundle.getLibraryViewDataModel().libraryPathList.get(size - 1).title.get());
        } else {
            libraryDataBundle.getLibraryViewDataModel().title.set(isMultiSelectionMode() ? getString(R.string.manage_book) : "");
        }
    }

    private void quitMultiSelectionMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.NORMAL_MODE);
        libraryDataBundle.getLibraryViewDataModel().clearSelectedData();
        setTitle();
        showMangeMenu();
    }

    @Subscribe
    public void onLibraryBackEvent(LibraryBackEvent event) {
        processBackRequest();
    }

    @Subscribe
    public void onSearchBookEvent(SearchBookEvent event) {
        setBundle(null);
        viewEventCallBack.gotoView(SearchBookFragment.class.getName());
    }

    @Subscribe
    public void onLibraryManageEvent(LibraryManageEvent event) {
        libraryDataBundle.getLibraryViewDataModel().title.set(getString(R.string.manage_book));
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        getIntoMultiSelectMode();
        showMangeMenu();
    }

    @Subscribe
    public void onLibraryMenuEvent(LibraryMenuEvent event) {
        MenuPopupWindow menuPopupWindow = new MenuPopupWindow(getActivity(), getEventBus());
        menuPopupWindow.setShowItemDecoration(true);
        menuPopupWindow.showPopupWindow(libraryBinding.imageMenu, libraryDataBundle.getLibraryViewDataModel().getMenuData());
    }

    @Subscribe
    public void onSortByTimeEvent(SortByTimeEvent event) {
        libraryDataBundle.getLibraryViewDataModel().updateSortBy(SortBy.CreationTime, SortOrder.Desc);
        refreshData();
    }

    private void refreshData() {
        loadData(libraryBuildQueryArgs(), false);
    }

    @Subscribe
    public void onSortByNameEvent(SortByNameEvent event) {
        libraryDataBundle.getLibraryViewDataModel().updateSortBy(SortBy.Name, SortOrder.Asc);
        refreshData();
    }

    @Subscribe
    public void onMyBookEvent(MyBookEvent event) {

    }

    @Subscribe
    public void onWifiPassBookEvent(WifiPassBookEvent event) {
        viewEventCallBack.gotoView(WiFiPassBookFragment.class.getName());
    }

    @Subscribe
    public void onLibraryRenameEvent(LibraryRenameEvent event) {
        LibraryRenameAction renameAction = new LibraryRenameAction(JDReadApplication.getInstance(), event.getDataModel());
        renameAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                refreshData();
            }
        });
    }

    @Subscribe
    public void onLibraryDeleteEvent(LibraryDeleteEvent event) {
        LibraryDeleteAction action = new LibraryDeleteAction(JDReadApplication.getInstance(), event.getDataModel(), false);
        action.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });
    }

    @Subscribe
    public void onLibraryDeleteIncludeBookEvent(LibraryDeleteIncludeBookEvent event) {
        LibraryDeleteAction action = new LibraryDeleteAction(JDReadApplication.getInstance(), event.getDataModel(), true);
        action.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                refreshData();
            }
        });
    }

    @Subscribe
    public void onItemLongClickEvent(ItemLongClickEvent event) {
        if (isMultiSelectionMode()) {
            return;
        }
        DataModel currentChosenModel = event.getDataModel();
        libraryDataBundle.getLibraryViewDataModel().addItemSelected(currentChosenModel, true);
        showSingleMangeDialog(currentChosenModel);
    }

    private void showSingleMangeDialog(DataModel currentChosenModel) {
        SingleItemManageDialog.DialogModel dialogModel = new SingleItemManageDialog.DialogModel(libraryDataBundle.getEventBus());
        dialogModel.dataModel.set(currentChosenModel);
        SingleItemManageDialog.Builder builder = new SingleItemManageDialog.Builder(JDReadApplication.getInstance(), dialogModel);
        SingleItemManageDialog dialog = builder.create();
        dialog.show();
    }

    @Subscribe
    public void onItemClickEvent(ItemClickEvent event) {
        if (isMultiSelectionMode()) {
            processMultiModeItemClick(event.getModel(), event.isLayoutClicked());
        } else {
            processNormalModeItemClick(event.getModel());
        }
    }

    @Subscribe
    public void onModifyLibraryDataEvent(ModifyLibraryDataEvent event) {
        libraryDataBundle.getLibraryViewDataModel().libraryPathList.clear();
        loadData(libraryBuildQueryArgs(), false);
    }

    @Subscribe
    public void onDeleteBookEvent(DeleteBookEvent event) {
        deleteBook();
    }

    @Subscribe
    public void onBookDetailEvent(BookDetailEvent event) {
        if (event.getDataModel().cloudId.get() != -1) {
            PreferenceManager.setLongValue(JDReadApplication.getInstance(), Constants.SP_KEY_BOOK_ID, event.getDataModel().cloudId.get());
            if (getViewEventCallBack() != null) {
                getViewEventCallBack().gotoView(BookDetailFragment.class.getName());
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.search_name_key), event.getDataModel().title.get());
            setBundle(bundle);
            viewEventCallBack.gotoView(SearchBookFragment.class.getName());
        }
    }

    @Subscribe
    public void onMoveToLibraryEvent(MoveToLibraryEvent event) {
        LibraryMoveToAction moveToAction = new LibraryMoveToAction(JDReadApplication.getInstance());
        moveToAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                libraryDataBundle.getLibraryViewDataModel().clearSelectedData();
                int deletePageCount = libraryDataBundle.getLibraryViewDataModel().getDeletePageCount();
                loadData(libraryDataBundle.getLibraryViewDataModel().gotoPage(pagination.getCurrentPage() - deletePageCount), false);
            }
        });
    }

    private void deleteBook() {
        MetadataDeleteAction metadataDeleteAction = new MetadataDeleteAction(JDReadApplication.getInstance());
        metadataDeleteAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                libraryDataBundle.getLibraryViewDataModel().clearSelectedData();
                int deletePageCount = libraryDataBundle.getLibraryViewDataModel().getDeletePageCount();
                loadData(libraryDataBundle.getLibraryViewDataModel().gotoPage(pagination.getCurrentPage() - deletePageCount), false);
            }
        });
    }

    private void processNormalModeItemClick(DataModel model) {
        if (model.type.get() == ModelType.TYPE_LIBRARY) {
            processLibraryItem(model);
        } else {
            processBookItemOpen(model);
        }
    }

    private void processLibraryItem(DataModel model) {
        addLibraryToParentRefList(model);
        loadData(libraryBuildQueryArgs(), false);
        libraryDataBundle.getLibraryViewDataModel().getSelectHelper().putLibrarySelectedModelMap(model.idString.get());
    }

    private void addLibraryToParentRefList(DataModel model) {
        libraryDataBundle.getLibraryViewDataModel().libraryPathList.add(model);
        libraryDataBundle.getLibraryViewDataModel().title.set(model.title.get());
        showMangeMenu();
    }

    private void showMangeMenu() {
        libraryDataBundle.getLibraryViewDataModel().setShowTopMenu(!isMultiSelectionMode());
        libraryDataBundle.getLibraryViewDataModel().setShowBottomMenu(isMultiSelectionMode());
        viewEventCallBack.hideOrShowFunctionBar(!isMultiSelectionMode());
    }

    private void processBookItemOpen(DataModel dataModel) {
        String filePath = dataModel.absolutePath.get();
        if (StringUtils.isNullOrEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setBookPath(filePath);
        OpenBookHelper.openBook(getContext(), documentInfo);
    }

    private void processMultiModeItemClick(DataModel dataModel, boolean layoutClicked) {
        if (dataModel.type.get() == ModelType.TYPE_LIBRARY) {
            processLibraryItem(dataModel);
            return;
        }
        if (layoutClicked) {
            dataModel.checked.set(!dataModel.checked.get());
        }
        libraryDataBundle.getLibraryViewDataModel().clickItem(dataModel);
        updateContentView();
    }

    private void getIntoMultiSelectMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        libraryDataBundle.getLibraryViewDataModel().clearItemSelectedList();
        updateContentView();
    }

    private boolean isMultiSelectionMode() {
        return modelAdapter.isMultiSelectionMode();
    }
}
