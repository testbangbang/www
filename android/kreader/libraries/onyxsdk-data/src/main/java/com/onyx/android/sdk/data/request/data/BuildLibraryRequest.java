package com.onyx.android.sdk.data.request.data;

import com.onyx.android.sdk.data.DataCacheManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryCriteria;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.utils.DataProviderUtils;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by suicheng on 2016/9/7.
 */
public class BuildLibraryRequest extends BaseDataRequest {
    private Library library;
    private QueryCriteria criteria;
    private List<Metadata> bookList;

    public BuildLibraryRequest(Library library, QueryCriteria queryCriteria) {
        this.library = library;
        this.criteria = queryCriteria;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        boolean isCriteriaContentEmpty = true;
        DataProviderBase providerBase = getDataProviderBase(dataManager);
        if (criteria != null && !criteria.isAllContentEmpty()) {
            library.setQueryString(QueryCriteria.toQueryString(criteria));
            isCriteriaContentEmpty = false;
        }
        providerBase.addLibrary(library);
        if (!isCriteriaContentEmpty) {
            DataCacheManager cacheManager = dataManager.getDataCacheManager();
            bookList = cacheManager.getMetadataList(library.getParentUniqueId(), criteria);
            if (CollectionUtils.isNullOrEmpty(bookList)) {
                bookList.addAll(providerBase.findMetadata(getContext(), library.getParentUniqueId(), criteria));
            }
            DataProviderUtils.addCollections(getContext(), providerBase, library, bookList);
        }
    }

    public List<Metadata> getBookList() {
        return bookList;
    }
}
