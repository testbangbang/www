package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.model.LibrarySelectHelper;
import com.onyx.jdread.library.request.RxSelectedMetadataFromMultipleLibraryRequest;
import com.onyx.jdread.main.action.BaseAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-19.
 */

public class GetSelectedMetadataAction extends BaseAction<LibraryDataBundle> {
    private Map<String, List<Metadata>> chosenItemsMap = new HashMap<>();

    @Override
    public void execute(LibraryDataBundle libraryDataBundle, final RxCallback baseCallback) {
        chosenItemsMap.clear();
        LibrarySelectHelper selectHelper = libraryDataBundle.getLibraryViewDataModel().getSelectHelper();
        RxSelectedMetadataFromMultipleLibraryRequest fromMultipleLibraryRequest = new RxSelectedMetadataFromMultipleLibraryRequest(libraryDataBundle.getDataManager(), selectHelper);
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
