package com.onyx.jdread.shop.action;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookstoreModelResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookStoreViewModel;
import com.onyx.jdread.shop.model.StoreDataBundle;
import com.onyx.jdread.shop.model.SubjectViewModel;
import com.onyx.jdread.shop.request.cloud.RxRequestBookstoreModule;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class StoreNewBookAction extends BaseAction<StoreDataBundle> {

    private Context context;
    private BookStoreViewModel storeViewModel;

    public StoreNewBookAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(StoreDataBundle storeDataBundle, final RxCallback rxCallback) {
        storeViewModel = storeDataBundle.getStoreViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.BookstoreModule.ID, CloudApiContext.BookstoreModule.NEW_BOOK_DELIVERY_ID);
        body.put(CloudApiContext.BookstoreModule.MODULE_TYPE, CloudApiContext.BookstoreModule.NEW_BOOK_DELIVERY_MODULE_TYPE);
        baseRequestBean.setBody(body.toJSONString());
        RxRequestBookstoreModule request = new RxRequestBookstoreModule();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestBookstoreModule>() {
            @Override
            public void onNext(RxRequestBookstoreModule request) {
                BookstoreModelResultBean bookstoreModelResultBean = request.getBookstoreModelResultBean();
                SubjectViewModel subjectViewModel = new SubjectViewModel();
                subjectViewModel.setModelBean(bookstoreModelResultBean);
                storeViewModel.setCoverSubjectIems(subjectViewModel);
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
