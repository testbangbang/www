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
public class BuildLibraryRequest extends BaseDBRequest {
    private Library library;
    private QueryArgs criteria;
    private List<Metadata> bookList;

    public BuildLibraryRequest(Library library, QueryArgs queryCriteria) {
        this.library = library;
        this.criteria = queryCriteria;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        library.save();
        if (criteria == null || (criteria.isAllSetContentEmpty())) {
            return;
        }
        criteria.libraryUniqueId = library.getParentUniqueId();
        QueryBuilder.generateCriteriaCondition(criteria);
        QueryBuilder.generateMetadataInQueryArgs(criteria);
        bookList = dataManager.getMetadataListWithLimit(getContext(), criteria);
        for (Metadata metadata : bookList) {
            MetadataCollection collection = DataManagerHelper.loadMetadataCollection(getContext(), dataManager,
                    library.getParentUniqueId(), metadata.getIdString());
            if (collection == null) {
                collection = MetadataCollection.create(metadata.getIdString(), library.getIdString());
            }
            collection.setLibraryUniqueId(library.getIdString());
            collection.save();
        }
    }

    public List<Metadata> getBookList() {
        return bookList;
    }
}
