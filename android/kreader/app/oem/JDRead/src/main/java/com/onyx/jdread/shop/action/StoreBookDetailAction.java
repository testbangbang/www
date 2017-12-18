package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.StoreDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookDetail;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class StoreBookDetailAction extends BaseAction<StoreDataBundle> {

    private long bookID;
    private BookDetailResultBean bookDetailResultBean;

    public StoreBookDetailAction(long bookID) {
        this.bookID = bookID;
    }

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }

    @Override
    public void execute(StoreDataBundle storeDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = storeDataBundle.getBookDetailViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.NewBookDetail.TYPE, CloudApiContext.NewBookDetail.BOOK_SPECIAL_PRICE_TYPE);
        body.put(CloudApiContext.NewBookDetail.BOOK_ID, bookID);
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestBookDetail rq = new RxRequestBookDetail();
        rq.setBaseRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBookDetail>() {
            @Override
            public void onNext(RxRequestBookDetail request) {
                bookDetailResultBean = request.getBookDetailResultBean();
                bookDetailViewModel.setBookDetailResultBean(bookDetailResultBean);
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
