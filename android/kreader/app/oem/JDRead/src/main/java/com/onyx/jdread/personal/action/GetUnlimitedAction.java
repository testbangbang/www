package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.cloud.entity.jdbean.PersonalBookBean;
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

public class GetUnlimitedAction extends BaseAction {
    private List<PersonalBookBean> unlimitedBooks;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setDefaultPage();
        Map<String, String> map = new HashMap<>();
        map.put(Constants.SEARCH_TYPE, Constants.TYPE_UNLIMITED);
        baseInfo.addRequestParams(map);
        String signValue = baseInfo.getSignValue(CloudApiContext.User.BOUGHT_UNLIMITED_BOOKS);
        baseInfo.setSign(signValue);
        final RxGetBoughtAndUnlimitedRequest rq = new RxGetBoughtAndUnlimitedRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {

            @Override
            public void onNext(Object o) {
                unlimitedBooks = rq.getBooks();
                RxCallback.invokeNext(rxCallback, GetUnlimitedAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<PersonalBookBean> getUnlimitedBooks() {
        return unlimitedBooks;
    }
}
