package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxSaveContentRequest;

import java.io.File;

/**
 * Created by li on 2018/3/14.
 */

public class SaveContentAction extends BaseAction {
    private String content;
    private File file;
    private boolean result;

    public SaveContentAction(File file, String content) {
        this.file = file;
        this.content = content;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxSaveContentRequest rq = new RxSaveContentRequest(dataBundle.getDataManager(), file, content);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                result = rq.getResult();
                RxCallback.invokeNext(rxCallback, SaveContentAction.this);
            }
        });
    }

    public boolean getResult() {
        return result;
    }
}
