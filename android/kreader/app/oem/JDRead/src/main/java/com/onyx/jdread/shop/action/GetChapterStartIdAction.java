package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChapterStartIdResult;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestChapterStartId;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class GetChapterStartIdAction extends BaseAction<ShopDataBundle> {

    private long bookID;
    private GetChapterStartIdResult resultBean;

    public GetChapterStartIdAction(long bookID) {
        this.bookID = bookID;
    }

    public GetChapterStartIdResult getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        GetChapterGroupInfoRequestBean baseRequestBean = new GetChapterGroupInfoRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String sign = String.format(CloudApiContext.BookShopURI.GET_CHAPTER_START_ID, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        baseRequestBean.setBaseInfo(appBaseInfo);
        baseRequestBean.bookId = bookID;
        final RxRequestChapterStartId rq = new RxRequestChapterStartId();
        rq.setRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestChapterStartId>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(shopDataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(shopDataBundle);
            }

            @Override
            public void onNext(RxRequestChapterStartId request) {
                resultBean = request.getResultBean();
                invokeNext(rxCallback, GetChapterStartIdAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), shopDataBundle.getEventBus());
                invokeError(rxCallback, throwable);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                invokeComplete(rxCallback);
            }
        });
    }
}
