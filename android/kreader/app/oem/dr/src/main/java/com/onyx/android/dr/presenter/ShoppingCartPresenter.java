package com.onyx.android.dr.presenter;

import com.onyx.android.dr.bean.ProductBean;
import com.onyx.android.dr.data.ShoppingCartData;
import com.onyx.android.dr.interfaces.ShoppingCartView;
import com.onyx.android.dr.request.cloud.RequestCreateOrders;
import com.onyx.android.dr.request.cloud.RequestGetProducts;
import com.onyx.android.dr.request.cloud.RequestRemoveProduct;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.ProductRequestBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-9-12.
 */

public class ShoppingCartPresenter {
    private ShoppingCartView shoppingCartView;
    private ShoppingCartData shoppingCartData;

    public ShoppingCartPresenter(ShoppingCartView shoppingCartView) {
        this.shoppingCartView = shoppingCartView;
        shoppingCartData = new ShoppingCartData();
    }

    public void getProducts() {
        final RequestGetProducts req = new RequestGetProducts();
        shoppingCartData.getProducts(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                shoppingCartView.setProducts(shoppingCartData.getList());
            }
        });
    }

    public void removeProduct(List<ProductBean> list) {
        List<String> orderIdList = new ArrayList<>();
        for (ProductBean productBean : list) {
            orderIdList.add(productBean.getProductCart()._id);
        }
        ProductRequestBean productRequestBean = new ProductRequestBean(orderIdList);
        RequestRemoveProduct req = new RequestRemoveProduct(productRequestBean);
        shoppingCartData.removeProducts(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getProducts();
            }
        });
    }

    public void buy(List<ProductBean> selectList) {
        List<String> orderIdList = new ArrayList<>();
        for (ProductBean productBean : selectList) {
            orderIdList.add(productBean.getProductCart()._id);
        }
        ProductRequestBean productRequestBean = new ProductRequestBean(orderIdList);
        final RequestCreateOrders req = new RequestCreateOrders(productRequestBean);
        shoppingCartData.createOrder(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ProductOrder<CloudMetadata> order = req.getOrder();
                if (order != null && StringUtils.isNotBlank(order._id)) {
                    shoppingCartView.setOrderId(order._id);
                }
            }
        });
    }
}
