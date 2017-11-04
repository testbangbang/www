package com.onyx.android.sdk.data.rxrequest.data.db;

import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.request.data.db.BaseDBRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.Benchmark;

import java.util.List;

/**
 * Created by suicheng on 2016/9/7.
 */
public class RxLibraryBuildRequest extends RxBaseDBRequest {
    private Library library;
    private QueryArgs criteria;
    private List<Metadata> bookList;

    public RxLibraryBuildRequest(DataManager dataManager,Library library, QueryArgs queryCriteria) {
        super(dataManager);
        this.library = library;
        this.criteria = queryCriteria;
    }

    @Override
    public RxLibraryBuildRequest call() throws Exception {
        getDataProvider().addLibrary(library);
        if (criteria == null || (criteria.isAllSetContentEmpty())) {
            return this;
        }
        criteria.libraryUniqueId = this.library.getParentUniqueId();
        QueryBuilder.generateCriteriaCondition(criteria);
        QueryBuilder.generateMetadataInQueryArgs(criteria);

        bookList = getDataProvider().findMetadataByQueryArgs(getAppContext(), criteria);
        for (Metadata metadata : bookList) {
            MetadataCollection collection = DataManagerHelper.loadMetadataCollection(getAppContext(), getDataManager(),
                    this.library.getParentUniqueId(), metadata.getIdString());
            if (collection == null) {
                collection = MetadataCollection.create(metadata.getIdString(), this.library.getIdString());
            }
            collection.setLibraryUniqueId(this.library.getIdString());
            if (collection.hasValidId()) {
                getDataProvider().updateMetadataCollection(collection);
            } else {
                getDataProvider().addMetadataCollection(getAppContext(), collection);
            }

        }
        return this;
    }

    public List<Metadata> getBookList() {
        return bookList;
    }
}
