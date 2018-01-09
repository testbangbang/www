package com.onyx.jdread.personal.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.personal.cloud.entity.ReadUnlimitedRequestBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetUnlimitedRequest;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.List;

/**
 * Created by li on 2018/1/8.
 */

public class GetUnlimitedAction extends BaseAction {
    private List<Metadata> unlimitedBooks;

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        ReadUnlimitedRequestBean requestBean = new ReadUnlimitedRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.AddToSmooth.CURRENT_PAGE, 1);
        body.put(CloudApiContext.AddToSmooth.PAGE_SIZE, 10);
        requestBean.setBody(body.toJSONString());
        final RxGetUnlimitedRequest rq = new RxGetUnlimitedRequest(requestBean);
        rq.execute(new RxCallback() {

            @Override
            public void onNext(Object o) {
                if (rxCallback != null) {
                    unlimitedBooks = rq.getUnlimitedBooks();
                    rxCallback.onNext(GetUnlimitedAction.class);
                }
            }
        });
    }

    public List<Metadata> getUnlimitedBooks() {
        return unlimitedBooks;
    }
}
