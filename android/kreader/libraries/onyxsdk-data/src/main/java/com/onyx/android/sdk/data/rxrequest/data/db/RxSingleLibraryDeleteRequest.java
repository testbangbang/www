package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.List;

/**
 * Created by hehai on 17-12-22.
 */

public class RxSingleLibraryDeleteRequest extends RxBaseDBRequest {
    private String libraryId;
    private boolean deleteBooks;

    public RxSingleLibraryDeleteRequest(DataManager dm, String libraryId, boolean deleteBooks) {
        super(dm);
        this.libraryId = libraryId;
        this.deleteBooks = deleteBooks;
    }

    @Override
    public RxSingleLibraryDeleteRequest call() throws Exception {
        if (deleteBooks) {
            QueryArgs queryArgs = new QueryArgs(SortBy.None, SortOrder.Asc).appendFilter(BookFilter.ALL);
            queryArgs.libraryUniqueId = libraryId;
            QueryBuilder.generateMetadataInQueryArgs(queryArgs);
            List<Metadata> metadataList = getDataProvider().findMetadataByQueryArgs(getAppContext(), queryArgs);
            for (Metadata metadata : metadataList) {
                getDataProvider().removeMetadata(getAppContext(), metadata);
                FileUtils.deleteFile(metadata.getNativeAbsolutePath());
            }
        }
        Library library = getDataProvider().loadLibrary(libraryId);
        getDataProvider().deleteMetadataCollection(getAppContext(), libraryId);
        getDataProvider().deleteLibrary(library);
        return this;
    }
}
