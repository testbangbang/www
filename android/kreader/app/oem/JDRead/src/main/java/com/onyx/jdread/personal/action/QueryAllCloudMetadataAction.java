package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxRequestAllCloudMetadataQuery;

import java.util.List;

/**
 * Created by li on 2018/1/6.
 */

public class QueryAllCloudMetadataAction extends BaseAction {
    private List<Metadata> metadatas;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxRequestAllCloudMetadataQuery rq = new RxRequestAllCloudMetadataQuery(dataBundle.getDataManager());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                metadatas = rq.getMetadatas();
                RxCallback.invokeNext(rxCallback, QueryAllCloudMetadataAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<Metadata> getMetadatas() {
        return metadatas;
    }
}
