package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.AddOrDelFromCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestAddOrDeleteCart;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jackdeng on 2017/12/29.
 */

public class AddOrDeleteCartAction extends BaseAction {

    private String[] bookIds;
    private String cartType;
    private AddOrDelFromCartBean.ResultBean result;
    private AddOrDelFromCartBean addOrDelFromCartBean;

    public AddOrDeleteCartAction(String[] bookIds, String cartType) {
        this.bookIds = bookIds;
        this.cartType = cartType;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(dataBundle.getAppBaseInfo());
        String body = getShoppingCartBody(CommonUtils.array2String(bookIds), cartType);
        baseRequestBean.setBody(body);
        RxRequestAddOrDeleteCart rq = new RxRequestAddOrDeleteCart();
        rq.setRequestBean(baseRequestBean);
        rq.execute(new RxCallback<RxRequestAddOrDeleteCart>() {
            @Override
            public void onNext(RxRequestAddOrDeleteCart rq) {
                addOrDelFromCartBean = rq.getResultBean();
                if (addOrDelFromCartBean != null) {
                    result = addOrDelFromCartBean.getResult();
                }
                if (rxCallback != null) {
                    rxCallback.onNext(AddOrDeleteCartAction.this);
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
                if (addOrDelFromCartBean != null) {
                    if (Constants.CODE_STATE_THREE.equals(addOrDelFromCartBean.getCode()) || Constants.CODE_STATE_FOUR.equals(addOrDelFromCartBean.getCode())) {
                        JDReadApplication.getInstance().setLogin(false);
                        //TODO autoLogin();
                    }
                }
            }
        });
    }

    private String getShoppingCartBody(String bookList, String type) {
        JSONObject json = new JSONObject();
        try {
            json.put(CloudApiContext.NewBookDetail.BOOK_LIST, bookList);
            json.put(CloudApiContext.NewBookDetail.TYPE, type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public AddOrDelFromCartBean.ResultBean getResult() {
        return result;
    }
}