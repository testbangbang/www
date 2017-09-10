package com.onyx.einfo.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.data.request.data.fs.StorageFileListLoadRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.einfo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/9.
 */
public class StorageViewModel extends BaseObservable implements StorageItemNavigator {
    private static final String TAG = "StorageViewModel";

    private Context mContext;

    public final ObservableList<StorageItemViewModel> items = new ObservableArrayList<>();
    public final ObservableInt folderCount = new ObservableInt();
    public final ObservableInt fileCount = new ObservableInt();
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    public final ObservableField<String> currentFolderTitle = new ObservableField<>();
    public final ObservableField<ViewType> viewType = new ObservableField<>();

    private DataManager dataManager = new DataManager();
    private StorageNavigator storageNavigator;

    private Map<StorageItemViewModel, Boolean> mapSelected = new HashMap<>();
    private boolean multiSelectionMode = false;

    public StorageViewModel(Context context) {
        mContext = context.getApplicationContext();
        viewType.set(ViewType.Thumbnail);
        items.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<StorageItemViewModel>>() {
            @Override
            public void onChanged(ObservableList<StorageItemViewModel> sender) {
            }

            @Override
            public void onItemRangeChanged(ObservableList<StorageItemViewModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeInserted(ObservableList<StorageItemViewModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeMoved(ObservableList<StorageItemViewModel> sender, int fromPosition, int toPosition, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<StorageItemViewModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }
        });
    }

    private void updateTypeCount(ObservableList<StorageItemViewModel> sender) {
        int folderTotal = 0, fileTotal = 0;
        for (StorageItemViewModel model : sender) {
            switch (model.getFileModel().getType()) {
                case FileModel.TYPE_DIRECTORY:
                    folderTotal++;
                    break;
                case FileModel.TYPE_FILE:
                    fileTotal++;
                    break;
            }
        }
        folderCount.set(folderTotal);
        fileCount.set(fileTotal);
    }

    public void setPageStatus(int curPage, int allPage) {
        currentPage.set(curPage);
        totalPage.set(allPage);
    }

    public void setNavigator(StorageNavigator navigator) {
        this.storageNavigator = navigator;
    }

    public boolean goUp() {
        if (isTopLevelFolder()) {
            return false;
        } else {
            loadData(getParentFile());
            return true;
        }
    }

    public void start() {
        loadData(EnvironmentUtil.getStorageRootDirectory());
    }

    private void loadData(final File parentFile) {
        List<String> filterList = null;
        if (isStorageRoot(parentFile)) {
            filterList = new ArrayList<>();
            filterList.add(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
            filterList.add(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
        }
        final StorageFileListLoadRequest fileListLoadRequest = new StorageFileListLoadRequest(parentFile, filterList);
        getDataManager().submitToMulti(mContext, fileListLoadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                addToModelItemList(parentFile, fileListLoadRequest.getResultFileList());
            }
        });
    }

    private void addToModelItemList(File parentFile, List<File> fileList) {
        items.clear();
        items.add(createGoUpModel(parentFile));
        if (!CollectionUtils.isNullOrEmpty(fileList)) {
            for (File file : fileList) {
                items.add(createNormalModel(file));
            }
        }
    }

    private void openFile(File file) {
        Intent intent = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        ResolveInfo info = ViewDocumentUtils.getDefaultActivityInfo(mContext, intent,
                ViewDocumentUtils.getEduReaderComponentName().getPackageName());
        if (info == null) {
            return;
        }
        ActivityUtil.startActivitySafely(mContext, intent, info.activityInfo);
    }

    public void onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        storageNavigator = null;
    }

    public void addFolder(final String folderTitle) {
    }

    public void onViewChangeClick() {
        ViewType type = viewType.get();
        viewType.set(type == ViewType.Details ? ViewType.Thumbnail : ViewType.Details);
        if (storageNavigator != null) {
            storageNavigator.onViewChange();
        }
    }

    public ViewType getCurrentViewType() {
        return viewType.get();
    }

    @Override
    public void onClick(StorageItemViewModel itemModel) {
        if (itemModel.getFileModel().isGoUpType()) {
            if (storageNavigator != null) {
                storageNavigator.onGoUp();
            }
            getItemSelectedMap().clear();
            return;
        }
        if (isInMultiSelectionMode()) {
            boolean isSelected = !itemModel.isSelected.get();
            itemModel.setSelected(isSelected);
            getItemSelectedMap().put(itemModel, isSelected);
            itemModel.notifyChange();
            return;
        }
        File file = itemModel.getFileModel().getFile();
        if (file.isDirectory()) {
            loadData(file);
        } else if (file.isFile()) {
            openFile(file);
        }
    }

    @Override
    public void onLongClick(StorageItemViewModel file) {
    }

    private File getParentFile() {
        return getCurrentFile().getParentFile();
    }

    private File getCurrentFile() {
        return items.get(0).getFileModel().getFile();
    }

    private boolean isTopLevelFolder() {
        File file = getCurrentFile();
        return file == null || isStorageRoot(file);
    }

    private boolean isStorageRoot(File targetDirectory) {
        return EnvironmentUtil.getStorageRootDirectory().getAbsolutePath().contains(targetDirectory.getAbsolutePath());
    }

    private StorageItemViewModel createGoUpModel(File file) {
        StorageItemViewModel model = new StorageItemViewModel(mContext, this);
        model.setFileModel(FileModel.createGoUpModel(file, mContext.getString(R.string.storage_go_up)));
        model.setEnableSelection(false);
        return model;
    }

    private StorageItemViewModel createNormalModel(File file) {
        StorageItemViewModel model = new StorageItemViewModel(mContext, this);
        model.setFileModel(FileModel.create(file, null));
        model.setEnableSelection(isInMultiSelectionMode());
        return model;
    }

    public boolean isInMultiSelectionMode() {
        return multiSelectionMode;
    }

    public void setMultiSelection(boolean beIn) {
        multiSelectionMode = beIn;
        if (!beIn) {
            mapSelected.clear();
        }
    }

    private DataManager getDataManager() {
        return dataManager;
    }

    public Map<StorageItemViewModel, Boolean> getItemSelectedMap() {
        return mapSelected;
    }

    public boolean isItemSelected(StorageItemViewModel key) {
        Boolean selected = getItemSelectedMap().get(key);
        if (selected == null) {
            return false;
        }
        return selected;
    }
}
