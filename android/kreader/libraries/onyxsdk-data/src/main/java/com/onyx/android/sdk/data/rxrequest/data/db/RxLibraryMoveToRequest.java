package com.onyx.android.sdk.data.rxrequest.data.db;

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
public class RxLibraryMoveToRequest extends RxBaseDBRequest {

    private Library fromLibrary;
    private Library toLibrary;
    private List<Metadata> addList = new ArrayList<>();

    public RxLibraryMoveToRequest(DataManager dataManager,Library fromLibrary, Library toLibrary, List<Metadata> addList) {
        super(dataManager);
        this.fromLibrary = fromLibrary;
        this.toLibrary = toLibrary;
        this.addList.addAll(addList);
    }

    @Override
    public RxLibraryMoveToRequest call() throws Exception {
        String fromIdString = null;
        String toIdString = null;
        if (fromLibrary != null) {
            fromIdString = fromLibrary.getIdString();
        }
        if (toLibrary != null) {
            toIdString = toLibrary.getIdString();
        }
        DataProviderBase providerBase = getDataProvider();
        for (Metadata metadata : addList) {
            MetadataCollection collection = providerBase.loadMetadataCollection(getAppContext(),
                    fromIdString, metadata.getIdString());
            if (StringUtils.isNullOrEmpty(toIdString)) {
                if (collection != null) {
                    providerBase.deleteMetadataCollection(getAppContext(), fromIdString, metadata.getIdString());
                }
            } else {
                if (collection == null) {
                    collection = MetadataCollection.create(metadata.getIdString(), toIdString);
                }
                collection.setLibraryUniqueId(toIdString);
                if (collection.hasValidId()) {
                    providerBase.updateMetadataCollection(collection);
                } else {
                    providerBase.addMetadataCollection(getAppContext(), collection);
                }
            }
        }
        return this;
    }
}
