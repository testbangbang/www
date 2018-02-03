package com.onyx.jdread.library.action;

import android.content.Context;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.request.RxDeleteMetadataFromMultipleLibraryRequest;
import com.onyx.jdread.library.view.LibraryDeleteDialog;
import com.onyx.jdread.main.action.BaseAction;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-13.
 */

public class MetadataDeleteAction extends BaseAction<LibraryDataBundle> {
    private Context context;
    private Map<String, List<Metadata>> chosenItemsMap;

    public MetadataDeleteAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(final LibraryDataBundle libraryDataBundle, final RxCallback baseCallback) {
        final GetSelectedMetadataAction getSelectedMetadataAction = new GetSelectedMetadataAction();
        getSelectedMetadataAction.execute(libraryDataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                chosenItemsMap = getSelectedMetadataAction.getChosenItemsMap();
                if (CollectionUtils.isNullOrEmpty(chosenItemsMap)) {
                    return;
                }
                showDeleteDialog(libraryDataBundle, chosenItemsMap, baseCallback);
            }
        });
    }

    private void showDeleteDialog(final LibraryDataBundle libraryDataBundle, final Map<String, List<Metadata>> chosenItemsMap, final RxCallback baseCallback) {
        LibraryDeleteDialog.DialogModel dialogModel = new LibraryDeleteDialog.DialogModel();
        dialogModel.message.set(libraryDataBundle.getAppContext().getString(R.string.delete_book_prompt));
        LibraryDeleteDialog.Builder builder = new LibraryDeleteDialog.Builder(context, dialogModel);
        final LibraryDeleteDialog libraryDeleteDialog = builder.create();
        libraryDeleteDialog.show();
        dialogModel.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                deleteMetadata(libraryDataBundle, chosenItemsMap, baseCallback);
                libraryDeleteDialog.dismiss();
            }
        });
        dialogModel.setNegativeClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                libraryDeleteDialog.dismiss();
            }
        });
    }

    private void deleteMetadata(final LibraryDataBundle libraryDataBundle, Map<String, List<Metadata>> chosenItemsMap, final RxCallback baseCallback) {
        RxDeleteMetadataFromMultipleLibraryRequest request = new RxDeleteMetadataFromMultipleLibraryRequest(libraryDataBundle.getDataManager(), chosenItemsMap);
        request.execute(new RxCallback<RxDeleteMetadataFromMultipleLibraryRequest>() {
            @Override
            public void onNext(RxDeleteMetadataFromMultipleLibraryRequest o) {
                if (baseCallback != null) {
                    baseCallback.onNext(o);
                }
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(libraryDataBundle);
            }
        });
        showLoadingDialog(libraryDataBundle, R.string.loading);
    }
}
