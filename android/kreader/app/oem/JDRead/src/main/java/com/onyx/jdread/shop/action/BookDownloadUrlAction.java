package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDownloadUrlResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookDownloadUrl;

/**
 * Created by jackdeng on 2017/12/29.
 */

public class BookDownloadUrlAction extends BaseAction {

    private BookDetailResultBean.Detail bookDetailBean;
    private BookDownloadUrlResultBean bookDownloadUrlResultBean;

    public BookDownloadUrlAction(BookDetailResultBean.Detail bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    @Override
    public void execute(final ShopDataBundle dataBundle, final RxCallback rxCallback) {
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(dataBundle.getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.BookDownloadUrl.ORDER_ID, bookDetailBean.getOrderId());
        body.put(CloudApiContext.BookDownloadUrl.UUID, dataBundle.getAppBaseInfo().getUuid());
        body.put(CloudApiContext.BookDownloadUrl.EBOOK_ID, bookDetailBean.getEbookId());
        body.put(CloudApiContext.BookDownloadUrl.USER_ID, PreferenceManager.getStringValue(JDReadApplication.getInstance(), Constants.SP_KEY_USER_NICK_NAME, ""));
        baseRequestBean.setBody(body.toString());
        final RxRequestBookDownloadUrl req = new RxRequestBookDownloadUrl();
        req.setBaseRequestBean(baseRequestBean);
        req.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                bookDownloadUrlResultBean = req.getBookDownloadUrlResultBean();
                if (bookDownloadUrlResultBean != null && StringUtils.isNotBlank(bookDownloadUrlResultBean.ebookAddress)) {
                    bookDetailBean.setDownLoadUrl( bookDownloadUrlResultBean.ebookAddress);
                    BookCertAction bookCertAction = new BookCertAction(bookDetailBean);
                    bookCertAction.execute(dataBundle, new RxCallback() {
                        @Override
                        public void onNext(Object o) {
                            if (rxCallback != null) {
                                rxCallback.onNext(BookDownloadUrlAction.this);
                            }
                        }
                    });
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
                if (bookDownloadUrlResultBean != null) {
                    if (Constants.CODE_STATE_THREE.equals(bookDownloadUrlResultBean.Code) || Constants.CODE_STATE_FOUR.equals(bookDownloadUrlResultBean.Code)) {
                        JDReadApplication.getInstance().setLogin(false);
                        //TODO autoLogin();
                    }
                }
                if (rxCallback != null) {
                    rxCallback.onComplete();
                }
            }
        });
    }
}