package com.onyx.kcb.action;

import android.content.Context;
import android.databinding.ObservableList;
import android.graphics.Bitmap;

import com.alibaba.fastjson.TypeReference;
import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.FileModel;
import com.onyx.android.sdk.data.provider.SystemConfigProvider;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxStorageFileListLoadRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.kcb.R;
import com.onyx.kcb.holder.DataBundle;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/11/21.
 */
public class StorageDataLoadAction extends BaseAction<DataBundle> {

    private final ObservableList<DataModel> resultDataItemList;
    private final File parentFile;
    private final Context context;
    private SortBy sortBy;
    private SortOrder sortOrder;

    public StorageDataLoadAction(Context context,final File parentFile, final ObservableList<DataModel> resultDataItemList) {
        this.context = context;
        this.parentFile = parentFile;
        this.resultDataItemList = resultDataItemList;
    }

    public void setSort(SortBy sortBy, SortOrder sortOrder) {
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    @Override
    public void execute(DataBundle dataHolder, RxCallback rxCallback) {
        loadData(dataHolder, parentFile, rxCallback);
    }

    private void loadData(final DataBundle dataHolder, final File parentFile, final RxCallback rxCallback) {
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

    public List<DataModel> loadShortcutModelList(DataBundle dataHolder) {
        List<DataModel> list = new ArrayList<>();
        List<String> dirPathList = loadShortcutList(dataHolder.getAppContext());
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

    private void addToModelItemList(DataBundle dataHolder, File parentFile, List<File> fileList) {
        resultDataItemList.clear();
        resultDataItemList.add(createGoUpModel(dataHolder, parentFile));
        if (!CollectionUtils.isNullOrEmpty(fileList)) {
            for (File file : fileList) {
                resultDataItemList.add(createNormalModel(dataHolder, file));
            }
        }
    }

    private void addShortcutModelItemList(DataBundle dataHolder) {
        if (isStorageRoot(parentFile)) {
            List<DataModel> list = loadShortcutModelList(dataHolder);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                resultDataItemList.addAll(list);
            }
        }
    }

    public DataModel createGoUpModel(DataBundle dataHolder, File file) {
        DataModel model = new DataModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.createGoUpModel(file, dataHolder.getAppContext().getString(R.string.storage_go_up)));
        model.setEnableSelection(false);
        loadThumbnail(model);
        return model;
    }

    public DataModel createNormalModel(DataBundle dataHolder, File file) {
        DataModel model = new DataModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.create(file, null));
        loadThumbnail(model);
        return model;
    }

    public DataModel createShortcutModel(DataBundle dataHolder, File file) {
        DataModel model = new DataModel(dataHolder.getEventBus());
        model.setFileModel(FileModel.createShortcutModel(file));
        loadThumbnail(model);
        return model;
    }

    private boolean isStorageRoot(File targetDirectory) {
        return EnvironmentUtil.getStorageRootDirectory().getAbsolutePath().contains(targetDirectory.getAbsolutePath());
    }

    private void loadThumbnail(final DataModel itemModel) {
        FileModel fileModel = itemModel.getFileModel();
        if (fileModel == null){
            return;
        }
        int res;
        switch (fileModel.getType()) {
            case TYPE_DIRECTORY:
                res = R.drawable.directory;
                break;
            case TYPE_GO_UP:
                res = R.drawable.directory_go_up;
                break;
            case TYPE_SHORT_CUT:
                res = R.drawable.directory_shortcut;
                break;
            case TYPE_FILE:
                res = getDrawable(fileModel.getFile());
                break;
            default:
                res = R.drawable.unknown_document;
                break;
        }

        try {
            @SuppressWarnings("ResourceType")
            InputStream inputStream = context.getResources().openRawResource(res);
            CloseableReference<Bitmap> bitmapCloseableReference = ThumbnailUtils.decodeStream(inputStream, null);
            itemModel.setCoverThumbnail(bitmapCloseableReference);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int getDrawable(File file) {
        Integer res = ThumbnailUtils.defaultThumbnailMapping().get(FilenameUtils.getExtension(file.getName()));
        if (res == null) {
            return ThumbnailUtils.thumbnailUnknown();
        }
        return res;
    }
}