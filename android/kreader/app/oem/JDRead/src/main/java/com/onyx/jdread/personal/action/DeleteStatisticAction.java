package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxDeleteStatisticsRequest;

/**
 * Created by li on 2018/3/19.
 */

public class DeleteStatisticAction extends BaseAction {
    private String cloudId;

    public DeleteStatisticAction(String cloudId) {
        this.cloudId = cloudId;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        RxDeleteStatisticsRequest rq = new RxDeleteStatisticsRequest(dataBundle.getDataManager(), cloudId);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                RxCallback.invokeNext(rxCallback, DeleteStatisticAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                RxCallback.invokeError(rxCallback, throwable);
            }
        });
    }
}
