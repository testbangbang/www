package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.db.table.EduAccountProvider;
import com.onyx.android.sdk.data.db.table.OnyxMetadataProvider;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.CloudContentImportList;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.CloudGroup_Table;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.EduAccount;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/6/8.
 */

public class CloudContentImportFromJsonRequest extends BaseCloudRequest {
    private static final String TAG = CloudContentImportFromJsonRequest.class.getSimpleName();

    private List<String> filePathList = new ArrayList<>();

    public CloudContentImportFromJsonRequest(List<String> filePathList) {
        this.filePathList = filePathList;
    }

    @Override
    public void execute(CloudManager cloudManager) throws Exception {
        if (CollectionUtils.isNullOrEmpty(filePathList)) {
            return;
        }
        for (String path : filePathList) {
            if (StringUtils.isNullOrEmpty(path) || !FileUtils.fileExist(path)) {
                continue;
            }
            List<CloudContentImportList> contentImportList = JSONObjectParseUtils.parseObject(
                    StringUtils.getBlankStr(FileUtils.readContentOfFile(path)),
                    new TypeReference<List<CloudContentImportList>>() {
                    });
            if (contentImportList == null || CollectionUtils.isNullOrEmpty(contentImportList)) {
                Log.w(TAG, "detect contentImportList is empty");
                continue;
            }
            pingDatabase();
            for (CloudContentImportList contentImport : contentImportList) {
                if (contentImport == null) {
                    continue;
                }
                importToDatabase(getContext(), cloudManager, FileUtils.getParent(path), contentImport);
            }
        }
    }

    private void importToDatabase(Context context, CloudManager cloudManager, String parentPath,
                                  CloudContentImportList contentImport) {
        saveGroup(contentImport.group);
        DataProviderBase dataProvider = cloudManager.getCloudDataProvider();
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        Library library = saveLibrary(context, dataProvider, contentImport.library);
        if (!CollectionUtils.isNullOrEmpty(contentImport.metadataList)) {
            for (Metadata metadata : contentImport.metadataList) {
                String fileName = FileUtils.getFileNameFromUrl(metadata.getLocation());
                if (StringUtils.isNotBlank(fileName)) {
                    metadata.setNativeAbsolutePath(parentPath + "/" + fileName);
                }
                String thumbnailPath = null;
                String thumbnailFileName = FileUtils.getFileNameFromUrl(metadata.getCoverUrl());
                if (StringUtils.isNotBlank(thumbnailFileName)) {
                    thumbnailPath = parentPath + "/" + thumbnailFileName;
                }
                boolean success = saveMetadata(context, dataProvider, metadata);
                if (!success) {
                    continue;
                }
                saveCloudCollection(context, dataProvider, library, metadata.getAssociationId());
                saveThumbnail(context, dataProvider, metadata.getAssociationId(),
                        metadata.getNativeAbsolutePath(), thumbnailPath);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private boolean saveMetadata(Context context, DataProviderBase dataProvider, Metadata metadata) {
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

    private void saveGroup(CloudGroup group) {
        if (group == null || StringUtils.isNullOrEmpty(group._id)) {
            return;
        }
        CloudGroup findItem = StoreUtils.queryDataSingle(CloudGroup.class, CloudGroup_Table._id.eq(group._id));
        if (findItem != null && findItem.hasValidId()) {
            return;
        }
        group.save();
    }

    private void saveThumbnail(Context context, DataProviderBase dataProvider, String associationId,
                               String filePath, String thumbnailPath) {
        if (StringUtils.isNullOrEmpty(thumbnailPath) || StringUtils.isNullOrEmpty(associationId)) {
            return;
        }
        if (!FileUtils.fileExist(thumbnailPath)) {
            return;
        }
        Bitmap bitmap = BitmapUtils.loadBitmapFromFile(thumbnailPath);
        if (bitmap == null) {
            return;
        }
        ThumbnailUtils.insertThumbnail(context, dataProvider, filePath, associationId, bitmap);
    }

    private void saveCloudCollection(Context context, DataProviderBase dataProvider, Library library, String associationId) {
        if (library == null || StringUtils.isNullOrEmpty(library.getIdString())) {
            return;
        }
        DataManagerHelper.saveCloudCollection(context, dataProvider, library.getIdString(), associationId);
    }

    private void pingDatabase() {
        for (int i = 0; i < 3; i++) {
            try {
                ContentUtils.querySingle(OnyxMetadataProvider.CONTENT_URI,
                        Metadata.class, ConditionGroup.clause(), null);
                StoreUtils.queryDataList(CloudGroup.class);
            } catch (Exception e) {
                TestUtils.sleep(300);
                continue;
            }
            break;
        }
    }
}
