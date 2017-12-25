package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-21.
 */

public class RxMoveToLibraryFromMultipleLibraryRequest extends RxBaseDBRequest {
    private Map<String, List<Metadata>> chosenItemsMap;
    private DataModel toLibrary;

    public RxMoveToLibraryFromMultipleLibraryRequest(DataManager dm, Map<String, List<Metadata>> chosenItemsMap, DataModel toLibrary) {
        super(dm);
        this.chosenItemsMap = chosenItemsMap;
        this.toLibrary = toLibrary;
    }

    @Override
    public RxMoveToLibraryFromMultipleLibraryRequest call() throws Exception {
        for (Map.Entry<String, List<Metadata>> entry : chosenItemsMap.entrySet()) {
            moveToLibrary(entry.getKey(), toLibrary, entry.getValue());
        }
        return this;
    }

    private void moveToLibrary(String fromLibraryId, DataModel toLibrary, List<Metadata> list) {
        String fromIdString = null;
        String toIdString = null;
        if (StringUtils.isNotBlank(fromLibraryId)) {
            fromIdString = fromLibraryId;
        }
        if (toLibrary != null) {
            toIdString = toLibrary.idString.get();
        }
        DataProviderBase providerBase = getDataProvider();
        if (StringUtils.isNotBlank(toIdString)) {
            Library library = providerBase.findLibraryByName(getAppContext(), toLibrary.title.get());
            if (!library.hasValidId()) {
                library.setIdString(toLibrary.idString.get());
                library.setParentUniqueId(toLibrary.parentId.get());
                library.setName(toLibrary.title.get());
                library.setDescription(toLibrary.desc.get());
                getDataProvider().addLibrary(library);
            }
        }
        for (Metadata metadata : list) {
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
    }
}
