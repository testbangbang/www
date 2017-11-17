package com.onyx.kcb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.SinglePageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.kcb.KCPApplication;
import com.onyx.kcb.R;
import com.onyx.kcb.action.LibraryBuildAction;
import com.onyx.kcb.action.LibraryDeleteAction;
import com.onyx.kcb.action.RxFileSystemScanAction;
import com.onyx.kcb.action.RxMetadataLoadAction;
import com.onyx.kcb.adapter.ModelAdapter;
import com.onyx.kcb.databinding.ActivityLibraryBinding;
import com.onyx.kcb.event.ItemClickEvent;
import com.onyx.kcb.event.ItemLongClickEvent;
import com.onyx.kcb.event.MetadataItemClickEvent;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.model.DataModel;
import com.onyx.kcb.model.LibraryViewDataModel;
import com.onyx.kcb.model.ModelType;
import com.onyx.kcb.model.PageIndicatorModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

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
    private int row = KCPApplication.getInstance().getResources().getInteger(R.integer.library_row);
    private int col = KCPApplication.getInstance().getResources().getInteger(R.integer.library_col);
    private boolean longClickMode = false;
    private ModelAdapter modelAdapter;
    private DataModel currentChosenModel;
    private boolean multiSelectionMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        QueryArgs args = dataHolder.getLibraryViewInfo().libraryQuery();
        QueryBuilder.andWith(args.conditionGroup, null);
        return args;
    }

    private void loadData(QueryArgs queryArgs) {
        loadData(queryArgs, true);
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
        nextLoad();
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
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataHolder.getLibraryViewInfo().pageQueryArgs(preLoadPage), false);
        loadAction.execute(dataHolder, null);
    }

    private void processFileSystemScan() {
        final DialogLoading dialogLoading = new DialogLoading(this, R.string.loading, false);
        dialogLoading.show();
        RxFileSystemScanAction action = new RxFileSystemScanAction(dataModel.items, RxFileSystemScanAction.MMC_STORAGE_ID, true);
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
        dataModel = new LibraryViewDataModel();
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
        pagination = dataHolder.getLibraryViewInfo().getQueryPagination();
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
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataHolder.getLibraryViewInfo().prevPage(), false);
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
                dataHolder.getLibraryViewInfo().pageQueryArgs(preLoadPage), false);
        loadAction.execute(dataHolder, null);
    }

    private void nextPage() {
        if (!pagination.nextPage()) {
            return;
        }
        final RxMetadataLoadAction loadAction = new RxMetadataLoadAction(dataModel, dataHolder.getLibraryViewInfo().nextPage(), false);
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

    }

    private void processDeleteLibrary() {
        new LibraryDeleteAction(this, currentChosenModel.library.get()).execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });
    }

    private void processRemoveFromLibrary() {

    }

    private void processAddToLibrary() {

    }

    private void processBuildLibrary() {
        LibraryBuildAction libraryBuildAction = new LibraryBuildAction(this, dataHolder.getLibraryViewInfo().getLibraryIdString());
        libraryBuildAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });
    }

    private void getIntoMultiSelectMode() {

    }

    private void processFilterByBy() {

    }

    private void processSortBy() {

    }

    @Subscribe
    public void onMetadataItemClickEvent(MetadataItemClickEvent event) {
        processBookItemOpen(event.getMetadata());
    }

    @Subscribe
    public void onItemLongClickEvent(ItemLongClickEvent event) {
        currentChosenModel = event.getDataModel();
        longClickMode = true;
        dataModel.addItemSelected(currentChosenModel, true);
        actionBar.openOptionsMenu();
    }

    @Subscribe
    public void onItemClickEvent(ItemClickEvent event) {

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
        return dataModel.type.get() == ModelType.Library;
    }

    private void processBookItemOpen(Metadata metadata) {
        String filePath = metadata.getNativeAbsolutePath();
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
        return multiSelectionMode;
    }
}
