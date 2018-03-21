package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxSaveContentRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/3/14.
 */

public class SaveContentAction extends BaseAction {
    private List<NoteBean> noteBeans = new ArrayList<>();
    private File file;
    private boolean result;

    public SaveContentAction(File file, List<NoteBean> noteBeans) {
        this.file = file;
        this.noteBeans.addAll(noteBeans);
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxSaveContentRequest rq = new RxSaveContentRequest(dataBundle.getDataManager(), file, noteBeans);
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
