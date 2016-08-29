package com.onyx.cloud.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;

/**
 * Created by zhuzeng on 8/27/16.
 * product and category mapping.
 */
public class ProductCollection extends BaseObject {

    @Column
    @Index
    int category;

    @Column
    @Index
    String productId;


    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
