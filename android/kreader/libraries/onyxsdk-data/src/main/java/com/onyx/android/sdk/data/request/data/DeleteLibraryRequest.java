package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataCacheManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/9.
 */
public class DeleteLibraryRequest extends BaseDataRequest {

    private Library library;

    public DeleteLibraryRequest(Library library) {
        this.library = library;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        DataProviderBase providerBase = getDataProviderBase(dataManager);
        DataCacheManager cacheManager = dataManager.getDataCacheManager();

        List<Library> libraryList = new ArrayList<>();
        loadAllLibrary(providerBase, libraryList, library.getIdString());
        libraryList.add(0, library);

        for (Library tmp : libraryList) {
            providerBase.deleteLibrary(tmp);
            List<Metadata> list = cacheManager.getMetadataList(tmp.getIdString());
            if (CollectionUtils.isNullOrEmpty(list)) {
                list = getListMetadata(dataManager);
            } else {
                cacheManager.removeAll(tmp.getIdString(), list);
            }

            if (list.size() <= 0) {
                continue;
            }
            cacheManager.addAll(library.getParentUniqueId(), list);
            updateCollection(providerBase, library.getParentUniqueId(), tmp.getIdString());
        }
    }

    private List<Metadata> getListMetadata(DataManager dataManager) throws Exception {
        QueryArgs args = new QueryArgs();
        args.parentId = library.getIdString();
        return getDataProviderBase(dataManager).findMetadata(getContext(), args);
    }

    private void updateCollection(DataProviderBase dataProvider, String newUniqueId, String oldUniqueId) {
        if (newUniqueId == null) {
            dataProvider.deleteMetadataCollection(getContext(), oldUniqueId, null);
        } else {
            List<MetadataCollection> collections = dataProvider.loadMetadataCollection(getContext(), oldUniqueId);
            for (MetadataCollection collection : collections) {
                collection.setLibraryUniqueId(newUniqueId);
                dataProvider.updateMetadataCollection(collection);
            }
        }
    }

    private void loadAllLibrary(DataProviderBase providerBase, List<Library> list, String targetId) throws Exception {
        List<Library> tmpList = providerBase.loadAllLibrary(targetId);
        if (tmpList.size() > 0) {
            list.addAll(tmpList);
        }
        for (Library library : tmpList) {
            loadAllLibrary(providerBase, list, library.getIdString());
        }
    }
}
