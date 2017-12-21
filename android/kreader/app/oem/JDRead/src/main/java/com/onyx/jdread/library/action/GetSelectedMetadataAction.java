package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.model.LibrarySelectHelper;
import com.onyx.jdread.library.request.RxSelectedMetadataFromMultipleLibraryRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-19.
 */

public class GetSelectedMetadataAction extends BaseAction<DataBundle> {
    private Map<String, List<Metadata>> chosenItemsMap = new HashMap<>();

    @Override
    public void execute(DataBundle dataBundle, final RxCallback baseCallback) {
        chosenItemsMap.clear();
        LibrarySelectHelper selectHelper = dataBundle.getLibraryViewDataModel().getSelectHelper();
        RxSelectedMetadataFromMultipleLibraryRequest fromMultipleLibraryRequest = new RxSelectedMetadataFromMultipleLibraryRequest(dataBundle.getDataManager(), selectHelper);
        fromMultipleLibraryRequest.execute(new RxCallback<RxSelectedMetadataFromMultipleLibraryRequest>() {
            @Override
            public void onNext(RxSelectedMetadataFromMultipleLibraryRequest request) {
                chosenItemsMap.putAll(request.getChosenItemsMap());
                baseCallback.onNext(request);
            }
        });
    }

    public Map<String, List<Metadata>> getChosenItemsMap() {
        return chosenItemsMap;
    }
}
