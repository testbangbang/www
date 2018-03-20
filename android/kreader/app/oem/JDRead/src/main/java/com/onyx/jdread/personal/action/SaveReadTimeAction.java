package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.StatisticalData;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxStatisticalSaveRequest;

/**
 * Created by li on 2018/3/19.
 */

public class SaveReadTimeAction extends BaseAction {
    private StatisticalData data;

    public SaveReadTimeAction(StatisticalData data) {
        this.data = data;
    }
    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        RxStatisticalSaveRequest rq = new RxStatisticalSaveRequest(dataBundle.getDataManager(), data);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                RxCallback.invokeNext(rxCallback, SaveReadTimeAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                RxCallback.invokeError(rxCallback, throwable);
            }
        });
    }
}
