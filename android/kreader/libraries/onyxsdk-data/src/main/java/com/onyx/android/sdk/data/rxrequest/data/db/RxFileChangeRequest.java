package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by hehai on 17-12-14.
 */

public class RxFileChangeRequest extends RxBaseDBRequest {
    private List<String> pathList;
    private Set<String> extensionFilterSet;

    public RxFileChangeRequest(DataManager dm, List<String> pathList) {
        super(dm);
        this.pathList = pathList;
    }

    public void setExtensionFilterSet(Set<String> extensionFilterSet) {
        this.extensionFilterSet = extensionFilterSet;
    }

    @Override
    public RxFileChangeRequest call() throws Exception {
        Benchmark benchmark = new Benchmark();
        Debug.setDebug(true);
        DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        for (String path : pathList) {
            File file = new File(path);
            if (!extensionFilterSet.contains(FileUtils.getFileExtension(file))) {
                continue;
            }
            modifyMetadataByPath(file);
        }
        database.setTransactionSuccessful();
        database.endTransaction();
        benchmark.report("===============RxFileChangeRequest");
        return this;
    }

    private void modifyMetadataByPath(File file) {
        if (file.exists()) {
            Metadata metadata = getDataProvider().findMetadataByPath(getAppContext(), file.getAbsolutePath());
            if (metadata == null || !metadata.hasValidId()) {
                metadata = Metadata.createFromFile(file, true);
                metadata.setName(FileUtils.getBaseName(file.getAbsolutePath()));
                getDataProvider().saveMetadata(getAppContext(), metadata);
            }
        } else {
            Metadata metadata = getDataProvider().findMetadataByPath(getAppContext(), file.getAbsolutePath());
            getDataProvider().removeMetadata(getAppContext(), metadata);
            getDataProvider().deleteMetadataCollectionByDocId(getAppContext(), metadata.getIdString());
        }
    }
}
