package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.utils.QueryBuilder;

import java.util.List;

/**
 * Created by suicheng on 2016/9/7.
 */
public class LibraryBuildRequest extends BaseDBRequest {
    private Library library;
    private QueryArgs criteria;
    private List<Metadata> bookList;

    public LibraryBuildRequest(Library library, QueryArgs queryCriteria) {
        this.library = library;
        this.criteria = queryCriteria;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        dataManager.getRemoteContentProvider().addLibrary(library);
        if (criteria == null || (criteria.isAllSetContentEmpty())) {
            return;
        }
        criteria.libraryUniqueId = library.getParentUniqueId();
        QueryBuilder.generateCriteriaCondition(criteria);
        QueryBuilder.generateMetadataInQueryArgs(criteria);

        bookList = dataManager.getRemoteContentProvider().findMetadataByQueryArgs(getContext(), criteria);
        for (Metadata metadata : bookList) {
            MetadataCollection collection = DataManagerHelper.loadMetadataCollection(getContext(), dataManager,
                    library.getParentUniqueId(), metadata.getIdString());
            if (collection == null) {
                collection = MetadataCollection.create(metadata.getIdString(), library.getIdString());
            }
            collection.setLibraryUniqueId(library.getIdString());
            if (collection.hasValidId()) {
                dataManager.getRemoteContentProvider().updateMetadataCollection(collection);
            } else {
                dataManager.getRemoteContentProvider().addMetadataCollection(getContext(), collection);
            }

        }
    }

    public List<Metadata> getBookList() {
        return bookList;
    }
}
