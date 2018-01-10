package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.jingdong.app.reader.data.DrmTools;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CertBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestBookCert;

/**
 * Created by jackdeng on 2017/12/29.
 */

public class BookCertAction extends BaseAction {

    private BookDetailResultBean.Detail bookDetailBean;

    public BookCertAction(BookDetailResultBean.Detail bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    public BookDetailResultBean.Detail getBookDetailBean() {
        return bookDetailBean;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, RxCallback rxCallback) {
        RxRequestBookCert rxRequestBookCert = new RxRequestBookCert();
        rxRequestBookCert.setAppContext(JDReadApplication.getInstance());
        rxRequestBookCert.setDataBundle(dataBundle);
        rxRequestBookCert.setBookDetailEntity(bookDetailBean);
        BaseRequestBean requestBean = new BaseRequestBean();
        requestBean.setAppBaseInfo(dataBundle.getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.Cert.EBOOK_ID, bookDetailBean.getEbookId());
        body.put(CloudApiContext.Cert.DEVICE_MODEL, dataBundle.getAppBaseInfo().getBrand() +
                dataBundle.getAppBaseInfo().getModel());
        body.put(CloudApiContext.Cert.UUID, DrmTools.hashDevicesInfo(JDReadApplication.getInstance()));
        body.put(CloudApiContext.Cert.HAS_RANDOM, Constants.RANDOW_VALUE);
        body.put(CloudApiContext.Cert.ORDER_TYPE, Constants.ORDER_TYPE);
        body.put(CloudApiContext.Cert.ORDER_ID, bookDetailBean.isFluentRead() ? bookDetailBean.getEbookId() : bookDetailBean.getOrderId());
        body.put(CloudApiContext.Cert.HAS_CERT, Constants.HAS_CERT_VALUE);
        body.put(CloudApiContext.Cert.USER_ID, LoginHelper.getUserName());
        body.put(CloudApiContext.Cert.DEVICE_TYPE, Constants.DEVICE_TYPE_A);
        requestBean.setBody(body.toJSONString());
        rxRequestBookCert.setRequestBean(requestBean);
        rxRequestBookCert.execute(new RxCallback<RxRequestBookCert>() {
            @Override
            public void onNext(RxRequestBookCert requestBookCert) {
                CertBean certBean = requestBookCert.getCertBean();
                if (certBean != null) {
                    bookDetailBean.setKey(certBean.key);
                    bookDetailBean.setRandom(certBean.random);
                }
            }
        });
    }
}
