package com.onyx.jdread.library.action;

import android.content.Context;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.request.RxDeleteMetadataFromMultipleLibraryRequest;
import com.onyx.jdread.library.view.LibraryDeleteDialog;

import java.util.List;
import java.util.Map;

/**
 * Created by hehai on 17-12-13.
 */

public class MetadataDeleteAction extends BaseAction<DataBundle> {
    private Context context;
    private Map<String, List<Metadata>> chosenItemsMap;

    public MetadataDeleteAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback baseCallback) {
        final GetSelectedMetadataAction getSelectedMetadataAction = new GetSelectedMetadataAction();
        getSelectedMetadataAction.execute(dataBundle, new RxCallback() {
            @Override
            public void onNext(Object o) {
                chosenItemsMap = getSelectedMetadataAction.getChosenItemsMap();
                if (CollectionUtils.isNullOrEmpty(chosenItemsMap)) {
                    ToastUtils.showToast(dataBundle.getAppContext(), R.string.please_select_book);
                    return;
                }
                showDeleteDialog(dataBundle, chosenItemsMap, baseCallback);
            }
        });
    }

    private void showDeleteDialog(final DataBundle dataBundle, final Map<String, List<Metadata>> chosenItemsMap, final RxCallback baseCallback) {
        LibraryDeleteDialog.DialogModel dialogModel = new LibraryDeleteDialog.DialogModel();
        dialogModel.message.set(dataBundle.getAppContext().getString(R.string.delete_book_prompt));
        LibraryDeleteDialog.Builder builder = new LibraryDeleteDialog.Builder(context, dialogModel);
        final LibraryDeleteDialog libraryDeleteDialog = builder.create();
        libraryDeleteDialog.show();
        dialogModel.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                deleteMetadata(dataBundle, chosenItemsMap, baseCallback);
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

    private void deleteMetadata(DataBundle dataBundle, Map<String, List<Metadata>> chosenItemsMap, RxCallback baseCallback) {
        RxDeleteMetadataFromMultipleLibraryRequest request = new RxDeleteMetadataFromMultipleLibraryRequest(dataBundle.getDataManager(), chosenItemsMap);
        request.execute(baseCallback);
    }
}
