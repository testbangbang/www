package com.onyx.kcb.action;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.rxrequest.data.db.RxFilesAddToMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileSystemScanRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFilesDiffFromMetadataRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kcb.holder.DataBundle;
import com.raizlabs.android.dbflow.sql.language.Condition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2017/7/29.
 */

public class RxFileSystemScanAction extends BaseAction<DataBundle> {
    static public final String MMC_STORAGE_ID = "flash";

    private String storageId;
    private boolean isFlash;

    public RxFileSystemScanAction(String storageId, boolean isFlash) {
        this.storageId = storageId;
        this.isFlash = isFlash;
    }

    @Override
    public void execute(DataBundle dataHolder, RxCallback baseCallback) {
        startFileSystemScan(dataHolder, baseCallback);
    }

    private void startFileSystemScan(DataBundle dataHolder, RxCallback baseCallback) {
        startFileSystemScan(dataHolder, baseCallback, storageId, getBookDirList(isFlash));
    }

    private void startFileSystemScan(final DataBundle dataHolder, final RxCallback baseCallback,
                                     final String storageId, List<String> bookDirList) {
        final RxFileSystemScanRequest fileSystemScanRequest = new RxFileSystemScanRequest(dataHolder.getDataManager(), storageId, bookDirList, true);
        fileSystemScanRequest.setExtensionFilterSet(MimeTypeUtils.getDocumentExtension());
        fileSystemScanRequest.execute(new RxCallback<RxFileSystemScanRequest>() {
            @Override
            public void onNext(RxFileSystemScanRequest rxFileSystemScanRequest) {
                HashSet<String> hashSet = fileSystemScanRequest.getResult();
                String tmp = storageId;
                if (tmp.equals(MMC_STORAGE_ID)) {
                    tmp = null;
                }
                startMetadataPathScan(dataHolder, baseCallback, tmp, hashSet);
            }
        });
    }

    private void startMetadataPathScan(final DataBundle dataHolder, final RxCallback baseCallback,
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
        final RxMetadataRequest metadataRequest = new RxMetadataRequest(dataHolder.getDataManager(), queryArgs);
        metadataRequest.execute(new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest request) {
                processMetaSnapshot(dataHolder, baseCallback, storageId, filePathSet, metadataRequest.getPathList());
            }
        });
    }

    private void processMetaSnapshot(final DataBundle dataHolder, final RxCallback baseCallback,
                                     final String storageId, HashSet<String> fileList, HashSet<String> snapshotList) {
        final RxFilesDiffFromMetadataRequest filesFromMetadataRequest = new RxFilesDiffFromMetadataRequest(dataHolder.getDataManager(),
                fileList, snapshotList);
        RxFilesDiffFromMetadataRequest.setAppContext(dataHolder.getAppContext());
        filesFromMetadataRequest.execute(new RxCallback<RxFilesDiffFromMetadataRequest>() {
            @Override
            public void onNext(RxFilesDiffFromMetadataRequest rxFilesDiffFromMetadataRequest) {
                HashSet<String> addedSet = filesFromMetadataRequest.getDiffSet();
                addFilesToMetaData(dataHolder, baseCallback, storageId, addedSet);
            }
        });
    }

    private void addFilesToMetaData(final DataBundle dataHolder, final RxCallback baseCallback, String storageId, Set<String> addedSet) {
        RxFilesAddToMetadataRequest addRequest = new RxFilesAddToMetadataRequest(dataHolder.getDataManager(), storageId, addedSet);
        addRequest.execute(new RxCallback<RxFilesAddToMetadataRequest>() {
            @Override
            public void onNext(RxFilesAddToMetadataRequest rxFilesAddToMetadataRequest) {
                dataHolder.getDataManager().getCacheManager().clearMetadataCache();
                if (baseCallback != null) {
                    baseCallback.onNext(rxFilesAddToMetadataRequest);
                    baseCallback.onComplete();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (baseCallback != null) {
                    baseCallback.onError(throwable);
                }
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
