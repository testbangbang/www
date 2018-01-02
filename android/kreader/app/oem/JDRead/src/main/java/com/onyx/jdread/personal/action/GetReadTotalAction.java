package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.personal.cloud.entity.GetReadInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetReadTotalRequest;

/**
 * Created by li on 2018/1/2.
 */

public class GetReadTotalAction extends BaseAction {
    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        GetReadInfoRequestBean requestBean = new GetReadInfoRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        requestBean.setUserName(ClientUtils.getWJLoginHelper().getPin());
        final RxGetReadTotalRequest rq = new RxGetReadTotalRequest();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadTotalInfoBean readTotalInfoBean = rq.getReadTotalInfoBean();
                if (readTotalInfoBean != null) {
                    dataBundle.setReadTotalInfo(readTotalInfoBean);
                    if (rxCallback != null) {
                        rxCallback.onNext(GetReadTotalAction.class);
                    }
                }
            }
        });
    }
}
