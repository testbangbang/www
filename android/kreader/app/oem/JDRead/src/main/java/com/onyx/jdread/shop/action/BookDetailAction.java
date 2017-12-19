package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookDetail;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookDetailAction extends BaseAction<ShopDataBundle> {

    private long bookID;
    private BookDetailResultBean bookDetailResultBean;

    public BookDetailAction(long bookID) {
        this.bookID = bookID;
    }

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }

    @Override
    public void execute(ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = shopDataBundle.getBookDetailViewModel();
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
                    rxCallback.onNext(BookDetailAction.this);
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
