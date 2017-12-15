package com.onyx.jdread.library.action;

import com.onyx.android.sdk.data.rxrequest.data.db.RxFileChangeRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.library.model.DataBundle;

import java.util.List;

/**
 * Created by hehai on 17-12-14.
 */

public class ModifyLibraryDataAction extends BaseAction<DataBundle> {
    private List<String> pathList;

    public ModifyLibraryDataAction(List<String> pathList) {
        this.pathList = pathList;
    }

    @Override
    public void execute(DataBundle dataBundle, RxCallback baseCallback) {
        if (CollectionUtils.isNullOrEmpty(pathList)) {
            return;
        }
        RxFileChangeRequest rxFileChangeRequest = new RxFileChangeRequest(dataBundle.getDataManager(), pathList);
        rxFileChangeRequest.execute(baseCallback);
    }
}
