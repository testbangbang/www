package com.onyx.kcb.action;

import android.content.Context;
import android.databinding.ObservableList;

import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.provider.SystemConfigProvider;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxStorageFileListLoadRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.model.FileModel;
import com.onyx.kcb.model.StorageItemViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/11/21.
 */
public class StorageDataLoadAction extends BaseAction<LibraryDataHolder> {

    private final ObservableList<StorageItemViewModel> resultDataItemList;
    private final File parentFile;
    private SortBy sortBy;
    private SortOrder sortOrder;

    public StorageDataLoadAction(final File parentFile, final ObservableList<StorageItemViewModel> resultDataItemList) {
        this.parentFile = parentFile;
        this.resultDataItemList = resultDataItemList;
    }

    public void setSort(SortBy sortBy, SortOrder sortOrder) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, RxCallback rxCallback) {
        loadData(dataHolder, parentFile, rxCallback);
    }

    private void loadData(final LibraryDataHolder dataHolder, final File parentFile, final RxCallback rxCallback) {
        List<String> filterList = new ArrayList<>();
        final RxStorageFileListLoadRequest fileListLoadRequest = new RxStorageFileListLoadRequest(dataHolder.getDataManager(), parentFile, filterList);
        if (isStorageRoot(parentFile)) {
            filterList.add(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
            filterList.add(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
        }
        else {
            fileListLoadRequest.setSort(sortBy, sortOrder);
        }
        fileListLoadRequest.execute(new RxCallback<RxStorageFileListLoadRequest>() {
            @Override
            public void onNext(RxStorageFileListLoadRequest request) {
                addToModelItemList(dataHolder, parentFile, fileListLoadRequest.getResultFileList());
                addShortcutModelItemList(dataHolder);
                rxCallback.onNext(request);
            }
        });
    }

    public static List<StorageItemViewModel> loadShortcutModelList(LibraryDataHolder dataHolder) {
        List<StorageItemViewModel> list = new ArrayList<>();
        List<String> dirPathList = loadShortcutList(dataHolder.getContext());
        if (!CollectionUtils.isNullOrEmpty(dirPathList)) {
            for (String path : dirPathList) {
                list.add(createShortcutModel(dataHolder, new File(path)));
            }
        }
        return list;
    }

    public static List<String> loadShortcutList(Context context) {
        String json = SystemConfigProvider.getStringValue(context, SystemConfigProvider.KEY_STORAGE_FOLDER_SHORTCUT_LIST);
        List<String> dirPathList = JSONObjectParseUtils.parseObject(json, new TypeReference<List<String>>() {
        });
        if (dirPathList == null) {
            dirPathList = new ArrayList<>();
        }
        return dirPathList;
    }

    public static boolean saveShortcutList(Context context, List<String> list) {
        return SystemConfigProvider.setStringValue(context, SystemConfigProvider.KEY_STORAGE_FOLDER_SHORTCUT_LIST,
                JSONObjectParseUtils.toJson(list));
    }

    private void addToModelItemList(LibraryDataHolder dataHolder, File parentFile, List<File> fileList) {
        resultDataItemList.clear();
        resultDataItemList.add(createGoUpModel(dataHolder, parentFile));
        if (!CollectionUtils.isNullOrEmpty(fileList)) {
            for (File file : fileList) {
                resultDataItemList.add(createNormalModel(dataHolder, file));
            }
        }
    }

    private void addShortcutModelItemList(LibraryDataHolder dataHolder) {
        if (isStorageRoot(parentFile)) {
            List<StorageItemViewModel> list = loadShortcutModelList(dataHolder);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                resultDataItemList.addAll(list);
            }
        }
    }

    public static StorageItemViewModel createGoUpModel(LibraryDataHolder dataHolder, File file) {
        StorageItemViewModel model = new StorageItemViewModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.createGoUpModel(file, dataHolder.getContext().getString(R.string.storage_go_up)));
        model.setEnableSelection(false);
        return model;
    }

    public static StorageItemViewModel createNormalModel(LibraryDataHolder dataHolder, File file) {
        StorageItemViewModel model = new StorageItemViewModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.create(file, null));
        return model;
    }

    public static StorageItemViewModel createShortcutModel(LibraryDataHolder dataHolder, File file) {
        StorageItemViewModel model = new StorageItemViewModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.createShortcutModel(file));
        return model;
    }

    private boolean isStorageRoot(File targetDirectory) {
        return EnvironmentUtil.getStorageRootDirectory().getAbsolutePath().contains(targetDirectory.getAbsolutePath());
    }
}