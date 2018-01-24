package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.UpdateCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestUpdateCart;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by jackdeng on 2017/12/29.
 */

public class AddOrDeleteCartAction extends BaseAction {

    private String[] bookIds;
    private String cartType;
    private UpdateBean data;

    public AddOrDeleteCartAction(String[] bookIds, String cartType) {
        this.bookIds = bookIds;
        this.cartType = cartType;
    }

    @Override
    public void execute(ShopDataBundle dataBundle, final RxCallback rxCallback) {
        BaseShopRequestBean bean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.GotoOrder.CART);
        baseInfo.setSign(signValue);
        bean.setBaseInfo(baseInfo);

        Map<String, Object> map = new HashMap<>();
        map.put(Constants.CART_TYPE, cartType);
        if (bookIds != null && bookIds.length > 0) {
            map.put(Constants.CART_BOOK_LIST, bookIds);
        }
        String s = JSON.toJSONString(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), s);
        bean.setBody(requestBody);
        final RxRequestUpdateCart rq = new RxRequestUpdateCart();
        rq.setBaseShopRequestBean(bean);
        rq.execute(new RxCallback<RxRequestUpdateCart>() {
            @Override
            public void onNext(RxRequestUpdateCart rq) {
                UpdateCartBean resultBean = rq.getResultBean();
                if (resultBean != null) {
                    data = resultBean.data;
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
        });
    }

    public UpdateBean getData() {
        return data;
    }
}
