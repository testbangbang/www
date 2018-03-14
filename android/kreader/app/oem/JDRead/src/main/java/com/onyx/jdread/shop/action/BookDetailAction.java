package com.onyx.jdread.shop.action;

import android.text.TextUtils;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.shop.cloud.entity.GetBookDetailRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookDetail;
import com.onyx.jdread.shop.utils.ViewHelper;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookDetailAction extends BaseAction<ShopDataBundle> {

    private long bookID;
    private boolean withCookie;
    private BookDetailResultBean bookDetailResultBean;

    public BookDetailAction(long bookID,boolean withCookie) {
        this.bookID = bookID;
        this.withCookie = withCookie;
    }

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = shopDataBundle.getBookDetailViewModel();
        GetBookDetailRequestBean baseRequestBean = new GetBookDetailRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_DETAIL_URI, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        baseRequestBean.setAppBaseInfo(appBaseInfo);
        baseRequestBean.bookId = bookID;
        baseRequestBean.withCookie = withCookie;
        final RxRequestBookDetail rq = new RxRequestBookDetail();
        rq.setRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestBookDetail>() {

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
            public void onNext(RxRequestBookDetail request) {
                bookDetailResultBean = request.getBookDetailResultBean();
                if (bookDetailResultBean != null) {
                    BookDetailResultBean.DetailBean data = bookDetailResultBean.data;
                    if (!TextUtils.isEmpty(data.info)) {
                        bookDetailResultBean.data.info = CommonUtils.removeBlank(data.info);
                    }
                    if (ViewHelper.isNetBook(data.book_type)) {
                        bookDetailResultBean.data.isbn = "";
                    }
                }
                bookDetailViewModel.setBookDetailResultBean(bookDetailResultBean);
                if (rxCallback != null) {
                    rxCallback.onNext(BookDetailAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
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
