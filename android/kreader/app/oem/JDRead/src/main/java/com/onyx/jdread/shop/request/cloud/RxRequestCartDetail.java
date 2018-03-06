package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CartDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.PromotionalEntityBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.SignalProductListBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.SimplifiedDetail;
import com.onyx.jdread.shop.cloud.entity.jdbean.SuitEntityListBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopCartItemData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/22.
 */

public class RxRequestCartDetail extends RxBaseCloudRequest {
    private BaseShopRequestBean requestBean;
    private List<ShopCartItemData> list = new ArrayList<>();
    private CartDetailResultBean.DataBean data;

    public CartDetailResultBean.DataBean getResultBean() {
        return data;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<CartDetailResultBean> call = getCall(service);
        Response<CartDetailResultBean> response = call.execute();
        if (response.isSuccessful()) {
            CartDetailResultBean resultBean = response.body();
            data = resultBean.data;
            if (data != null) {
                handleResult(data);
            }
        }
        return this;
    }

    private void handleResult(CartDetailResultBean.DataBean data) {
        List<SuitEntityListBean> suitList = data.suit_list;
        if (suitList != null && suitList.size() > 0) {
            for (int i = 0; i < suitList.size(); i++) {
                for(int j=0;j<suitList.get(i).product_list.size();j++){
                    ShopCartItemData itemData = new ShopCartItemData();
                    itemData.checked = true;
                    itemData.hasPromotion=suitList.get(i).is_suit_promotion;
                    itemData.promotionalEntity = suitList.get(i).promotion;

                    SignalProductListBean signalProductListBean = suitList.get(i).product_list.get(j);
                    SimplifiedDetail simplifiedDetail = new SimplifiedDetail();
                    simplifiedDetail.logo = signalProductListBean.img_url;
                    simplifiedDetail.bookName = signalProductListBean.product_name;
                    simplifiedDetail.jdPrice = signalProductListBean.price;
                    simplifiedDetail.bookId = signalProductListBean.product_id;

                    itemData.sort=signalProductListBean.sort;
                    itemData.detail = simplifiedDetail;

                    list.add(itemData);
                }
            }
            Collections.sort(list);
        }
    }

    private Call<CartDetailResultBean> getCall(ReadContentService service) {
        return service.getCartDetail(requestBean.getBaseInfo().getRequestParamsMap(),
                requestBean.getBody());
    }

    public void setRequestBean(BaseShopRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public List<ShopCartItemData> getShopCartItems() {
        return list;
    }
}
