package com.onyx.einfo.action;

import android.databinding.ObservableList;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.data.fs.StorageFileListLoadRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.einfo.R;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.onyx.einfo.model.FileModel;
import com.onyx.einfo.model.StorageItemViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/9/11.
 */
public class StorageDataLoadAction extends BaseAction<LibraryDataHolder> {

    private final ObservableList<StorageItemViewModel> resultDataItemList;
    private final File parentFile;

    public StorageDataLoadAction(final File parentFile, final ObservableList<StorageItemViewModel> resultDataItemList) {
        this.parentFile = parentFile;
        this.resultDataItemList = resultDataItemList;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        loadData(dataHolder, parentFile, baseCallback);
    }

    private void loadData(final LibraryDataHolder dataHolder, final File parentFile, final BaseCallback baseCallback) {
        List<String> filterList = null;
        if (isStorageRoot(parentFile)) {
            filterList = new ArrayList<>();
            filterList.add(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
            filterList.add(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
        }
        final StorageFileListLoadRequest fileListLoadRequest = new StorageFileListLoadRequest(parentFile, filterList);
        dataHolder.getDataManager().submitToMulti(dataHolder.getContext(), fileListLoadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    addToModelItemList(dataHolder, parentFile, fileListLoadRequest.getResultFileList());
                }
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
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

    private StorageItemViewModel createGoUpModel(LibraryDataHolder dataHolder, File file) {
        StorageItemViewModel model = new StorageItemViewModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.createGoUpModel(file, dataHolder.getContext().getString(R.string.storage_go_up)));
        model.setEnableSelection(false);
        return model;
    }

    private StorageItemViewModel createNormalModel(LibraryDataHolder dataHolder, File file) {
        StorageItemViewModel model = new StorageItemViewModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.create(file, null));
        return model;
    }

    private boolean isStorageRoot(File targetDirectory) {
        return EnvironmentUtil.getStorageRootDirectory().getAbsolutePath().contains(targetDirectory.getAbsolutePath());
    }
}
