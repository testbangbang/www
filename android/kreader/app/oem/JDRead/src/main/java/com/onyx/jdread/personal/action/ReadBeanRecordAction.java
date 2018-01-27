package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetReadBeanRecordRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

/**
 * Created by li on 2018/1/26.
 */

public class ReadBeanRecordAction extends BaseAction {
    private List<ConsumeRecordBean.DataBean> data;

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setPageSize("1", "20");
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.READ_BEAN_RECORD);
        baseInfo.setSign(signValue);

        final RxGetReadBeanRecordRequest rq = new RxGetReadBeanRecordRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ConsumeRecordBean readBeanRecord = rq.getReadBeanRecord();
                if (readBeanRecord != null) {
                    data = readBeanRecord.getData();
                }
                if (rxCallback != null) {
                    rxCallback.onNext(ReadBeanRecordAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (throwable != null) {
                    ToastUtil.showToast(throwable.getMessage());
                }
            }
        });
    }

    public List<ConsumeRecordBean.DataBean> getData() {
        return data;
    }
}
