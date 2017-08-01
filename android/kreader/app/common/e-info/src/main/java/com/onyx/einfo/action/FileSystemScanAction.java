package com.onyx.einfo.action;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.request.data.db.FilesAddToMetadataRequest;
import com.onyx.android.sdk.data.request.data.db.FilesDiffFromMetadataRequest;
import com.onyx.android.sdk.data.request.data.db.MetadataRequest;
import com.onyx.android.sdk.data.request.data.fs.FileSystemScanRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.einfo.holder.LibraryDataHolder;
import com.raizlabs.android.dbflow.sql.language.Condition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2017/7/29.
 */

public class FileSystemScanAction extends BaseAction<LibraryDataHolder> {
    static public final String MMC_STORAGE_ID = "flash";

    private String storageId;
    private boolean isFlash;

    public FileSystemScanAction(final String storageId, final boolean isFlash) {
        this.storageId = storageId;
        this.isFlash = isFlash;
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        startFileSystemScan(dataHolder, baseCallback);
    }

    private void startFileSystemScan(LibraryDataHolder dataHolder, BaseCallback baseCallback) {
        startFileSystemScan(dataHolder, baseCallback, storageId, getBookDirList(isFlash));
    }

    private void startFileSystemScan(final LibraryDataHolder dataHolder, final BaseCallback baseCallback,
                                     final String storageId, List<String> bookDirList) {
        final FileSystemScanRequest fileSystemScanRequest = new FileSystemScanRequest(storageId, bookDirList, true);
        fileSystemScanRequest.setExtensionFilterSet(TestUtils.defaultContentTypes());
        dataHolder.getDataManager().submit(dataHolder.getContext().getApplicationContext(),
                fileSystemScanRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            BaseCallback.invoke(baseCallback, request, e);
                            return;
                        }
                        HashSet<String> hashSet = fileSystemScanRequest.getResult();
                        String tmp = storageId;
                        if (tmp.equals(MMC_STORAGE_ID)) {
                            tmp = null;
                        }
                        startMetadataPathScan(dataHolder, baseCallback, tmp, hashSet);
                    }
                });
    }

    private void startMetadataPathScan(final LibraryDataHolder dataHolder, final BaseCallback baseCallback,
                                       final String storageId, final HashSet<String> filePathSet) {
        QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.CreationTime, SortOrder.Desc);
        queryArgs.propertyList.add(Metadata_Table.nativeAbsolutePath);
        Condition condition;
        if (StringUtils.isNullOrEmpty(storageId)) {
            condition = Metadata_Table.storageId.isNull();
        } else {
            condition = Metadata_Table.storageId.eq(storageId);
        }
        queryArgs.conditionGroup.and(condition);
        final MetadataRequest metadataRequest = new MetadataRequest(queryArgs);
        dataHolder.getDataManager().submit(dataHolder.getContext().getApplicationContext(), metadataRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            BaseCallback.invoke(baseCallback, request, e);
                            return;
                        }
                        processMetaSnapshot(dataHolder, baseCallback, storageId, filePathSet, metadataRequest.getPathList());
                    }
                });
    }

    private void processMetaSnapshot(final LibraryDataHolder dataHolder, final BaseCallback baseCallback,
                                     final String storageId, HashSet<String> fileList, HashSet<String> snapshotList) {
        final FilesDiffFromMetadataRequest filesFromMetadataRequest = new FilesDiffFromMetadataRequest(
                fileList, snapshotList);
        dataHolder.getDataManager().submit(dataHolder.getContext().getApplicationContext(), filesFromMetadataRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        HashSet<String> addedSet = filesFromMetadataRequest.getDiffSet();
                        if (e != null || CollectionUtils.isNullOrEmpty(addedSet)) {
                            BaseCallback.invoke(baseCallback, request, e);
                            return;
                        }
                        addFilesToMetaData(dataHolder, baseCallback, storageId, addedSet);
                    }
                });
    }

    private void addFilesToMetaData(final LibraryDataHolder dataHolder, final BaseCallback baseCallback, String storageId, Set<String> addedSet) {
        FilesAddToMetadataRequest addRequest = new FilesAddToMetadataRequest(storageId, addedSet);
        dataHolder.getDataManager().submitToMulti(dataHolder.getContext().getApplicationContext(), addRequest,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dataHolder.getDataManager().getCacheManager().clearMetadataCache();
                        BaseCallback.invoke(baseCallback, request, e);
                    }
                });
    }

    private List<String> getBookDirList(boolean isFlash) {
        List<String> dirList = new ArrayList<>();
        if (isFlash) {
            dirList.add(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath());
        } else {
            dirList.add(EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath());
        }
        return dirList;
    }
}
