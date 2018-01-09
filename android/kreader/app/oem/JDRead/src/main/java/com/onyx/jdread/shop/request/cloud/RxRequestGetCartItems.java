package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCartItemBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.SimplifiedDetail;
import com.onyx.jdread.shop.model.ShopCartItemData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/5.
 */

public class RxRequestGetCartItems extends RxBaseCloudRequest {
    private BaseRequestBean requestBean;
    private List<ShopCartItemData> list = new ArrayList<>();
    private BookCartItemBean.CartResultBean cartResult;

    public void setRequestBean(BaseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public List<ShopCartItemData> getShopCartItems() {
        return list;
    }

    public BookCartItemBean.CartResultBean getCartResult() {
        return cartResult;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.JD_BOOK_ORDER_URL);
        Call<BookCartItemBean> call = getCall(service);
        Response<BookCartItemBean> response = call.execute();
        if (response.isSuccessful()) {
            BookCartItemBean resultBean = response.body();
            cartResult = resultBean.getCartResult();
            if (cartResult != null) {
                handleResult(cartResult);
            }
        }
        return this;
    }

    private Call<BookCartItemBean> getCall(ReadContentService service) {
        return service.getBookCartItem(CloudApiContext.GotoOrder.CART,
                requestBean.getBody(),
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private void handleResult(BookCartItemBean.CartResultBean cartResult) {
        List<BookCartItemBean.CartResultBean.SignalProductListBean> signalProductList = cartResult.getSignalProductList();
        if (signalProductList != null && signalProductList.size() > 0) {
            for (int i = 0; i < signalProductList.size(); i++) {
                BookCartItemBean.CartResultBean.SignalProductListBean signalProductListBean = signalProductList.get(i);
                ShopCartItemData itemData = new ShopCartItemData();
                itemData.checked = true;
                SimplifiedDetail simplifiedDetail = new SimplifiedDetail();
                simplifiedDetail.logo = cartResult.getImageDomain() + signalProductListBean.getImgUrl();
                simplifiedDetail.bookName = signalProductListBean.getShopName();
                simplifiedDetail.jdPrice = signalProductListBean.getShopPrice();
                simplifiedDetail.bookId = signalProductListBean.getShopId();
                itemData.detail = simplifiedDetail;
                itemData.reAmount = signalProductListBean.getReAmount();
                itemData.shopNum = signalProductListBean.getShopNum();
                list.add(itemData);
            }
        }

        List<BookCartItemBean.CartResultBean.SuitEntityListBean> suitEntityList = cartResult.getSuitEntityList();
        if (suitEntityList != null && suitEntityList.size() > 0) {
            for (int i = 0; i < suitEntityList.size(); i++) {
                List<BookCartItemBean.CartResultBean.SuitEntityListBean.ProductEntityListBean> productEntityList = suitEntityList.get(i).getProductEntityList();
                for (int j = 0; j < productEntityList.size(); j++) {
                    BookCartItemBean.CartResultBean.SuitEntityListBean.ProductEntityListBean productEntityListBean = productEntityList.get(j);
                    ShopCartItemData itemData = new ShopCartItemData();
                    itemData.checked = true;
                    SimplifiedDetail simplifiedDetail = new SimplifiedDetail();
                    simplifiedDetail.bookId = productEntityListBean.getShopId();
                    simplifiedDetail.jdPrice = productEntityListBean.getShopPrice();
                    simplifiedDetail.bookName = productEntityListBean.getShopName();
                    simplifiedDetail.logo = cartResult.getImageDomain() + productEntityListBean.getImgUrl();
                    itemData.detail = simplifiedDetail;
                    itemData.reAmount = productEntityListBean.getReAmount();
                    itemData.shopNum = productEntityListBean.getShopNum();
                    itemData.promotionalEntity = suitEntityList.get(i).getPromotionalEntity();
                    list.add(itemData);
                }
            }
        }
    }
}
