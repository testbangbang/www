package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalNoteBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetPersonalNotesRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

/**
 * Created by li on 2018/1/30.
 */

public class GetPersonalNotesAction extends BaseAction {
    private List<NoteBean> notes;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setDefaultPage();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.PERSONAL_NOTES);
        baseInfo.setSign(signValue);

        final RxGetPersonalNotesRequest rq = new RxGetPersonalNotesRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                PersonalNoteBean personalNoteBean = rq.getPersonalNoteBean();
                if (personalNoteBean != null && personalNoteBean.data != null) {
                    PersonalNoteBean.DataBean data = personalNoteBean.data;
                    notes = data.items;
                }
                if (rxCallback != null) {
                    rxCallback.onNext(GetPersonalNotesAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<NoteBean> getNotes() {
        return notes;
    }
}
