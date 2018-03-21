package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
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
        try {
            for (String path : pathList) {
                File file = new File(path);
                modifyMetadataByPath(file);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        benchmark.report("===============RxFileChangeRequest");
        return this;
    }

    private void modifyMetadataByPath(File file) {
        if (file.exists() && file.isFile()) {
            Metadata metadata = getDataProvider().findMetadataByPath(getAppContext(), file.getAbsolutePath());
            if (metadata == null || !metadata.hasValidId()) {
                metadata = Metadata.createFromFile(file, true);
                metadata.setName(FileUtils.getBaseName(file.getAbsolutePath()));
                getDataProvider().saveMetadata(getAppContext(), metadata);
            }
        } else {
            QueryArgs queryArgs = new QueryArgs();
            queryArgs.conditionGroup.and(QueryBuilder.matchLike(Metadata_Table.nativeAbsolutePath.withTable(), file.getAbsolutePath()));
            List<Metadata> metadataList = getDataProvider().findMetadataByQueryArgs(getAppContext(), queryArgs);
            DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
            database.beginTransaction();
            try {
                for (Metadata metadata : metadataList) {
                    getDataProvider().removeMetadata(getAppContext(), metadata);
                    getDataProvider().deleteMetadataCollectionByDocId(getAppContext(), metadata.getIdString());
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }
}
