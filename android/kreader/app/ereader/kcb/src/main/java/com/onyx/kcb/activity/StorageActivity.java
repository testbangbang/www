package com.onyx.kcb.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.FileOperateMode;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GPaginator;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.data.event.ItemClickEvent;
import com.onyx.android.sdk.data.event.ItemLongClickEvent;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.FileModel;
import com.onyx.android.sdk.data.model.common.AppPreference;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.kcb.KCPApplication;
import com.onyx.kcb.R;
import com.onyx.kcb.action.FileCopyAction;
import com.onyx.kcb.action.FileDeleteAction;
import com.onyx.kcb.action.FileOpenWithAction;
import com.onyx.kcb.action.SortByProcessAction;
import com.onyx.kcb.action.StorageDataLoadAction;
import com.onyx.kcb.adapter.ModelAdapter;
import com.onyx.kcb.databinding.ActivityStorageBinding;
import com.onyx.kcb.device.DeviceConfig;
import com.onyx.kcb.dialog.DialogCreateNewFolder;
import com.onyx.kcb.dialog.DialogFileProperty;
import com.onyx.kcb.dialog.DialogRenameFile;
import com.onyx.kcb.event.OperationEvent;
import com.onyx.kcb.event.ViewTypeEvent;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.manager.ConfigPreferenceManager;
import com.onyx.kcb.model.OperationItem;
import com.onyx.kcb.model.StorageViewModel;
import com.onyx.kcb.utils.Constant;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/11/21.
 */

public class StorageActivity extends OnyxAppCompatActivity {
    private static final String TAG = StorageActivity.class.getSimpleName();

    private StorageViewModel storageViewModel;
    private LibraryDataHolder dataHolder;
    private ActivityStorageBinding binding;

    private FileOperateMode fileOperateMode = FileOperateMode.ReadOnly;
    private int viewTypeThumbnailRow = KCPApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_row);
    private int viewTypeThumbnailCol = KCPApplication.getInstance().getResources().getInteger(R.integer.library_view_type_thumbnail_col);
    private int viewTypeDetailsRow = KCPApplication.getInstance().getResources().getInteger(R.integer.library_view_type_details_row);
    private int viewTypeDetailsCol = KCPApplication.getInstance().getResources().getInteger(R.integer.library_view_type_details_col);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        getEventBus().register(this);
        loadRootDirData();
    }

    private void initView() {
        prevBinding();
        initToolbar();
        initRecyclerView();
        initSortByView();
    }

    private void prevBinding() {
        storageViewModel = new StorageViewModel(getEventBus());
        storageViewModel.viewType.set(ConfigPreferenceManager.getStorageViewType(this));
        prevAddOperationItems();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_storage);
        binding.setViewModel(storageViewModel);
    }

    private String getOperationText(int operation) {
        switch (operation) {
            case OperationEvent.OPERATION_COPY:
                return getString(android.R.string.copy);
            case OperationEvent.OPERATION_PASTE:
                return getString(R.string.paste);
            case OperationEvent.OPERATION_DELETE:
                return getString(R.string.delete);
            case OperationEvent.OPERATION_CANCEL:
                return getString(R.string.cancel);
            case OperationEvent.OPERATION_CUT:
                return getString(R.string.cut);
        }
        return null;
    }

    private void prevAddOperationItems() {
        List<OperationItem> itemList = new ArrayList<>();
        for (OperationEvent event : OperationEvent.createAll()) {
            itemList.add(new OperationItem(getEventBus(), event).setText(getOperationText(event.getOperation())));
        }
        getStorageViewModel().setOperationItemArray(itemList);
    }

    private void initToolbar() {
        initSupportActionBarWithCustomBackFunction();
        actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    if (getStorageViewModel().getSelectionMode() == SelectionMode.LONG_PRESS_MODE) {
                        getStorageViewModel().setSelectionMode(SelectionMode.NORMAL_MODE);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        PageRecyclerView contentPageView = getContentView();
        contentPageView.setHasFixedSize(true);
        contentPageView.setLayoutManager(new DisableScrollGridManager(getApplicationContext()));
        ModelAdapter modelAdapter = new ModelAdapter();
        modelAdapter.setRowAndCol(viewTypeThumbnailRow, viewTypeThumbnailCol);
        contentPageView.setAdapter(modelAdapter);
        contentPageView.setOnPagingListener(new PageRecyclerView.OnPagingListener() {
            @Override
            public void onPageChange(int position, int itemCount, int pageSize) {
                updatePageStatus(false);
            }
        });
        gotoPage(0);
    }

    private void initSortByView() {
        binding.buttonSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortByDialog();
            }
        });
    }

    private void saveSortConfig(SortBy sortBy, SortOrder sortOrder) {
        ConfigPreferenceManager.setStorageSortBy(getApplicationContext(), sortBy);
        ConfigPreferenceManager.setStorageSortOrder(getApplicationContext(), sortOrder);
    }

    private void showSortByDialog() {
        final SortByProcessAction sortByAction = new SortByProcessAction(this,
                ConfigPreferenceManager.getStorageSortBy(this),
                ConfigPreferenceManager.getStorageSortOrder(this));
        sortByAction.execute(getDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                saveSortConfig(sortByAction.getResultSortBy(), sortByAction.getResultSortOrder());
                loadData(getStorageViewModel().getCurrentFile());
            }
        });
    }

    private void updatePageStatus(boolean resetPage) {
        PageRecyclerView contentPageView = getContentView();
        if (contentPageView == null || contentPageView.getPaginator() == null) {
            return;
        }
        GPaginator paginator = contentPageView.getPaginator();
        paginator.resize(getRowCountBasedViewType(), getColCountBasedViewType(), contentPageView.getAdapter().getItemCount());
        if (resetPage) {
            gotoPage(0);
        }
        getStorageViewModel().setPageStatus(paginator.getVisibleCurrentPage(), paginator.pages());
    }

    private void gotoPage(int page) {
        PageRecyclerView contentPageView = getContentView();
        contentPageView.gotoPage(page);
    }

    private void updateContentView() {
        PageRecyclerView contentPageView = getContentView();
        if (contentPageView == null) {
            return;
        }
        contentPageView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (getStorageViewModel().isInMultiSelectionMode()) {
            switchToNormalMode();
        }
        else {
            if (getStorageViewModel().canGoUp()) {
                loadData(getStorageViewModel().getParentFile());
                return;
            }
        }
        super.onBackPressed();
    }

    private void getIntoMultiSelectionMode() {
        StorageViewModel viewModel = getStorageViewModel();
        viewModel.setSelectionMode(SelectionMode.MULTISELECT_MODE);
        viewModel.clearItemSelectedMap();
        viewModel.switchOperationPanel(false, OperationEvent.OPERATION_PASTE);
        viewModel.setShowOperationFunc(true);
        ModelAdapter modelAdapter = (ModelAdapter) getContentView().getAdapter();
        modelAdapter.setMultiSelectionMode(SelectionMode.MULTISELECT_MODE);
        updateContentView();
    }

    private void quitMultiSelectionMode(boolean showOperationPanel, int nextMode) {
        StorageViewModel viewModel = getStorageViewModel();
        viewModel.setSelectionMode(nextMode);
        if (!showOperationPanel) {
            viewModel.clearItemSelectedMap();
        }
        viewModel.setShowOperationFunc(showOperationPanel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.storage_option_menu, menu);
        return true;
    }

    private void prepareRootOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            int id = item.getItemId();
            if ((id == R.id.menu_properties && getStorageViewModel().getItemSelectedFileList().size() > 0) ||
                    id == R.id.menu_search || id == R.id.menu_select_multiple || id == R.id.menu_delete) {
                item.setVisible(true);
                if (id == R.id.menu_delete) {
                    item.setVisible(!getDeviceConfig().isContentReadOnly());
                }
            }
            else {
                item.setVisible(false);
            }
        }
    }

    private boolean prepareReadOnlyItem(MenuItem item) {
        int id = item.getItemId();
        if (((id == R.id.menu_copy) ||
                (id == R.id.menu_cut) ||
                (id == R.id.menu_delete) ||
                (id == R.id.menu_new_folder))) {
            item.setVisible(!getDeviceConfig().isContentReadOnly());
            return true;
        }
        return false;
    }

    private boolean isInStorageRoot() {
        return getStorageViewModel().isStorageRoot(getStorageViewModel().getCurrentFile());
    }

    private void prepareLongPressOptionsMenu(Menu menu) {
        if (isInStorageRoot()) {
            prepareRootOptionsMenu(menu);
            return;
        }
        FileModel model = getCurrentSelectedItem().getFileModel();
        if (model.isGoUpType()) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                switch (item.getItemId()) {
                    case R.id.menu_new_folder:
                        if (getDeviceConfig().isContentReadOnly()) {
                            item.setVisible(false);
                        }
                        else {
                            item.setVisible(true);
                        }
                        break;
                    default:
                        item.setVisible(false);
                }
            }
        }
        else if (model.isFileType()) {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                if (!prepareReadOnlyItem(item)) {
                    if (item.getItemId() == R.id.menu_new_shortcut) {
                        item.setVisible(false);
                    }
                    else {
                        item.setVisible(true);
                    }
                }
                updateScreenSaverMenuItem(item, model.getFile());
            }
        }
        else {
            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                int id = item.getItemId();
                if (id == R.id.menu_open_with) {
                    item.setVisible(false);
                }
                else if (prepareReadOnlyItem(item)) {
                }
                else if (id == R.id.menu_new_shortcut) {
                    item.setVisible(!isInStorageRoot());
                }
                updateScreenSaverMenuItem(item, model.getFile());
            }
        }
    }

    private void updateScreenSaverMenuItem(MenuItem item, final File file) {
        if (item.getItemId() == R.id.menu_screen_saver) {
            if (getDeviceConfig().supportScreenSaver()) {
                item.setEnabled(isScreenSaverFileSelected(file));
            }
            else {
                item.setVisible(false);
            }
        }
    }

    private boolean isScreenSaverFileSelected(final File file) {
        return file != null && MimeTypeUtils.isScreenSaverFile(FilenameUtils.getExtension(file.getName()));
    }

    private void prepareNormalOptionsMenu(Menu menu) {
        if (isInStorageRoot()) {
            prepareRootOptionsMenu(menu);
            return;
        }
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            switch (item.getItemId()) {
                case R.id.menu_search:
                case R.id.menu_select_multiple:
                    item.setVisible(true);
                    break;
                case R.id.menu_new_folder:
                    item.setVisible(!getDeviceConfig().isContentReadOnly());
                    break;
                default:
                    item.setVisible(false);
                    break;
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        switch (getStorageViewModel().getSelectionMode()) {
            case SelectionMode.LONG_PRESS_MODE:
                prepareLongPressOptionsMenu(menu);
                break;
            case SelectionMode.NORMAL_MODE:
                prepareNormalOptionsMenu(menu);
                break;
            default:
                for (int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setVisible(false);
                }
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_open_with:
                onFileOpenWith();
                break;
            case R.id.menu_properties:
                onShowFileProperty(getCurrentSelectedItem().getFileModel().getFile());
                return true;
            case R.id.menu_search:
                onSearchRequested();
                return true;
            case R.id.menu_select_multiple:
                getIntoMultiSelectionMode();
                return true;
            case R.id.menu_rename:
                onFileRename(getCurrentSelectedItem());
                return true;
            case R.id.menu_cut:
                fileOperateMode = FileOperateMode.Cut;
                switchToPasteMode();
                return true;
            case R.id.menu_copy:
                fileOperateMode = FileOperateMode.Copy;
                switchToPasteMode();
                return true;
            case R.id.menu_delete:
                showDeleteActionInform();
                return true;
            case R.id.menu_new_folder:
                onShowNewFolderCreate();
                return true;
            case R.id.menu_new_shortcut:
                setDirectoryToShortcut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private DataModel getCurrentSelectedItem() {
        return getStorageViewModel().getItemSelectedItemModelList().get(0);
    }

    private File getCurrentSelectedFile() {
        return getCurrentSelectedItem().getFileModel().getFile();
    }

    private void showDeleteActionInform() {
        if (getStorageViewModel().getItemSelectedMap().isEmpty()) {
            return;
        }
        String confirmMessage;
        List<File> targetItemsList = getStorageViewModel().getItemSelectedFileList();
        if (targetItemsList.size() == 1) {
            confirmMessage = targetItemsList.get(0).getName();
        }
        else {
            confirmMessage = targetItemsList.size() + getResources().getString(R.string.items);
        }
        if (isInStorageRoot()) {
            processShortCutDelete(targetItemsList);
        }
        else {
            showDeleteDialog(confirmMessage, targetItemsList);
        }
    }

    private void onFileRename(final DataModel model) {
        final DialogRenameFile dlgRename = new DialogRenameFile();
        final File originFile = model.getFileModel().getFile();
        Bundle args = new Bundle();
        args.putString(DialogRenameFile.ARGS_FILE_PATH, originFile.getAbsolutePath());
        dlgRename.setArguments(args);
        dlgRename.setOnRenameListener(new DialogRenameFile.OnRenameFinishedListener() {
            @Override
            public void onRenameFinish(String newFullPath, String newName) {
                model.setFileModel(FileModel.create(new File(newFullPath), model.getFileModel().getThumbnail()));
            }
        });
        dlgRename.show(getFragmentManager());
    }

    @Override
    public boolean onSearchRequested() {
        Bundle bundle = new Bundle();
        bundle.putString(GAdapterUtil.FILE_PATH, getStorageViewModel().getCurrentFile().getAbsolutePath());
        startSearch(null, false, bundle, false);
        return true;
    }

    private void onFileOpenWith() {
        final File file = getCurrentSelectedItem().getFileModel().getFile();
        final FileOpenWithAction openWithAction = new FileOpenWithAction(this, file);
        openWithAction.execute(getDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                List<AppDataInfo> list = openWithAction.getAppDataInfoList();
                if (CollectionUtils.isNullOrEmpty(list)) {
                    openWithAction.showFileOpenWithDialog(StorageActivity.this, getDataHolder());
                    return;
                }
                openWithAction.showAppListWithDialog(StorageActivity.this, file, list);
            }
        });
    }

    private void onShowFileProperty(File file) {
        if (file == null) {
            return;
        }
        DialogFileProperty dlgProperty = new DialogFileProperty();
        Bundle args = new Bundle();
        args.putString(DialogFileProperty.ARGS_FILE_PATH, file.getAbsolutePath());
        dlgProperty.setArguments(args);
        dlgProperty.show(getFragmentManager());
    }

    private void onShowNewFolderCreate() {
        DialogCreateNewFolder dlgCreateDirectory = new DialogCreateNewFolder();
        Bundle args = new Bundle();
        args.putString(DialogCreateNewFolder.ARGS_PARENT_DIR,
                getStorageViewModel().getCurrentFile().getAbsolutePath());
        dlgCreateDirectory.setArguments(args);
        dlgCreateDirectory.setOnCreatedListener(new DialogCreateNewFolder.OnCreateListener() {
            @Override
            public void onCreated(File file) {
                reloadData();
            }
        });
        dlgCreateDirectory.show(getFragmentManager());
    }

    private boolean containsShortcut(List<String> shortcutList, String newPath) {
        for (String path : shortcutList) {
            if (path.equals(newPath)) {
                return true;
            }
        }
        return false;
    }

    private void setDirectoryToShortcut() {
        List<String> shortcutList = StorageDataLoadAction.loadShortcutList(getApplicationContext());
        if (CollectionUtils.getSize(shortcutList) + 3 >=
                getRowCount(ViewType.Thumbnail) * getColCount(ViewType.Thumbnail)) {
            ToastUtils.showToast(getApplicationContext(), R.string.shortcut_link_full);
            return;
        }
        File selectedFile = getCurrentSelectedFile();
        if (containsShortcut(shortcutList, selectedFile.getAbsolutePath())) {
            ToastUtils.showToast(getApplicationContext(), R.string.shortcut_link_exist);
            return;
        }
        shortcutList.add(selectedFile.getAbsolutePath());
        boolean success = StorageDataLoadAction.saveShortcutList(getApplicationContext(), shortcutList);
        ToastUtils.showToast(getApplicationContext(), success ? R.string.succeedSetting : R.string.failSetting);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLevelGoUpEvent() {
        onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewTypeEvent(ViewTypeEvent event) {
        ConfigPreferenceManager.setStorageViewType(getApplicationContext(), event.viewType);
        PageRecyclerView contentView = getContentView();
        contentView.setAdapter(contentView.getAdapter());
        updatePageStatus(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemClickEvent(ItemClickEvent event) {
        DataModel itemModel = event.getModel();
        if (itemModel.getFileModel().isGoUpType()) {
            onLevelGoUpEvent();
            return;
        }
        if (getStorageViewModel().isInMultiSelectionMode()) {
            getStorageViewModel().toggleItemModelSelection(itemModel);
            return;
        }
        processFileClick(itemModel.getFileModel().getFile());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemLongClickEvent(ItemLongClickEvent event) {
        switch (getStorageViewModel().getSelectionMode()) {
            case SelectionMode.NORMAL_MODE:
                getStorageViewModel().setSelectionMode(SelectionMode.LONG_PRESS_MODE);
                getStorageViewModel().addItemSelected(event.getDataModel(), true);
                actionBar.openOptionsMenu();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onOperationEvent(OperationEvent event) {
        switch (event.getOperation()) {
            case OperationEvent.OPERATION_COPY:
                processFileCopyOrCut(FileOperateMode.Copy);
                break;
            case OperationEvent.OPERATION_CUT:
                processFileCopyOrCut(FileOperateMode.Cut);
                break;
            case OperationEvent.OPERATION_PASTE:
                processFilePaste();
                break;
            case OperationEvent.OPERATION_DELETE:
                showDeleteActionInform();
                break;
            case OperationEvent.OPERATION_CANCEL:
                switchToNormalMode();
                break;
        }
    }

    private void processFileCopyOrCut(FileOperateMode mode) {
        if (CollectionUtils.isNullOrEmpty(getStorageViewModel().getItemSelectedMap())) {
            ToastUtils.showToast(getApplicationContext(), R.string.no_item_select);
            return;
        }
        setFileOperateMode(mode);
        getStorageViewModel().switchOperationPanel(true, OperationEvent.OPERATION_PASTE, OperationEvent.OPERATION_CANCEL);
        quitMultiSelectionMode(true, SelectionMode.PASTE_MODE);
        updateContentView();
    }

    private void processFilePaste() {
        final FileCopyAction copyAction = new FileCopyAction(this, getStorageViewModel().getItemSelectedFileList(),
                getStorageViewModel().getCurrentFile(), isFileOperationCut());
        copyAction.execute(getDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                switchToNormalMode();
                reloadData();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                copyAction.processFileException(StorageActivity.this, dataHolder, (Exception) throwable);
            }
        });
    }

    private void showDeleteDialog(String confirmMessage, final List<File> removeItemsList) {
        final OnyxAlertDialog dialog = new OnyxAlertDialog();
        dialog.setParams(new OnyxAlertDialog.Params().setAlertMsgString(confirmMessage)
                .setTittleString(getString(R.string.delete))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        processFileDelete(removeItemsList);
                        dialog.dismiss();
                    }
                })
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getStorageViewModel().clearItemSelectedMap();
                        dialog.dismiss();
                    }
                }));
        dialog.show(getFragmentManager(), Constant.DIALOG_TAG_DELETE);
    }

    private void processShortCutDelete(List<File> removeItemsList) {
        if (CollectionUtils.isNullOrEmpty(getStorageViewModel().getItemSelectedMap())) {
            ToastUtils.showToast(getApplicationContext(), R.string.no_item_select);
            return;
        }
        List<String> pathList = StorageDataLoadAction.loadShortcutList(getApplicationContext());
        for (File file : removeItemsList) {
            for (String path : pathList) {
                if (file.getAbsolutePath().equals(path)) {
                    pathList.remove(path);
                    break;
                }
            }
        }
        StorageDataLoadAction.saveShortcutList(this, pathList);
        switchToNormalMode();
        reloadData();
    }

    private void processFileDelete(List<File> removeItemsList) {
        if (CollectionUtils.isNullOrEmpty(removeItemsList)) {
            ToastUtils.showToast(getApplicationContext(), R.string.no_item_select);
            return;
        }
        final FileDeleteAction deleteAction = new FileDeleteAction(this, removeItemsList);
        deleteAction.execute(getDataHolder(), new RxCallback() {

            @Override
            public void onNext(Object o) {
                switchToNormalMode();
                reloadData();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                deleteAction.processFileException(StorageActivity.this, dataHolder, (Exception) throwable);
            }
        });
    }

    private void processFileClick(File file) {
        if (file.isDirectory()) {
            loadData(file);
        }
        else if (file.isFile()) {
            openFile(file);
        }
    }

    private boolean isFileOperationCut() {
        return fileOperateMode == FileOperateMode.Cut;
    }

    private void setFileOperateMode(FileOperateMode mode) {
        fileOperateMode = mode;
    }

    private void switchToNormalMode() {
        setFileOperateMode(FileOperateMode.ReadOnly);
        quitMultiSelectionMode(false, SelectionMode.NORMAL_MODE);
        updateContentView();
    }

    private void switchToPasteMode() {
        StorageViewModel viewModel = getStorageViewModel();
        viewModel.setSelectionMode(SelectionMode.PASTE_MODE);
        viewModel.switchOperationPanel(true, OperationEvent.OPERATION_PASTE, OperationEvent.OPERATION_CANCEL);
        viewModel.setShowOperationFunc(true);
        updateContentView();
    }

    private void loadRootDirData() {
        loadData(EnvironmentUtil.getStorageRootDirectory());
    }

    private void reloadData() {
        loadData(getStorageViewModel().getCurrentFile());
    }

    private void loadData(File dir) {
        StorageDataLoadAction dataLoadAction = new StorageDataLoadAction(KCPApplication.getInstance(),dir, getStorageViewModel().items);
        dataLoadAction.setSort(ConfigPreferenceManager.getStorageSortBy(getApplicationContext()),
                ConfigPreferenceManager.getStorageSortOrder(getApplicationContext()));
        dataLoadAction.execute(getDataHolder(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                getStorageViewModel().updateCurrentTitleName(getString(R.string.storage));
                notifyContentChanged();
            }
        });
    }

    private void openFile(File file) {
        Intent intent = MetadataUtils.putIntentExtraDataMetadata(
                ViewDocumentUtils.viewActionIntentWithMimeType(file), null);
        AppPreference app = AppPreference.getFileAppPreferMap().get(FilenameUtils.getExtension(file.getName()));
        ComponentName componentName;
        if (app != null) {
            componentName = new ComponentName(app.packageName, app.className);
        }
        else {
            componentName = ViewDocumentUtils.getEduReaderComponentName();
        }
        ResolveInfo info = ViewDocumentUtils.getDefaultActivityInfo(this, intent,
                componentName.getPackageName());
        if (info == null) {
            return;
        }
        ActivityUtil.startActivitySafely(this, intent, info.activityInfo);
    }

    private EventBus getEventBus() {
        return getDataHolder().getEventBus();
    }

    private LibraryDataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new LibraryDataHolder(getApplicationContext());
        }
        return dataHolder;
    }

    private DeviceConfig getDeviceConfig() {
        return DeviceConfig.sharedInstance(getApplicationContext());
    }

    private StorageViewModel getStorageViewModel() {
        return storageViewModel;
    }

    private ViewType getViewType() {
        return getStorageViewModel().getCurrentViewType();
    }

    private PageRecyclerView getContentView() {
        return binding.contentPageView;
    }

    private int getRowCountBasedViewType() {
        return getRowCount(getViewType());
    }

    private int getColCountBasedViewType() {
        return getColCount(getViewType());
    }

    private int getRowCount(ViewType viewType) {
        return viewType == ViewType.Thumbnail ? viewTypeThumbnailRow : viewTypeDetailsRow;
    }

    private int getColCount(ViewType viewType) {
        return viewType == ViewType.Thumbnail ? viewTypeThumbnailCol : viewTypeDetailsCol;
    }

    private void notifyContentChanged() {
        updateContentView();
        updatePageStatus(true);
    }
}