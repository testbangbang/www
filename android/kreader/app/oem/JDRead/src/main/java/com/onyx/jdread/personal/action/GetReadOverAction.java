package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.personal.cloud.entity.GetReadInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetReadOverRequest;

/**
 * Created by li on 2018/1/2.
 */

public class GetReadOverAction extends BaseAction {
    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        GetReadInfoRequestBean requestBean = new GetReadInfoRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        requestBean.setUserName(ClientUtils.getWJLoginHelper().getPin());
        final RxGetReadOverRequest rq = new RxGetReadOverRequest();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadOverInfoBean readOverInfoBean = rq.getReadOverInfoBean();
                if (readOverInfoBean != null) {
                    dataBundle.setReadOverInfo(readOverInfoBean);
                    if (rxCallback != null) {
                        rxCallback.onNext(GetReadOverAction.class);
                    }
                }
            }
        });
    }
}
