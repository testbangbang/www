package com.onyx.kcb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.event.ItemLongClickEvent;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.ModelType;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.kcb.KCPApplication;
import com.onyx.kcb.R;
import com.onyx.kcb.action.LibraryBuildAction;
import com.onyx.kcb.action.LibraryChoiceAction;
import com.onyx.kcb.action.LibraryDeleteAction;
import com.onyx.kcb.action.LibraryMoveToAction;
import com.onyx.kcb.action.RxFileSystemScanAction;
import com.onyx.kcb.action.RxMetadataLoadAction;
import com.onyx.kcb.adapter.ModelAdapter;
import com.onyx.kcb.databinding.ActivityLibraryBinding;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.model.LibraryViewDataModel;
import com.onyx.kcb.model.PageIndicatorModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

/**
 * Created by hehai on 17-11-10.
 */

public class LibraryActivity extends OnyxAppCompatActivity {
    static private boolean hasMetadataScanned = false;
    private ActivityLibraryBinding dataBinding;
    private LibraryViewDataModel dataModel;
    private LibraryDataHolder dataHolder;
    private QueryPagination pagination;
    private PageIndicatorModel pageIndicatorModel;
    private int row = KCPApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_row);
    private int col = KCPApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_col);
    private boolean longClickMode = false;
    private ModelAdapter modelAdapter;
    private DataModel currentChosenModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getEventBus().register(this);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(this);
    }

    private void initData() {
        if (!isHasMetadataScanned()) {
            processFileSystemScan();
            return;
        }
        loadData();
    }

    private void loadData() {
        loadData(libraryBuildQueryArgs());
    }

    private QueryArgs libraryBuildQueryArgs() {
        QueryArgs args = dataModel.libraryQuery();
        QueryBuilder.andWith(args.conditionGroup, null);
        return args;
    }

    private void loadData(QueryArgs queryArgs) {
        loadData(queryArgs, false);
    }

    private void loadData(QueryArgs queryArgs, boolean loadFromCache) {
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, queryArgs);
        loadAction.setLoadFromCache(loadFromCache);
        loadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
                loadAction.hideLoadingDialog();
            }
        });
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
        return dataModel.count.get();
    }

    private void nextLoad() {
        int preLoadPage = pagination.getCurrentPage() + 1;
        if (preLoadPage >= pagination.pages()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataModel.pageQueryArgs(preLoadPage), false);
        loadAction.execute(dataHolder, null);
    }

    private void processFileSystemScan() {
        final DialogLoading dialogLoading = new DialogLoading(this, R.string.loading, false);
        dialogLoading.show();
        RxFileSystemScanAction action = new RxFileSystemScanAction(RxFileSystemScanAction.MMC_STORAGE_ID, true);
        action.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                dialogLoading.dismiss();
                setHasMetadataScanned(true);
                loadData();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                dialogLoading.dismiss();
            }
        });
    }

    private void initView() {
        dataHolder = getDataHolder();
        dataModel = LibraryViewDataModel.create(getEventBus(), row, col);
        dataModel.title.set(getString(R.string.library));
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_library);
        dataBinding.setDataModel(dataModel);
        initActionBar();
        initPageRecyclerView();
        initPageIndicator();
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
        pagination = dataModel.getQueryPagination();
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
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataModel.prevPage(), false);
        loadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        prevLoad();
    }

    private void prevLoad() {
        int preLoadPage = pagination.getCurrentPage() - 1;
        if (preLoadPage < 0) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel,
                dataModel.pageQueryArgs(preLoadPage), false);
        loadAction.execute(dataHolder, null);
    }

    private void nextPage() {
        if (!pagination.nextPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataModel.nextPage(), false);
        loadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateContentView();
            }
        });
        nextLoad();
    }

    public static boolean isHasMetadataScanned() {
        return hasMetadataScanned;
    }

    public static void setHasMetadataScanned(boolean hasMetadataScanned) {
        LibraryActivity.hasMetadataScanned = hasMetadataScanned;
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(this);
        }
        return dataHolder;
    }

    private EventBus getEventBus() {
        return getDataHolder().getEventBus();
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
        if (CollectionUtils.isNullOrEmpty(dataModel.libraryPathList)) {
            super.onBackPressed();
            return;
        }
        removeLastParentLibrary();
        loadData();
    }

    private void removeLastParentLibrary() {
        dataModel.libraryPathList.remove(dataModel.libraryPathList.size() - 1);
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processGotoLibrary() {
        final LibraryChoiceAction choiceAction = new LibraryChoiceAction(getFragmentManager(), getString(R.string.menu_library_toc_index));
        choiceAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                final DataModel gotoLibrary = choiceAction.getChooseLibrary();
                choiceAction.gotoLibrary(dataHolder, gotoLibrary, new RxCallback() {
                    @Override
                    public void onNext(Object o) {
                        dataModel.libraryPathList.clear();
                        dataModel.libraryPathList.addAll(choiceAction.getParentPathList());
                        loadData(dataModel.libraryQuery(gotoLibrary.idString.get()));
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
        new LibraryDeleteAction(this, library).execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });
    }

    private void processRemoveFromLibrary() {

    }

    private void processAddToLibrary() {
        List<DataModel> listSelected = dataModel.getListSelected();
        LibraryMoveToAction libraryMoveToAction = new LibraryMoveToAction(this, listSelected);
        libraryMoveToAction.execute(getDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                quitLongClickMode();
                quitMultiSelectionMode();
                loadData();
            }
        });
    }

    private void processBuildLibrary() {
        LibraryBuildAction libraryBuildAction = new LibraryBuildAction(this, dataModel.getLibraryIdString());
        libraryBuildAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });
    }

    private void getIntoMultiSelectMode() {
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        dataModel.clearItemSelectedList();
        updateContentView();
    }

    private void processFilterByBy() {

    }

    private void processSortBy() {

    }

    @Subscribe
    public void onItemLongClickEvent(ItemLongClickEvent event) {
        currentChosenModel = event.getDataModel();
        longClickMode = true;
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

    private void processNormalModeItemClick(DataModel model) {
        if (model.type.get() == ModelType.TYPE_LIBRARY) {
            processLibraryItem(model);
        } else {
            processBookItemOpen(model);
        }
    }

    private void processLibraryItem(DataModel model) {
        addLibraryToParentRefList(model);
        loadData();
    }

    private void addLibraryToParentRefList(DataModel model) {
        dataModel.libraryPathList.add(model);
    }

    private void processMultiModeItemClick(DataModel dataModel) {
        if (dataModel.type.get() == ModelType.TYPE_LIBRARY) {
            return;
        }
        dataModel.checked.set(!dataModel.checked.get());
        if (dataModel.checked.get()) {
            this.dataModel.addItemSelected(dataModel, false);
        } else {
            this.dataModel.removeFromSelected(dataModel);
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
                                dataHolder.getLibraryViewInfo().getLibraryPathList()));
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
                    if (CollectionUtils.isNullOrEmpty(dataHolder.getLibraryViewInfo().getLibraryPathList())) {
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
        dataModel.clearItemSelectedList();
    }
}
