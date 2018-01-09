package com.onyx.jdread.personal.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.personal.cloud.entity.ReadUnlimitedRequestBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetBoughtBookRequest;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.List;

/**
 * Created by li on 2018/1/8.
 */

public class GetBoughtAction extends BaseAction {
    private List<Metadata> boughtBooks;

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        ReadUnlimitedRequestBean requestBean = new ReadUnlimitedRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        final JSONObject body = new JSONObject();
        body.put(CloudApiContext.AddToSmooth.CURRENT_PAGE, 1);
        body.put(CloudApiContext.AddToSmooth.PAGE_SIZE, 20);
        requestBean.setBody(body.toJSONString());
        final RxGetBoughtBookRequest rq = new RxGetBoughtBookRequest(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (rxCallback != null) {
                    boughtBooks = rq.getBoughtBooks();
                    if (boughtBooks != null) {
                        rxCallback.onNext(GetBoughtAction.class);
                    }
                }
            }
        });
    }

    public List<Metadata> getBoughtBooks() {
        return boughtBooks;
    }
}
