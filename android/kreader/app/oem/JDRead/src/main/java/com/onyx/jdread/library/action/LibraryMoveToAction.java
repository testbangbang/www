package com.onyx.jdread.library.action;

import android.content.Context;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.request.RxMoveToLibraryFromMultipleLibraryRequest;
import com.onyx.jdread.main.action.BaseAction;

import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/14.
 */

public class LibraryMoveToAction extends BaseAction<LibraryDataBundle> {

    private Context context;
    private Map<String, List<Metadata>> chosenItemsMap;

    public LibraryMoveToAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(final LibraryDataBundle libraryDataBundle, final RxCallback baseCallback) {
        final GetSelectedMetadataAction getSelectedAction = new GetSelectedMetadataAction();
        getSelectedAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                chosenItemsMap = getSelectedAction.getChosenItemsMap();
                if (CollectionUtils.isNullOrEmpty(chosenItemsMap)) {
                    return;
                }
                selectTargetLibrary(libraryDataBundle, chosenItemsMap, baseCallback);
            }
        });
    }

    private void selectTargetLibrary(final LibraryDataBundle libraryDataBundle, Map<String, List<Metadata>> chosenItemsMap, final RxCallback baseCallback) {
        final LibrarySelectionAction selectionAction = new LibrarySelectionAction(chosenItemsMap, context);
        selectionAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                moveBookToLibrary(libraryDataBundle, selectionAction.getLibrarySelected(), baseCallback);
            }
        });
    }

    private void moveBookToLibrary(final LibraryDataBundle libraryDataBundle, DataModel librarySelected, final RxCallback baseCallback) {
        if (CollectionUtils.isNullOrEmpty(chosenItemsMap.keySet())) {
            return;
        }
        showLoadingDialog(libraryDataBundle, R.string.adding);
        RxMoveToLibraryFromMultipleLibraryRequest request = new RxMoveToLibraryFromMultipleLibraryRequest(libraryDataBundle.getDataManager(), chosenItemsMap, librarySelected);
        request.execute(new RxCallback<RxMoveToLibraryFromMultipleLibraryRequest>() {
            @Override
            public void onNext(RxMoveToLibraryFromMultipleLibraryRequest rxLibraryMoveToRequest) {
                hideLoadingDialog(libraryDataBundle);
                if (baseCallback != null) {
                    baseCallback.onNext(rxLibraryMoveToRequest);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                hideLoadingDialog(libraryDataBundle);
                if (throwable != null) {
                    ToastUtils.showToast(libraryDataBundle.getAppContext(), R.string.library_move_to_fail);
                }
            }
        });
    }
}
