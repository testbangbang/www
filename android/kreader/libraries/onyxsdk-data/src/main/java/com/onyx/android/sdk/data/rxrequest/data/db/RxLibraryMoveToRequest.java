package com.onyx.android.sdk.data.rxrequest.data.db;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.DataModel;
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

    private DataModel fromLibrary;
    private DataModel toLibrary;
    private List<DataModel> addList = new ArrayList<>();

    public RxLibraryMoveToRequest(DataManager dataManager,DataModel fromLibrary, DataModel toLibrary, List<DataModel> addList) {
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
            fromIdString = fromLibrary.idString.get();
        }
        if (toLibrary != null) {
            toIdString = toLibrary.idString.get();
        }
        DataProviderBase providerBase = getDataProvider();
        for (DataModel metadata : addList) {
            MetadataCollection collection = providerBase.loadMetadataCollection(getAppContext(),
                    fromIdString, metadata.idString.get());
            if (StringUtils.isNullOrEmpty(toIdString)) {
                if (collection != null) {
                    providerBase.deleteMetadataCollection(getAppContext(), fromIdString, metadata.idString.get());
                }
            } else {
                if (collection == null) {
                    collection = MetadataCollection.create(metadata.idString.get(), toIdString);
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
