package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2016/9/10.
 */
public class ClearLibraryRequest extends BaseDataRequest {
    private Library library;

    public ClearLibraryRequest(Library library) {
        this.library = library;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        List<Metadata> resultList = getListMetadata(dataManager);
        if (CollectionUtils.isNullOrEmpty(resultList)) {
            resultList = getListMetadata(dataManager);
        } else {
            dataManager.getDataCacheManager().removeAll(library.getIdString(), resultList);
        }

        if (resultList.size() <= 0) {
            return;
        }
        dataManager.getDataCacheManager().addAll(library.getParentUniqueId(), resultList);
        updateCollection(getDataProviderBase(dataManager), library.getParentUniqueId(), library.getIdString());
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
}
