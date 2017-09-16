package com.onyx.android.dr.bean;

import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;

/**
 * Created by hehai on 17-9-12.
 */

public class ProductBean {
    private boolean isFirst;
    private boolean isChecked;
    private ProductCart<CloudMetadata> productCart;

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ProductBean(ProductCart<CloudMetadata> productCart) {
        this.productCart = productCart;
    }

    public ProductCart<CloudMetadata> getProductCart() {
        return productCart;
    }
}
