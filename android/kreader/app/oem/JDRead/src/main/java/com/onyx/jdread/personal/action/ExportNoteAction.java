package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ExportNoteResultBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxExportNoteRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

/**
 * Created by li on 2018/3/3.
 */

public class ExportNoteAction extends BaseAction {
    private ExportNoteBean noteBean;
    private ExportNoteResultBean resultBean;

    public ExportNoteAction(ExportNoteBean noteBean) {
        this.noteBean = noteBean;
    }

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.EXPORT_NOTE);
        baseInfo.setSign(signValue);

        final RxExportNoteRequest rq = new RxExportNoteRequest(noteBean, baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                resultBean = rq.getResultBean();
                RxCallback.invokeNext(rxCallback, ExportNoteAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                RxCallback.invokeError(rxCallback, throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public ExportNoteResultBean getResultBean() {
        return resultBean;
    }
}
