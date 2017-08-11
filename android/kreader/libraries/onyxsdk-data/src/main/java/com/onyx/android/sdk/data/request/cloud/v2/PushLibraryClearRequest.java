package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudQueryBuilder;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.PushLibraryClearEvent;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/8/2.
 */
public class PushLibraryClearRequest extends BaseCloudRequest {

    private PushLibraryClearEvent libraryClear;

    public PushLibraryClearRequest(PushLibraryClearEvent libraryClear) {
        this.libraryClear = libraryClear;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (libraryClear == null || libraryClear.library == null) {
            Log.w("libraryContentClear", "detect the library is null");
            return;
        }
        Library clearLibrary = libraryClear.library;
        List<Library> libraryList = loadLibraryList(parent, clearLibrary);
        clearLibraryList(parent, libraryList);
        if (libraryClear.deleteCurrent) {
            deleteCurrentLibrary(parent, clearLibrary);
        }
    }

    private List<Library> loadLibraryList(CloudManager parent, Library parentLibrary) {
        List<Library> libraryList = new ArrayList<>();
        DataProviderBase dataProvider = parent.getCloudDataProvider();
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.libraryUniqueId = parentLibrary.getIdString();
        queryArgs.fetchPolicy = FetchPolicy.MEM_DB_ONLY;
        if (libraryClear.recursive) {
            DataManagerHelper.loadLibraryRecursive(dataProvider, libraryList, queryArgs);
        } else {
            DataManagerHelper.loadLibraryList(dataProvider, libraryList, queryArgs);
        }
        libraryList.add(parentLibrary);
        return libraryList;
    }

    private QueryArgs getMetadataQueryArgs(Library library) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.fetchPolicy = FetchPolicy.MEM_DB_ONLY;
        queryArgs.limit = Integer.MAX_VALUE;
        queryArgs.libraryUniqueId = library.getIdString();
        CloudQueryBuilder.generateMetadataInQueryArgs(queryArgs);
        return queryArgs;
    }

    private QueryResult<Metadata> getMetadataQueryResult(CloudManager parent, Library library) {
        QueryArgs args = getMetadataQueryArgs(library);
        DataProviderBase dataProvider = parent.getCloudDataProvider();
        return dataProvider.findMetadataResultByQueryArgs(getContext(), args);
    }

    private void clearLibrary(CloudManager parent, Library library) {
        DataProviderBase dataProvider = parent.getCloudDataProvider();
        QueryResult<Metadata> result = getMetadataQueryResult(parent, library);
        final DatabaseWrapper database = FlowManager.getDatabase(ContentDatabase.NAME).getWritableDatabase();
        database.beginTransaction();
        deleteLibrary(dataProvider, library);
        if (QueryResult.isValidQueryResult(result)) {
            for (Metadata metadata : result.list) {
                dataProvider.deleteMetadataCollection(getContext(), library.getIdString(), metadata.getAssociationId());
                deleteMetadata(getContext(), dataProvider, metadata);
            }
        }
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    private void clearLibraryList(CloudManager parent, List<Library> libraryList) {
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        for (Library library : libraryList) {
            clearLibrary(parent, library);
        }
    }

    private void deleteCurrentLibrary(CloudManager parent, Library currentLibrary) {
        Library loadedLibrary = parent.getCloudDataProvider().loadLibrary(currentLibrary.getIdString());
        deleteLibrary(parent.getCloudDataProvider(), loadedLibrary);
    }

    private void deleteLibrary(DataProviderBase dataProvider, Library library) {
        if (library == null || !library.hasValidId()) {
            return;
        }
        dataProvider.deleteLibrary(library);
    }

    private void deleteMetadata(Context context, DataProviderBase dataProvider, Metadata metadata) {
        dataProvider.removeMetadata(context, metadata);
        deleteMetadataFile(metadata.getNativeAbsolutePath());
    }

    private void deleteMetadataFile(String filePath) {
        if (StringUtils.isNullOrEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        file.delete();
    }
}
