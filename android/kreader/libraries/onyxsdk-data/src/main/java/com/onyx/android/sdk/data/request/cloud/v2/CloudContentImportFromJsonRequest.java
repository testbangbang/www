package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.db.table.OnyxMetadataProvider;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.CloudContentImportConfig;
import com.onyx.android.sdk.data.model.v2.CloudContentImportList;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.CloudGroup_Table;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/6/8.
 */

public class CloudContentImportFromJsonRequest extends BaseCloudRequest {

    private List<String> filePathList = new ArrayList<>();
    private boolean copyToCloudDir = true;
    private boolean supportCFA = true;

    public CloudContentImportFromJsonRequest(List<String> filePathList) {
        this.filePathList = filePathList;
    }

    public void setCopyToCloudDir(boolean set) {
        this.copyToCloudDir = set;
    }

    public void setSupportCFA(boolean support) {
        this.supportCFA = support;
    }

    @Override
    public void execute(CloudManager cloudManager) throws Exception {
        if (CollectionUtils.isNullOrEmpty(filePathList)) {
            return;
        }
        Benchmark benchmark = new Benchmark();
        for (String path : filePathList) {
            if (StringUtils.isNullOrEmpty(path) || !FileUtils.fileExist(path)) {
                continue;
            }
            CloudContentImportConfig importConfig = JSONObjectParseUtils.parseObject(
                    StringUtils.getBlankStr(FileUtils.readContentOfFile(path)),
                    CloudContentImportConfig.class);
            List<CloudContentImportList> contentImportList = null;
            if (importConfig != null) {
                contentImportList = importConfig.contentImportList;
            }
            if (importConfig == null || contentImportList == null || CollectionUtils.isNullOrEmpty(contentImportList)) {
                Log.w(getClass().getSimpleName(), "detect contentImportList is empty");
                continue;
            }
            pingDatabase();
            if (importConfig.clearDatabase) {
                clearDatabase(cloudManager);
            }
            for (CloudContentImportList contentImport : contentImportList) {
                if (contentImport == null) {
                    continue;
                }
                importToDatabase(getContext(), cloudManager, FileUtils.getParent(path), contentImport);
            }
        }
        Log.w(getClass().getName(), "time: " + String.valueOf(benchmark.duration()) + "ms");
    }

    private void pingDatabase() {
        for (int i = 0; i < 3; i++) {
            try {
                ContentUtils.querySingle(OnyxMetadataProvider.CONTENT_URI,
                        Metadata.class, OperatorGroup.clause(), null);
                StoreUtils.queryDataList(CloudGroup.class);
            } catch (Exception e) {
                TestUtils.sleep(300);
                continue;
            }
            break;
        }
    }

    private void clearDatabase(CloudManager cloudManager) {
        StoreUtils.clearTable(CloudGroup.class);
        DataProviderBase dataProvider = cloudManager.getCloudDataProvider();
        dataProvider.clearLibrary();
        dataProvider.clearMetadataCollection();
        dataProvider.clearMetadata();
        dataProvider.clearThumbnails();
    }

    private void importToDatabase(Context context, CloudManager cloudManager, String parentPath,
                                  CloudContentImportList contentImport) {
        DataProviderBase dataProvider = cloudManager.getCloudDataProvider();
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        saveGroup(database, contentImport.group);
        Library library = saveLibrary(context, dataProvider, contentImport.library);
        saveAllMetadata(getContext(), dataProvider, parentPath, library, contentImport.metadataList);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private void saveAllMetadata(Context context, DataProviderBase dataProvider, String parentPath,
                                 Library library, List<CloudMetadata> metadataList) {
        if (!CollectionUtils.isNullOrEmpty(metadataList)) {
            for (Metadata metadata : metadataList) {
                boolean success = saveMetadata(context, dataProvider, parentPath, metadata);
                if (!success) {
                    continue;
                }
                saveCloudCollection(context, dataProvider, library, metadata.getAssociationId());
                saveThumbnail(context, dataProvider, parentPath, metadata);
            }
        }
    }

    private boolean saveMetadata(Context context, DataProviderBase dataProvider, String parentPath, Metadata metadata) {
        String fileName = FileUtils.getFileNameFromUrl(MetadataUtils.getDownloadUrl(metadata, supportCFA));
        if (StringUtils.isNotBlank(fileName)) {
            String filePath = getMetadataTargetFilePath(parentPath, metadata);
            metadata.setNativeAbsolutePath(filePath);
        }
        if (StringUtils.isNullOrEmpty(metadata.getNativeAbsolutePath())) {
            return false;
        }
        if (!FileUtils.fileExist(metadata.getNativeAbsolutePath())) {
            return false;
        }
        metadata = CloudMetadata.createFromMetadataPath(metadata, true);
        if (metadata == null) {
            return false;
        }
        dataProvider.saveMetadata(context, metadata);
        return true;
    }

    private Library saveLibrary(Context context, DataProviderBase dataProvider, Library library) {
        if (library == null || StringUtils.isNullOrEmpty(library.getIdString()) ||
                StringUtils.isNullOrEmpty(library.getName())) {
            return null;
        }
        dataProvider.addLibrary(library);
        return library;
    }

    private void saveThumbnail(Context context, DataProviderBase dataProvider, String parentPath, Metadata metadata) {
        String thumbnailPath = getCoverTargetFilePath(parentPath, metadata);
        if (StringUtils.isNullOrEmpty(thumbnailPath) || StringUtils.isNullOrEmpty(metadata.getAssociationId())) {
            return;
        }
        if (!FileUtils.fileExist(thumbnailPath)) {
            return;
        }
        Bitmap bitmap = BitmapUtils.loadBitmapFromFile(thumbnailPath);
        if (bitmap == null) {
            return;
        }
        ThumbnailUtils.insertThumbnail(context, dataProvider, metadata.getNativeAbsolutePath(),
                metadata.getAssociationId(), bitmap);
    }

    private void saveCloudCollection(Context context, DataProviderBase dataProvider, Library library, String associationId) {
        if (library == null || StringUtils.isNullOrEmpty(library.getIdString())) {
            return;
        }
        DataManagerHelper.saveCloudCollection(context, dataProvider, library.getIdString(), associationId);
    }

    private void saveGroup(DatabaseWrapper databaseWrapper, CloudGroup group) {
        if (group == null || StringUtils.isNullOrEmpty(group._id)) {
            return;
        }
        CloudGroup findItem = StoreUtils.queryDataSingle(CloudGroup.class, CloudGroup_Table._id.eq(group._id));
        if (findItem != null && findItem.hasValidId()) {
            return;
        }
        group.save(databaseWrapper);
    }

    private String getCoverKey() {
        OnyxThumbnail.ThumbnailKind kind = supportCFA ? OnyxThumbnail.ThumbnailKind.Large :
                OnyxThumbnail.ThumbnailKind.Original;
        return kind.toString().toLowerCase();
    }

    private String getCoverTargetFilePath(String sourceDir, Metadata metadata) {
        String coverKey = getCoverKey();
        File targetFile = CloudUtils.imageCachePath(getContext(), metadata.getAssociationId(), coverKey);
        if (targetFile == null) {
            return null;
        }
        String sourceFilePath = sourceDir + File.separator + FileUtils.getFileNameFromUrl(metadata.getCoverUrl(coverKey));
        if (!FileUtils.fileExist(sourceFilePath)) {
            sourceFilePath = sourceDir + File.separator + FileUtils.getFileNameFromUrl(metadata.getCoverUrl());
        }
        boolean fileExist = FileUtils.fileExist(sourceFilePath);
        return fileExist ? createTargetFile(sourceFilePath, metadata.getAssociationId(), targetFile.getName()) : null;
    }

    private String createTargetFile(String sourceFilePath, String associationId, String targetFileName) {
        if (!copyToCloudDir || StringUtils.isNullOrEmpty(associationId) || StringUtils.isNullOrEmpty(targetFileName)) {
            return sourceFilePath;
        }
        File targetFile = new File(CloudUtils.dataCacheDirectory(getContext(), associationId), targetFileName);
        boolean success = FileUtils.copyFile(new File(sourceFilePath), targetFile);
        Log.w(getClass().getSimpleName(), "createTargetFile:" + targetFile.getAbsolutePath() + " ,success:" + success);
        return success ? targetFile.getAbsolutePath() : sourceFilePath;
    }

    private String getMetadataTargetFilePath(String sourceDir, Metadata metadata) {
        String fileName = FileUtils.getFileNameFromUrl(MetadataUtils.getDownloadUrl(metadata, supportCFA));
        String sourceFilePath = sourceDir + File.separator + fileName;
        if (!copyToCloudDir || StringUtils.isNullOrEmpty(metadata.getAssociationId())) {
            return sourceFilePath;
        }
        String targetFileName = FileUtils.fixNotAllowFileName(metadata.getName() + "." + MetadataUtils.getType(metadata));
        return createTargetFile(sourceFilePath, metadata.getAssociationId(), targetFileName);
    }
}
