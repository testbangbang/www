package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BookModelRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookModelAction extends BaseAction<ShopDataBundle> {

    private int modelId;
    private int modelType;
    private int currentPage;
    private BookModelBooksResultBean bookModelResultBean;

    public BookModelBooksResultBean getBookModelResultBean() {
        return bookModelResultBean;
    }

    public BookModelAction(int modelId, int modelType, int currentPage) {
        this.modelId = modelId;
        this.modelType = modelType;
        this.currentPage = currentPage;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        BookModelRequestBean requestBean = new BookModelRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        requestBean.setfType(modelType);
        requestBean.setModuleId(modelId);
        Map<String, String> queryArgs = new HashMap();
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, Constants.BOOK_PAGE_SIZE);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, String.valueOf(currentPage));
        appBaseInfo.addRequestParams(queryArgs);
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_MODULE_URI, String.valueOf(modelType), String.valueOf(modelId));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        requestBean.setAppBaseInfo(appBaseInfo);
        RxRequestBookModule request = new RxRequestBookModule();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestBookModule>() {

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
            public void onNext(RxRequestBookModule request) {
                bookModelResultBean = request.getBookModelResultBean();
                if (rxCallback != null) {
                    rxCallback.onNext(BookModelAction.this);
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
