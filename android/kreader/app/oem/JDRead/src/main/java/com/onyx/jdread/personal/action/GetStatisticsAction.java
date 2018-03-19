package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.StatisticalData;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxGetStatisticsDataRequest;

import java.util.List;

/**
 * Created by li on 2018/3/19.
 */

public class GetStatisticsAction extends BaseAction {
    private List<StatisticalData> list;

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        final RxGetStatisticsDataRequest rq = new RxGetStatisticsDataRequest(dataBundle.getDataManager());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                list = rq.getList();
                RxCallback.invokeNext(rxCallback, GetStatisticsAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                RxCallback.invokeError(rxCallback, throwable);
            }
        });
    }

    public List<StatisticalData> getList() {
        return list;
    }
}
