package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetBoughtAndUnlimitedRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

/**
 * Created by li on 2018/1/29.
 */

public class GetBoughtAndUnlimitedAction extends BaseAction {
    private List<Metadata> books;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setDefaultPage();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.BOUGHT_UNLIMITED_BOOKS);
        baseInfo.setSign(signValue);
        final RxGetBoughtAndUnlimitedRequest rq = new RxGetBoughtAndUnlimitedRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                books = rq.getBooks();
                if (rxCallback != null) {
                    rxCallback.onNext(GetBoughtAndUnlimitedAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<Metadata> getBooks() {
        return books;
    }
}
