package com.onyx.jdread.library.action;

import android.content.Context;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.rxrequest.data.db.RxSingleLibraryDeleteRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.view.LibraryDeleteDialog;

/**
 * Created by hehai on 17-12-22.
 */

public class LibraryDeleteAction extends BaseAction<DataBundle> {
    private Context context;
    private DataModel dataModel;
    private boolean deleteBooks;

    public LibraryDeleteAction(Context context, DataModel dataModel, boolean deleteBooks) {
        this.context = context;
        this.dataModel = dataModel;
        this.deleteBooks = deleteBooks;
    }

    @Override
    public void execute(final DataBundle dataBundle, final RxCallback baseCallback) {
        LibraryDeleteDialog.DialogModel dialogModel = new LibraryDeleteDialog.DialogModel();
        dialogModel.message.set(getDeleteMessage(dataBundle) + "?");
        LibraryDeleteDialog.Builder builder = new LibraryDeleteDialog.Builder(context, dialogModel);
        final LibraryDeleteDialog libraryDeleteDialog = builder.create();
        libraryDeleteDialog.show();
        dialogModel.setPositiveClickLister(new LibraryDeleteDialog.DialogModel.OnClickListener() {
            @Override
            public void onClicked() {
                RxSingleLibraryDeleteRequest request = new RxSingleLibraryDeleteRequest(dataBundle.getDataManager(), dataModel.idString.get(), deleteBooks);
                request.execute(baseCallback);
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

    @NonNull
    private String getDeleteMessage(DataBundle dataBundle) {
        return deleteBooks ? dataBundle.getAppContext().getString(R.string.delete_library_include_book) : dataBundle.getAppContext().getString(R.string.delete_library);
    }
}
