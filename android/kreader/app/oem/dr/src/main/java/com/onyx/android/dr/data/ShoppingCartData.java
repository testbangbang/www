package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.bean.ProductBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.request.cloud.RequestCreateOrders;
import com.onyx.android.dr.request.cloud.RequestGetProducts;
import com.onyx.android.dr.request.cloud.RequestRemoveProduct;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-9-12.
 */

public class ShoppingCartData {
    private List<ProductBean> list = new ArrayList<>();
    private List<ProductBean> chineseList = new ArrayList<>();
    private List<ProductBean> englishList = new ArrayList<>();
    private List<ProductBean> smallLanguageList = new ArrayList<>();

    public void getProducts(final RequestGetProducts req, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<ProductCart<CloudMetadata>> carts = req.getCarts();
                checkProduct(carts);
                invoke(baseCallback, request, e);
            }
        });
    }

    private void checkProduct(QueryResult<ProductCart<CloudMetadata>> cartResult) {
        chineseList.clear();
        englishList.clear();
        smallLanguageList.clear();
        if (cartResult != null && !CollectionUtils.isNullOrEmpty(cartResult.list)) {
            for (ProductCart<CloudMetadata> product : cartResult.list) {
                ProductBean productBean = new ProductBean(product.product);
                if (Constants.CHINESE.equals(product.product.getLanguage())) {
                    productBean.setFirst(chineseList.isEmpty());
                    chineseList.add(productBean);
                } else if (Constants.ENGLISH.equals(product.product.getLanguage())) {
                    productBean.setFirst(englishList.isEmpty());
                    englishList.add(productBean);
                } else {
                    productBean.setFirst(smallLanguageList.isEmpty());
                    smallLanguageList.add(productBean);
                }
            }
        }
    }

    public void removeProducts(RequestRemoveProduct req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public List<ProductBean> getList() {
        list.clear();
        list.addAll(chineseList);
        list.addAll(englishList);
        list.addAll(smallLanguageList);
        return list;
    }

    public void createOrder(RequestCreateOrders req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
