package com.onyx.android.sdk.data.request.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2016/9/8.
 */
public class LibraryMoveToRequest extends BaseDBRequest {

    private Library fromLibrary;
    private Library toLibrary;
    private List<Metadata> addList = new ArrayList<>();

    public LibraryMoveToRequest(Library fromLibrary, Library toLibrary, List<Metadata> addList) {
        this.fromLibrary = fromLibrary;
        this.toLibrary = toLibrary;
        this.addList.addAll(addList);
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        String fromIdString = null;
        String toIdString = null;
        if (fromLibrary != null) {
            fromIdString = fromLibrary.getIdString();
        }
        if (toLibrary != null) {
            toIdString = toLibrary.getIdString();
        }
        DataProviderBase providerBase = dataManager.getRemoteContentProvider();
        for (Metadata metadata : addList) {
            MetadataCollection collection = providerBase.loadMetadataCollection(getContext(),
                    fromIdString, metadata.getIdString());
            if (StringUtils.isNullOrEmpty(toIdString)) {
                if (collection != null) {
                    providerBase.deleteMetadataCollection(getContext(), fromIdString, metadata.getIdString());
                }
            } else {
                if (collection == null) {
                    collection = MetadataCollection.create(metadata.getIdString(), toIdString);
                }
                collection.setLibraryUniqueId(toIdString);
                if (collection.hasValidId()) {
                    providerBase.updateMetadataCollection(collection);
                } else {
                    providerBase.addMetadataCollection(getContext(), collection);
                }
            }
        }
    }
}
