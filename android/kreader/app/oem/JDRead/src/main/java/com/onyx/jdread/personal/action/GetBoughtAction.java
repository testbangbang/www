package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetBoughtAndUnlimitedRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2018/1/8.
 */

public class GetBoughtAction extends BaseAction {
    private List<Metadata> boughtBooks;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setDefaultPage();
        Map<String, String> map = new HashMap<>();
        map.put("search_type", "1");
        baseInfo.addRequestParams(map);
        String signValue = baseInfo.getSignValue(CloudApiContext.User.BOUGHT_UNLIMITED_BOOKS);
        baseInfo.setSign(signValue);
        final RxGetBoughtAndUnlimitedRequest rq = new RxGetBoughtAndUnlimitedRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                boughtBooks = rq.getBooks();
                RxCallback.invokeNext(rxCallback, GetBoughtAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<Metadata> getBoughtBooks() {
        return boughtBooks;
    }
}
