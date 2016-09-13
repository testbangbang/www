package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataCacheManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryCriteria;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.utils.DataProviderUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2016/9/12.
 */
public class ModifyLibraryRequest extends BaseDataRequest {
    private Library library;
    private boolean modifyCriteria = false;

    public ModifyLibraryRequest(Library library, boolean modifyCriteria) {
        this.library = library;
        this.modifyCriteria = modifyCriteria;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        getDataProviderBase(dataManager).addLibrary(library);
        if (modifyCriteria) {
            DataCacheManager cacheManager = dataManager.getDataCacheManager();
            DataProviderBase providerBase = getDataProviderBase(dataManager);
            List<Metadata> list = cacheManager.getMetadataList(library.getIdString());
            if (CollectionUtils.isNullOrEmpty(list)) {
                QueryArgs args = new QueryArgs();
                args.parentId = library.getIdString();
                list.addAll(providerBase.findMetadata(getContext(), args));
            }
            if (!CollectionUtils.isNullOrEmpty(list)) {
                cacheManager.removeAll(library.getIdString(), list);
                DataProviderUtils.removeCollections(getContext(), providerBase, library, list);
            }
            QueryCriteria criteria = QueryCriteria.fromQueryString(library.getQueryString());
            list = providerBase.findMetadata(getContext(), library.getParentUniqueId(), criteria);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                cacheManager.addAll(library.getIdString(), list);
                DataProviderUtils.addCollections(getContext(), providerBase, library, list);
            }
        }
    }
}
