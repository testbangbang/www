package com.onyx.android.dr.interfaces;

import com.onyx.android.dr.bean.ProductBean;

import java.util.List;

/**
 * Created by hehai on 17-9-12.
 */

public interface ShoppingCartView {
    void setProducts(List<ProductBean> products);

    void setOrderId(String id);
}
