package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.RecommendListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.StoreDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestRecommendList;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class StoreBookRecommendListAction extends BaseAction<StoreDataBundle> {

    private long bookID;
    private RecommendListResultBean recommendListResultBean;

    public StoreBookRecommendListAction(long bookID) {
        this.bookID = bookID;
    }

    public RecommendListResultBean getRecommendListResultBean() {
        return recommendListResultBean;
    }

    @Override
    public void execute(StoreDataBundle storeDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = storeDataBundle.getBookDetailViewModel();


        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
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
                    rxCallback.onNext(request);
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
