package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.RequestAddProduct;
import com.onyx.android.dr.request.cloud.RequestCreateOrders;
import com.onyx.android.dr.request.cloud.RequestGetBookDetail;
import com.onyx.android.dr.request.cloud.RequestPayForOrder;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by hehai on 17-9-6.
 */

public class BookDetailData {
    public void loadBookDetail(RequestGetBookDetail req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void createOrder(RequestCreateOrders req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void addToCart(RequestAddProduct req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
