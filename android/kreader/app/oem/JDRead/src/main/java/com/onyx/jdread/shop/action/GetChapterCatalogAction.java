package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChapterCatalogResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetChapterCatalog;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class GetChapterCatalogAction extends BaseAction<ShopDataBundle> {

    private long bookID;
    private GetChapterCatalogResultBean resultBean;

    public GetChapterCatalogAction(long bookID) {
        this.bookID = bookID;
    }

    public GetChapterCatalogResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        GetChapterGroupInfoRequestBean baseRequestBean = new GetChapterGroupInfoRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        String sign = String.format(CloudApiContext.BookShopURI.GET_CHAPTER_CATALOG, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        baseRequestBean.setBaseInfo(appBaseInfo);
        baseRequestBean.bookId = bookID;
        final RxRequestGetChapterCatalog rq = new RxRequestGetChapterCatalog();
        rq.setRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestGetChapterCatalog>() {

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
            public void onNext(RxRequestGetChapterCatalog request) {
                resultBean = request.getResultBean();
                invokeNext(rxCallback, GetChapterCatalogAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
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
