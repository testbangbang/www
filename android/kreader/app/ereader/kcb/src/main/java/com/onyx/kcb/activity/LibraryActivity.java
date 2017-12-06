package com.onyx.kcb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.event.ItemLongClickEvent;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.kcb.KCBApplication;
import com.onyx.kcb.R;
import com.onyx.kcb.action.ActionChain;
import com.onyx.kcb.action.ConfigFilterAction;
import com.onyx.kcb.action.ConfigSortAction;
import com.onyx.kcb.action.DisplayModeAction;
import com.onyx.kcb.action.LibraryBuildAction;
import com.onyx.kcb.action.LibraryRemoveFromAction;
import com.onyx.kcb.action.LibrarySelectionAction;
import com.onyx.kcb.action.LibraryDeleteAction;
import com.onyx.kcb.action.LibraryMoveToAction;
import com.onyx.kcb.action.RxFileSystemScanAction;
import com.onyx.kcb.action.RxMetadataLoadAction;
import com.onyx.kcb.action.ExtractMetadataAction;
import com.onyx.kcb.adapter.ModelAdapter;
import com.onyx.kcb.databinding.ActivityLibraryBinding;
import com.onyx.kcb.event.HideAllDialogEvent;
import com.onyx.kcb.event.LoadingDialogEvent;
import com.onyx.kcb.event.SearchBookEvent;
import com.onyx.kcb.holder.DataBundle;
import com.onyx.kcb.model.PageIndicatorModel;
import com.onyx.kcb.utils.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

/**
 * Created by hehai on 17-11-10.
 */

public class LibraryActivity extends OnyxAppCompatActivity {
    private ActivityLibraryBinding dataBinding;
    private DataBundle dataBundle;
    private QueryPagination pagination;
    private PageIndicatorModel pageIndicatorModel;
    private int row = KCBApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_row);
    private int col = KCBApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_col);
    private boolean longClickMode = false;
    private ModelAdapter modelAdapter;
    private DataModel currentChosenModel;
    private int displayMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private void initData() {
        if (!KCBApplication.getInstance().isMetadataScanned()) {
            processFileSystemScan();
            return;
        }
        displayMode = getDisplayMode();
        loadData();
    }

    private int getDisplayMode() {
        return PreferenceManager.getIntValue(getBaseContext(), R.string.library_display_mode_key, Constant.LibraryDisplayMode.NORMAL_MODE);
    }

    private void loadData() {
        loadData(libraryBuildQueryArgs());
    }

    private QueryArgs libraryBuildQueryArgs() {
        QueryArgs args = dataBundle.getLibraryViewDataModel().libraryQuery();
        QueryBuilder.andWith(args.conditionGroup, null);
        return args;
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
        SinglePageRecyclerView contentPageView = dataBinding.contentPageView;
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
    }

    private int getTotalCount() {
        return dataBundle.getLibraryViewDataModel().count.get();
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

    private void processFileSystemScan() {
        final DialogLoading dialogLoading = new DialogLoading(this, R.string.loading, false);
        dialogLoading.show();
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(new RxFileSystemScanAction(RxFileSystemScanAction.MMC_STORAGE_ID, true));
        String sdcardCid = EnvironmentUtil.getRemovableSDCardCid();
        if (StringUtils.isNotBlank(sdcardCid)) {
            actionChain.addAction(new RxFileSystemScanAction(sdcardCid, false));
        }
        actionChain.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onComplete() {
                super.onComplete();
                dialogLoading.dismiss();
                KCBApplication.getInstance().setMetadataScanned(true);
                loadData();
            }
        });
    }

    private void initView() {
        initDataBundle();
        initDataBinding();
        initActionBar();
        initPageRecyclerView();
        initPageIndicator();
    }

    private void initDataBinding() {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_library);
        dataBinding.setDataModel(dataBundle.getLibraryViewDataModel());
    }

    private void initDataBundle() {
        dataBundle = KCBApplication.getDataBundle();
        dataBundle.getLibraryViewDataModel().title.set(getString(R.string.library));
    }

    private void initActionBar() {
        initSupportActionBarWithCustomBackFunction();
        getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            //AppCompat would not called onOptionMenuClosed();
            // Use this listener to obtain menu visibility.
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    quitLongClickMode();
                }
            }
        });
    }

    private void quitLongClickMode() {
        longClickMode = false;
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
                pagination.setCurrentPage(0);
                loadData();
            }
        });
        dataBinding.setIndicatorModel(pageIndicatorModel);
    }

    private void initPageRecyclerView() {
        SinglePageRecyclerView contentPageView = getContentView();
        contentPageView.setLayoutManager(new DisableScrollGridManager(getApplicationContext()));
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

    private void nextPage() {
        if (!pagination.nextPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataBundle.getLibraryViewDataModel().nextPage(), false);
        loadAction.setLoadFromCache(true);
        loadAction.setLoadMetadata(isLoadMetadata());
        loadAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        preloadNext();
    }

    private EventBus getEventBus() {
        return dataBundle.getEventBus();
    }

    public SinglePageRecyclerView getContentView() {
        return dataBinding.contentPageView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.library_option_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isLongClickMode()) {
            prepareLongClickOptionsMenu(menu, currentChosenModel);
        } else {
            prepareNormalOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        processBackRequest();
    }

    private void processBackRequest() {
        if (isMultiSelectionMode()) {
            quitMultiSelectionMode();
            updateContentView();
            return;
        }
        if (CollectionUtils.isNullOrEmpty(dataBundle.getLibraryViewDataModel().libraryPathList)) {
            super.onBackPressed();
            return;
        }
        removeLastParentLibrary();
        loadData();
    }

    private void removeLastParentLibrary() {
        dataBundle.getLibraryViewDataModel().libraryPathList.remove(dataBundle.getLibraryViewDataModel().libraryPathList.size() - 1);
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by:
                processSortBy();
                return true;
            case R.id.menu_filter_by:
                processFilterByBy();
                return true;
            case R.id.menu_multi_select:
                getIntoMultiSelectMode();
                return true;
            case R.id.menu_build_library:
                processBuildLibrary();
                return true;
            case R.id.menu_add_to_library:
                processAddToLibrary();
                return true;
            case R.id.menu_remove_from_library:
                processRemoveFromLibrary();
                break;
            case R.id.menu_delete_library:
                processDeleteLibrary();
                break;
            case R.id.menu_library_toc_index:
                processGotoLibrary();
                break;
            case R.id.menu_scan_thumbnail:
                processExtractMetadata();
                break;
            case R.id.menu_display_mode:
                processDisplayMode();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processDisplayMode() {
        DisplayModeAction displayModeAction = new DisplayModeAction(this);
        displayModeAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                displayMode = getDisplayMode();
                pagination.setCurrentPage(0);
                loadData();
            }
        });
    }

    private void processExtractMetadata() {
        final DialogLoading dialogLoading = new DialogLoading(this, R.string.loading, false);
        dialogLoading.show();
        final QueryArgs queryArgs = dataBundle.getLibraryViewDataModel().gotoPage(pagination.getCurrentPage());
        ExtractMetadataAction action = new ExtractMetadataAction(queryArgs, false);
        action.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData(queryArgs);
                dialogLoading.dismiss();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                dialogLoading.dismiss();
            }
        });

        ExtractMetadataAction allScanAction = new ExtractMetadataAction(QueryBuilder.allBooksQuery(queryArgs.sortBy, queryArgs.order), false);
        allScanAction.execute(dataBundle, null);
    }

    private void processGotoLibrary() {
        final LibrarySelectionAction choiceAction = new LibrarySelectionAction(getFragmentManager(), getString(R.string.menu_library_toc_index));
        choiceAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                final DataModel gotoLibrary = choiceAction.getLibrarySelected();
                choiceAction.gotoLibrary(dataBundle, gotoLibrary, new RxCallback() {
                    @Override
                    public void onNext(Object o) {
                        dataBundle.getLibraryViewDataModel().libraryPathList.clear();
                        dataBundle.getLibraryViewDataModel().libraryPathList.addAll(choiceAction.getParentPathList());
                        loadData(dataBundle.getLibraryViewDataModel().libraryQuery(gotoLibrary.idString.get()));
                    }
                });
            }
        });
    }

    private void processDeleteLibrary() {
        Library library = new Library();
        library.setId(currentChosenModel.id.get());
        library.setIdString(currentChosenModel.idString.get());
        library.setParentUniqueId(currentChosenModel.parentId.get());
        new LibraryDeleteAction(this, library).execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });
    }

    private void processRemoveFromLibrary() {
        List<DataModel> listSelected = dataBundle.getLibraryViewDataModel().getListSelected();
        DataModel library = dataBundle.getLibraryViewDataModel().libraryPathList.get(dataBundle.getLibraryViewDataModel().libraryPathList.size() - 1);
        LibraryRemoveFromAction removeFromAction = new LibraryRemoveFromAction(listSelected, library);
        removeFromAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                quitLongClickMode();
                quitMultiSelectionMode();
                loadData(libraryBuildQueryArgs(), false);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                showToast(R.string.remove_metadata_failed, Toast.LENGTH_SHORT);
            }
        });
    }

    private void processAddToLibrary() {
        List<DataModel> listSelected = dataBundle.getLibraryViewDataModel().getListSelected();
        LibraryMoveToAction libraryMoveToAction = new LibraryMoveToAction(this, listSelected);
        libraryMoveToAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                quitLongClickMode();
                quitMultiSelectionMode();
                loadData(libraryBuildQueryArgs(), false);
            }
        });
    }

    private void processBuildLibrary() {
        LibraryBuildAction libraryBuildAction = new LibraryBuildAction(this, dataBundle.getLibraryViewDataModel().getLibraryIdString());
        libraryBuildAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                quitLongClickMode();
                loadData(libraryBuildQueryArgs(), false);
            }
        });
    }

    private void getIntoMultiSelectMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        dataBundle.getLibraryViewDataModel().clearItemSelectedList();
        updateContentView();
    }

    private void processFilterByBy() {
        ConfigFilterAction filterAction = new ConfigFilterAction(this);
        filterAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onComplete() {
                loadData(dataBundle.getLibraryViewDataModel().getCurrentQueryArgs());
            }
        });
    }

    private void processSortBy() {
        ConfigSortAction sortAction = new ConfigSortAction(this);
        sortAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onComplete() {
                loadData(dataBundle.getLibraryViewDataModel().getCurrentQueryArgs());
            }
        });
    }

    @Subscribe
    public void onItemLongClickEvent(ItemLongClickEvent event) {
        currentChosenModel = event.getDataModel();
        longClickMode = true;
        dataBundle.getLibraryViewDataModel().addItemSelected(currentChosenModel, true);
        actionBar.openOptionsMenu();
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
    public void onSearchBookEvent(SearchBookEvent event) {
        pagination.setCurrentPage(0);
        loadData(event.getQueryArgs());
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
    }

    private void addLibraryToParentRefList(DataModel model) {
        dataBundle.getLibraryViewDataModel().libraryPathList.add(model);
    }

    private void processMultiModeItemClick(DataModel dataModel) {
        if (dataModel.type.get() == ModelType.TYPE_LIBRARY) {
            return;
        }
        dataModel.checked.set(!dataModel.checked.get());
        if (dataModel.checked.get()) {
            dataBundle.getLibraryViewDataModel().addItemSelected(dataModel, false);
        } else {
            dataBundle.getLibraryViewDataModel().removeFromSelected(dataModel);
        }
        updateContentView();
    }

    private void prepareLongClickOptionsMenu(Menu menu, DataModel dataModel) {
        boolean isLibraryItem = isLibraryItem(dataModel);
        for (int i = 0; i < menu.size(); i++) {
            switch (menu.getItem(i).getItemId()) {
                case R.id.menu_properties:
                    menu.getItem(i).setVisible(true);
                    break;
                case R.id.menu_remove_from_library:
                    if (isLibraryItem) {
                        menu.getItem(i).setVisible(false);
                    } else {
                        menu.getItem(i).setVisible(!CollectionUtils.isNullOrEmpty(
                                dataBundle.getLibraryViewDataModel().libraryPathList));
                    }
                    break;
                case R.id.menu_delete_library:
                    menu.getItem(i).setVisible(isLibraryItem);
                    break;
                default:
                    menu.getItem(i).setVisible(!isLibraryItem);
                    break;
            }
        }
    }

    private void prepareNormalOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            switch (menu.getItem(i).getItemId()) {
                case R.id.menu_remove_from_library:
                    if (CollectionUtils.isNullOrEmpty(dataBundle.getLibraryViewDataModel().libraryPathList)) {
                        menu.getItem(i).setVisible(false);
                    } else {
                        menu.getItem(i).setVisible(isMultiSelectionMode());
                    }
                    break;
                case R.id.menu_add_to_library:
                    menu.getItem(i).setVisible(isMultiSelectionMode());
                    break;
                case R.id.menu_delete_library:
                case R.id.menu_properties:
                    menu.getItem(i).setVisible(false);
                    break;
                default:
                    menu.getItem(i).setVisible(true);
                    break;
            }
        }
    }

    private boolean isLibraryItem(DataModel dataModel) {
        return dataModel.type.get() == ModelType.TYPE_LIBRARY;
    }

    private void processBookItemOpen(DataModel dataModel) {
        String filePath = dataModel.absolutePath.get();
        if (StringUtils.isNullOrEmpty(filePath)) {
            showToast(R.string.file_path_null, Toast.LENGTH_SHORT);
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            showToast(R.string.file_not_exist, Toast.LENGTH_SHORT);
            return;
        }
        ActivityUtil.startActivitySafely(this,
                ViewDocumentUtils.viewActionIntentWithMimeType(file),
                ViewDocumentUtils.getReaderComponentName(this));
    }

    public boolean isLongClickMode() {
        return longClickMode;
    }

    public boolean isMultiSelectionMode() {
        return modelAdapter.isMultiSelectionMode();
    }

    private void quitMultiSelectionMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.NORMAL_MODE);
        dataBundle.getLibraryViewDataModel().clearItemSelectedList();
    }

    @Subscribe
    public void onLoadingDialogEvent(LoadingDialogEvent event) {
        showProgressDialog(event, event.getResId(), null);
    }

    @Subscribe
    public void onHideAllDialogEvent(HideAllDialogEvent event) {
        dismissAllProgressDialog();
    }

    public boolean isLoadMetadata() {
        return displayMode != Constant.LibraryDisplayMode.CHILD_LIBRARY_MODE || dataBundle.getLibraryViewDataModel().libraryPathList.size() != 0;
    }
}
