package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.rxrequest.data.db.RxMetadataDeleteRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.library.model.DataBundle;
import com.onyx.jdread.library.model.LibraryViewDataModel;

import java.util.List;

/**
 * Created by hehai on 17-12-13.
 */

public class MetadataDeleteAction extends BaseAction<DataBundle> {

    @Override
    public void execute(DataBundle dataBundle, RxCallback baseCallback) {
        LibraryViewDataModel libraryViewDataModel = dataBundle.getLibraryViewDataModel();
        if (libraryViewDataModel.selectAllFlag.get()) {
            QueryArgs queryArgs = libraryViewDataModel.getCurrentQueryArgs();
            queryArgs.limit = Integer.MAX_VALUE;
            queryArgs.offset = 0;
            deleteBooksByQueryArgs(dataBundle, queryArgs, libraryViewDataModel.getListSelected(), baseCallback);
        } else {
            deleteBooksByList(dataBundle, libraryViewDataModel.getListSelected(), baseCallback);
        }
    }

    private void deleteBooksByList(DataBundle dataBundle, List<DataModel> listSelected, RxCallback baseCallback) {
        RxMetadataDeleteRequest deleteRequest = new RxMetadataDeleteRequest(dataBundle.getDataManager(), listSelected);
        RxMetadataDeleteRequest.setAppContext(dataBundle.getAppContext());
        deleteRequest.execute(baseCallback);
    }

    private void deleteBooksByQueryArgs(DataBundle dataBundle, QueryArgs queryArgs, List<DataModel> listSelected, RxCallback baseCallback) {
        RxMetadataDeleteRequest deleteRequest = new RxMetadataDeleteRequest(dataBundle.getDataManager(), queryArgs, listSelected);
        RxMetadataDeleteRequest.setAppContext(dataBundle.getAppContext());
        deleteRequest.execute(baseCallback);
    }
}
