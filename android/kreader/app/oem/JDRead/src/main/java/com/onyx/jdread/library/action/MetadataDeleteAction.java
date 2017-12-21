package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.request.RxDeleteMetadataFromMultipleLibraryRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-13.
 */

public class MetadataDeleteAction extends BaseAction<DataBundle> {

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback baseCallback) {
        final GetSelectedMetadataAction getSelectedMetadataAction = new GetSelectedMetadataAction();
        getSelectedMetadataAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (CollectionUtils.isNullOrEmpty(getSelectedMetadataAction.getChosenItemsMap())) {
                    ToastUtils.showToast(dataBundle.getAppContext(), R.string.please_select_book);
                    return;
                }
                deleteMetadata(dataBundle, getSelectedMetadataAction.getChosenItemsMap(), baseCallback);
            }
        });
    }

    private void deleteMetadata(DataBundle dataBundle, Map<String, List<Metadata>> chosenItemsMap, RxCallback baseCallback) {
        RxDeleteMetadataFromMultipleLibraryRequest request = new RxDeleteMetadataFromMultipleLibraryRequest(dataBundle.getDataManager(), chosenItemsMap);
        request.execute(baseCallback);
    }
}
