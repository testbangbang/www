package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
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
    public void execute(ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = shopDataBundle.getBookDetailViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(shopDataBundle.getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.RecommendList.BOOK_ID, bookID);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestRecommendList rq = new RxRequestRecommendList();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestRecommendList>() {
            @Override
            public void onNext(RxRequestRecommendList request) {
                recommendListResultBean = request.getRecommendListResultBean();
                if (recommendListResultBean != null) {
                    bookDetailViewModel.setRecommendList(recommendListResultBean.recommend);
                }
                if (rxCallback != null) {
                    rxCallback.onNext(BookRecommendListAction.this);
                    rxCallback.onComplete();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }
        });
    }
}
