package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.TopUpValueBean;
import com.onyx.jdread.personal.event.FiftyYuanEvent;
import com.onyx.jdread.personal.event.FiveHundredYuanEvent;
import com.onyx.jdread.personal.event.HundredYuanEvent;
import com.onyx.jdread.personal.event.OneYuanEvent;
import com.onyx.jdread.personal.event.TenYuanEvent;
import com.onyx.jdread.personal.event.TwoHundredYuanEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/12/30.
 */

public class RxGetTopUpValueRequest extends RxBaseCloudRequest {
    private List<TopUpValueBean> topUpValueBeans = new ArrayList<>();
    @Override
    public Object call() throws Exception {
        TopUpValueBean bean = new TopUpValueBean();
        bean.setValue("¥1.00");
        bean.setEvent(new OneYuanEvent());
        topUpValueBeans.add(bean);

        bean = new TopUpValueBean();
        bean.setValue("¥10.00");
        bean.setEvent(new TenYuanEvent());
        topUpValueBeans.add(bean);

        bean = new TopUpValueBean();
        bean.setValue("¥50.00");
        bean.setEvent(new FiftyYuanEvent());
        topUpValueBeans.add(bean);

        bean = new TopUpValueBean();
        bean.setValue("¥100.00");
        bean.setValueGift("赠500代金券");
        bean.setEvent(new HundredYuanEvent());
        topUpValueBeans.add(bean);

        bean = new TopUpValueBean();
        bean.setValue("¥200.00");
        bean.setEvent(new TwoHundredYuanEvent());
        bean.setValueGift("赠1000代金券");
        topUpValueBeans.add(bean);

        bean = new TopUpValueBean();
        bean.setValue("¥500.00");
        bean.setEvent(new FiveHundredYuanEvent());
        bean.setValueGift("赠5000代金券");
        topUpValueBeans.add(bean);
        return this;
    }

    public List<TopUpValueBean> getTopUpValueBeans() {
        return topUpValueBeans;
    }
}
