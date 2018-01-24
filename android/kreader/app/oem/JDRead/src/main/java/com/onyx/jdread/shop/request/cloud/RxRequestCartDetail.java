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
        List<SignalProductListBean> signalProductList = data.signal_product_list;
        if (signalProductList != null && signalProductList.size() > 0) {
            for (int i = 0; i < signalProductList.size(); i++) {
                ShopCartItemData itemData = new ShopCartItemData();
                SignalProductListBean signalProductListBean = signalProductList.get(i);
                itemData.checked = true;
                SimplifiedDetail simplifiedDetail = new SimplifiedDetail();
                simplifiedDetail.logo = signalProductListBean.img_url;
                simplifiedDetail.bookName = signalProductListBean.shop_name;
                simplifiedDetail.jdPrice = signalProductListBean.shop_price;
                simplifiedDetail.bookId = signalProductListBean.shop_id;
                itemData.detail = simplifiedDetail;
                itemData.reAmount = signalProductListBean.re_amount;
                list.add(itemData);
            }
        }

        List<SuitEntityListBean> suitEntityList = data.suit_entity_list;
        if (suitEntityList != null && suitEntityList.size() > 0) {
            for (int i = 0; i < suitEntityList.size(); i++) {
                SuitEntityListBean suitEntityListBean = suitEntityList.get(i);
                List<SignalProductListBean> productEntityList = suitEntityListBean.product_entity_list;
                PromotionalEntityBean promotionalEntity = suitEntityListBean.promotional_entity;

                if (productEntityList != null && productEntityList.size() > 0) {
                    for (int j = 0; j < productEntityList.size(); j++) {
                        SignalProductListBean signalProductListBean = productEntityList.get(j);
                        ShopCartItemData itemData = new ShopCartItemData();
                        itemData.checked = true;
                        SimplifiedDetail simplifiedDetail = new SimplifiedDetail();
                        simplifiedDetail.logo = signalProductListBean.img_url;
                        simplifiedDetail.bookName = signalProductListBean.shop_name;
                        simplifiedDetail.jdPrice = signalProductListBean.shop_price;
                        simplifiedDetail.bookId = signalProductListBean.shop_id;
                        itemData.detail = simplifiedDetail;
                        itemData.reAmount = signalProductListBean.re_amount;
                        itemData.promotionalEntity = promotionalEntity;
                        list.add(itemData);
                    }
                }
            }
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
