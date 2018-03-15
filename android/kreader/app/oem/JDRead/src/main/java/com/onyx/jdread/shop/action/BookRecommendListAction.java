package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.shop.cloud.entity.BookRecommendListRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestRecommendList;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookRecommendListAction extends BaseAction<ShopDataBundle> {

    private long bookID;
    private RecommendListResultBean recommendListResultBean;

    public BookRecommendListAction(long bookID) {
        this.bookID = bookID;
    }

    public RecommendListResultBean getRecommendListResultBean() {
        return recommendListResultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = shopDataBundle.getBookDetailViewModel();
        BookRecommendListRequestBean requestBean = new BookRecommendListRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_RECOMMEND_LIST_URI, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        requestBean.setAppBaseInfo(appBaseInfo);
        requestBean.bookId = bookID;
        final RxRequestRecommendList rq = new RxRequestRecommendList();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback<RxRequestRecommendList>() {
            @Override
            public void onNext(RxRequestRecommendList request) {
                recommendListResultBean = request.getRecommendListResultBean();
                if (recommendListResultBean != null) {
                    bookDetailViewModel.setRecommendList(recommendListResultBean.data);
                }
                if (rxCallback != null) {
                    rxCallback.onNext(BookRecommendListAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), shopDataBundle.getEventBus());
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (rxCallback != null) {
                    rxCallback.onComplete();
                }
            }
        });
    }
}
