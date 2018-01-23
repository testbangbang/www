package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2018/1/22.
 */
public class CloudLibraryDeleteRequest extends BaseCloudRequest {

    private Library library;
    private boolean deleteMetadata;

    public CloudLibraryDeleteRequest(Library library) {
        this(library, false);
    }

    public CloudLibraryDeleteRequest(Library library, boolean deleteMetadata) {
        this.library = library;
        this.deleteMetadata = deleteMetadata;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (!Library.isValid(library)) {
            return;
        }
        DataProviderBase dataProvider = parent.getCloudDataProvider();
        List<Library> libraryList = loadAllLibrary(dataProvider, library);
        processDelete(dataProvider, libraryList);
        Log.d(getClass().getSimpleName(), "exec duration " + benchmarkEnd());
    }

    private List<Library> loadAllLibrary(DataProviderBase dataProvider, Library library) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.libraryUniqueId = library.getIdString();
        queryArgs.fetchPolicy = FetchPolicy.MEM_DB_ONLY;
        List<Library> libraryList = new ArrayList<>();
        libraryList.add(library);
        DataManagerHelper.loadLibraryRecursive(dataProvider, libraryList, queryArgs);
        return libraryList;
    }

    private void processDelete(DataProviderBase dataProvider, List<Library> libraryList) {
        for (Library tmp : libraryList) {
            if (!Library.isValid(tmp)) {
                continue;
            }
            dataProvider.deleteLibrary(tmp);
            if (deleteMetadata) {
                deleteMetadata(dataProvider, tmp.getIdString());
            }
        }
    }

    private void deleteMetadata(DataProviderBase dataProvider, String libraryId) {
        List<MetadataCollection> list = dataProvider.loadMetadataCollection(getContext(), libraryId);
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        dataProvider.deleteMetadataCollection(getContext(), libraryId);
        Set<String> metadataIdSet = new HashSet<>();
        for (MetadataCollection metadataCollection : list) {
            String id = metadataCollection.getDocumentUniqueId();
            if (StringUtils.isNullOrEmpty(id)) {
                continue;
            }
            metadataIdSet.add(id);
        }
        for (String id : metadataIdSet) {
            dataProvider.deleteMetadata(getContext(), id);
            deleteMetadataFile(getContext(), id);
        }
    }

    private void deleteMetadataFile(Context context, String id) {
        String path = CloudUtils.dataCacheDirectory(context, id).getAbsolutePath();
        FileUtils.ensureFileDelete(path);
    }
}
