package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.shop.cloud.entity.BuyChaptersRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BuyChaptersResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBuyChapters;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BuyChaptersAction extends BaseAction<ShopDataBundle> {

    private String startChapterId;
    private int count;
    private long bookID;
    private BuyChaptersResultBean resultBean;

    public BuyChaptersAction(long bookID, String startChapterId, int count) {
        this.bookID = bookID;
        this.startChapterId = startChapterId;
        this.count = count;
    }

    public BuyChaptersResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        BuyChaptersRequestBean baseRequestBean = new BuyChaptersRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.BookDownLoad.START_CHAPTER_ID, startChapterId);
        queryArgs.put(CloudApiContext.BookDownLoad.CHAPTER_COUNT, String.valueOf(count));
        appBaseInfo.addRequestParams(queryArgs);
        String sign = String.format(CloudApiContext.BookShopURI.BUY_CHAPTERS, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        baseRequestBean.setBaseInfo(appBaseInfo);
        baseRequestBean.bookId = bookID;
        final RxRequestBuyChapters rq = new RxRequestBuyChapters();
        rq.setRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBuyChapters>() {

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
            public void onNext(RxRequestBuyChapters request) {
                resultBean = request.getResultBean();
                invokeNext(rxCallback, BuyChaptersAction.this);
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