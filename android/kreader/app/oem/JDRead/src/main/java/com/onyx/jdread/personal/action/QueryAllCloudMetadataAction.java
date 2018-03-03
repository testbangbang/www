package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxRequestAllCloudMetadataQuery;

import java.util.List;

/**
 * Created by li on 2018/1/6.
 */

public class QueryAllCloudMetadataAction extends BaseAction {
    private List<PersonalBookBean> books;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxRequestAllCloudMetadataQuery rq = new RxRequestAllCloudMetadataQuery(dataBundle.getDataManager());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                books = rq.getDatas();
                RxCallback.invokeNext(rxCallback, QueryAllCloudMetadataAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<PersonalBookBean> getBooks() {
        return books;
    }
}
