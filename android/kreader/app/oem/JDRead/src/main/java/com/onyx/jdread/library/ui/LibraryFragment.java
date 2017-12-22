package com.onyx.jdread.library.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.api.device.EpdDeviceManager;
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
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.databinding.FragmentLibraryBinding;
import com.onyx.jdread.event.ModifyLibraryDataEvent;
import com.onyx.jdread.library.action.LibraryMoveToAction;
import com.onyx.jdread.library.action.MetadataDeleteAction;
import com.onyx.jdread.library.action.RxMetadataLoadAction;
import com.onyx.jdread.library.adapter.ModelAdapter;
import com.onyx.jdread.library.event.DeleteBookEvent;
import com.onyx.jdread.library.event.LibraryBackEvent;
import com.onyx.jdread.library.event.LibraryDeleteEvent;
import com.onyx.jdread.library.event.LibraryDeleteIncludeBookEvent;
import com.onyx.jdread.library.event.LibraryManageEvent;
import com.onyx.jdread.library.event.LibraryMenuEvent;
import com.onyx.jdread.library.event.LibraryRenameEvent;
import com.onyx.jdread.library.event.MoveToLibraryEvent;
import com.onyx.jdread.library.event.MyBookEvent;
import com.onyx.jdread.library.event.SortByNameEvent;
import com.onyx.jdread.library.event.SortByTimeEvent;
import com.onyx.jdread.library.event.WifiPassBookEvent;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.model.LibraryViewDataModel;
import com.onyx.jdread.library.model.PageIndicatorModel;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.library.view.MenuPopupWindow;
import com.onyx.jdread.library.view.SingleItemManageDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by huxiaomao on 2017/12/7.
 */

public class LibraryFragment extends BaseFragment {

    private DataBundle dataBundle;
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
        getEventBus().register(this);
    }

    private EventBus getEventBus() {
        return dataBundle.getEventBus();
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
        loadAction.execute(dataBundle, new RxCallback() {
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
        pageIndicatorModel.updateTotal(totalCount);
        dataBundle.getLibraryViewDataModel().updateDeletePage();
    }

    private int getTotalCount() {
        return dataBundle.getLibraryViewDataModel().count.get();
    }

    private QueryArgs libraryBuildQueryArgs() {
        QueryArgs args = dataBundle.getLibraryViewDataModel().libraryQuery();
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
        pagination = dataBundle.getLibraryViewDataModel().getQueryPagination();
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
        if (!pagination.nextPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().nextPage(), false);
        loadAction.setLoadFromCache(true);
        loadAction.execute(dataBundle, new RxCallback() {
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
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().pageQueryArgs(preLoadPage), false);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, null);
    }

    private boolean isLoadMetadata() {
        return true;
    }

    private void prevPage() {
        if (!pagination.prevPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().prevPage(), false);
        loadAction.setLoadFromCache(true);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, new RxCallback() {
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
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().pageQueryArgs(preLoadPage), false);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, null);
    }

    private void initDataBundle() {
        dataBundle = JDReadApplication.getDataBundle();
        dataBundle.setLibraryViewDataModel(LibraryViewDataModel.create(dataBundle.getEventBus(), row, col));
        libraryBinding.setLibraryModel(dataBundle.getLibraryViewDataModel());
    }

    public SinglePageRecyclerView getContentView() {
        return libraryBinding.contentPageView;
    }

    private boolean processBackRequest() {
        if (!CollectionUtils.isNullOrEmpty(dataBundle.getLibraryViewDataModel().libraryPathList)) {
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
        dataBundle.getLibraryViewDataModel().libraryPathList.remove(dataBundle.getLibraryViewDataModel().libraryPathList.size() - 1);
        setTitle();
        loadData();
    }

    private void setTitle() {
        int size = dataBundle.getLibraryViewDataModel().libraryPathList.size();
        if (size > 0) {
            dataBundle.getLibraryViewDataModel().title.set(dataBundle.getLibraryViewDataModel().libraryPathList.get(size - 1).title.get());
        } else {
            dataBundle.getLibraryViewDataModel().title.set(isMultiSelectionMode() ? getString(R.string.manage_book) : "");
        }
    }

    private void quitMultiSelectionMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.NORMAL_MODE);
        dataBundle.getLibraryViewDataModel().quitManageMode();
        setTitle();
        showMangeMenu();
    }

    @Subscribe
    public void onLibraryBackEvent(LibraryBackEvent event) {
        processBackRequest();
    }

    @Subscribe
    public void onLibraryManageEvent(LibraryManageEvent event) {
        dataBundle.getLibraryViewDataModel().title.set(getString(R.string.manage_book));
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        getIntoMultiSelectMode();
        showMangeMenu();
    }

    @Subscribe
    public void onLibraryMenuEvent(LibraryMenuEvent event) {
        MenuPopupWindow menuPopupWindow = new MenuPopupWindow(getActivity(), getEventBus());
        menuPopupWindow.showPopupWindow(libraryBinding.imageMenu, dataBundle.getLibraryViewDataModel().getMenuData());
    }

    @Subscribe
    public void onSortByTimeEvent(SortByTimeEvent event) {
        dataBundle.getLibraryViewDataModel().updateSortBy(SortBy.CreationTime, SortOrder.Asc);
        refreshData();
    }

    private void refreshData() {
        pagination.setCurrentPage(0);
        loadData();
    }

    @Subscribe
    public void onSortByNameEvent(SortByNameEvent event) {
        dataBundle.getLibraryViewDataModel().updateSortBy(SortBy.Name, SortOrder.Asc);
        refreshData();
    }

    @Subscribe
    public void onMyBookEvent(MyBookEvent event) {

    }

    @Subscribe
    public void onWifiPassBookEvent(WifiPassBookEvent event) {

    }

    @Subscribe
    public void onLibraryRenameEvent(LibraryRenameEvent event) {

    }

    @Subscribe
    public void onLibraryDeleteEvent(LibraryDeleteEvent event) {

    }

    @Subscribe
    public void onLibraryDeleteIncludeBookEvent(LibraryDeleteIncludeBookEvent event) {

    }

    @Subscribe
    public void onItemLongClickEvent(ItemLongClickEvent event) {
        DataModel currentChosenModel = event.getDataModel();
        dataBundle.getLibraryViewDataModel().addItemSelected(currentChosenModel, true);
        showSingleMangeDialog(currentChosenModel);
    }

    private void showSingleMangeDialog(DataModel currentChosenModel) {
        SingleItemManageDialog.DialogModel dialogModel = new SingleItemManageDialog.DialogModel(dataBundle.getEventBus());
        dialogModel.dataModel.set(currentChosenModel);
        SingleItemManageDialog.Builder builder = new SingleItemManageDialog.Builder(getContext(), dialogModel);
        SingleItemManageDialog dialog = builder.create();
        dialog.show();
    }

    @Subscribe
    public void onItemClickEvent(ItemClickEvent event) {
        if (isMultiSelectionMode()) {
            processMultiModeItemClick(event.getModel());
        } else {
            processNormalModeItemClick(event.getModel());
        }
    }

    @Subscribe
    public void onModifyLibraryDataEvent(ModifyLibraryDataEvent event) {
        dataBundle.getLibraryViewDataModel().libraryPathList.clear();
        pagination.setCurrentPage(0);
        loadData(libraryBuildQueryArgs(), false);
    }

    @Subscribe
    public void onDeleteBookEvent(DeleteBookEvent event) {
        deleteBook();
    }

    @Subscribe
    public void onMoveToLibraryEvent(MoveToLibraryEvent event) {
        LibraryMoveToAction moveToAction = new LibraryMoveToAction(getActivity());
        moveToAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                quitMultiSelectionMode();
                int deletePageCount = dataBundle.getLibraryViewDataModel().getDeletePageCount();
                loadData(dataBundle.getLibraryViewDataModel().gotoPage(pagination.getCurrentPage() - deletePageCount), false);
            }
        });
    }

    private void deleteBook() {
        MetadataDeleteAction metadataDeleteAction = new MetadataDeleteAction(getActivity());
        metadataDeleteAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                quitMultiSelectionMode();
                int deletePageCount = dataBundle.getLibraryViewDataModel().getDeletePageCount();
                loadData(dataBundle.getLibraryViewDataModel().gotoPage(pagination.getCurrentPage() - deletePageCount), false);
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
        dataBundle.getLibraryViewDataModel().getSelectHelper().putLibrarySelectedModelMap(model.idString.get());
    }

    private void addLibraryToParentRefList(DataModel model) {
        dataBundle.getLibraryViewDataModel().libraryPathList.add(model);
        dataBundle.getLibraryViewDataModel().title.set(model.title.get());
        showMangeMenu();
    }

    private void showMangeMenu() {
        dataBundle.getLibraryViewDataModel().setShowTopMenu(!isMultiSelectionMode());
        dataBundle.getLibraryViewDataModel().setShowBottomMenu(isMultiSelectionMode());
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
        ActivityUtil.startActivitySafely(getContext().getApplicationContext(),
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(getContext().getApplicationContext()));
    }

    private void processMultiModeItemClick(DataModel dataModel) {
        if (dataModel.type.get() == ModelType.TYPE_LIBRARY) {
            processLibraryItem(dataModel);
            return;
        }
        dataModel.checked.set(!dataModel.checked.get());
        dataBundle.getLibraryViewDataModel().clickItem(dataModel);
        updateContentView();
    }

    private void getIntoMultiSelectMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        dataBundle.getLibraryViewDataModel().clearItemSelectedList();
        updateContentView();
    }

    private boolean isMultiSelectionMode() {
        return modelAdapter.isMultiSelectionMode();
    }
}
