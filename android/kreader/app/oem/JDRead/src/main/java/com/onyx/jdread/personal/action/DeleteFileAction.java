package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxDeleteFileRequest;

import java.io.File;

/**
 * Created by li on 2018/3/14.
 */

public class DeleteFileAction extends BaseAction {
    private File file;

    public DeleteFileAction(File file) {
        this.file = file;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        RxDeleteFileRequest rq = new RxDeleteFileRequest(dataBundle.getDataManager(), file);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                RxCallback.invokeNext(rxCallback, DeleteFileAction.this);
            }
        });
    }
}
