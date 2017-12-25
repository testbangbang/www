package com.onyx.jdread.library.action;

import android.content.Context;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.request.RxMoveToLibraryFromMultipleLibraryRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/14.
 */

public class LibraryMoveToAction extends BaseAction<DataBundle> {

    private Context context;
    private Map<String, List<Metadata>> chosenItemsMap;

    public LibraryMoveToAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback baseCallback) {
        final GetSelectedMetadataAction getSelectedAction = new GetSelectedMetadataAction();
        getSelectedAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                chosenItemsMap = getSelectedAction.getChosenItemsMap();
                if (CollectionUtils.isNullOrEmpty(chosenItemsMap)) {
                    ToastUtils.showToast(dataBundle.getAppContext(), R.string.please_select_book);
                    return;
                }
                selectTargetLibrary(dataBundle, chosenItemsMap, baseCallback);
            }
        });
    }

    private void selectTargetLibrary(final DataBundle dataBundle, Map<String, List<Metadata>> chosenItemsMap, final RxCallback baseCallback) {
        final LibrarySelectionAction selectionAction = new LibrarySelectionAction(chosenItemsMap, context);
        selectionAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                moveBookToLibrary(dataBundle, selectionAction.getLibrarySelected(), baseCallback);
            }
        });
    }

    private void moveBookToLibrary(final DataBundle dataBundle, DataModel librarySelected, final RxCallback baseCallback) {
        if (CollectionUtils.isNullOrEmpty(chosenItemsMap.keySet())) {
            return;
        }
        showLoadingDialog(dataBundle, R.string.adding);
        RxMoveToLibraryFromMultipleLibraryRequest request = new RxMoveToLibraryFromMultipleLibraryRequest(dataBundle.getDataManager(), chosenItemsMap, librarySelected);
        request.execute(new RxCallback<RxMoveToLibraryFromMultipleLibraryRequest>() {
            @Override
            public void onNext(RxMoveToLibraryFromMultipleLibraryRequest rxLibraryMoveToRequest) {
                hideLoadingDialog(dataBundle);
                if (baseCallback != null) {
                    baseCallback.onNext(rxLibraryMoveToRequest);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                hideLoadingDialog(dataBundle);
                if (throwable != null) {
                    ToastUtils.showToast(dataBundle.getAppContext(), R.string.library_move_to_fail);
                }
            }
        });
    }
}
