package com.onyx.android.sdk.data.model;

import java.util.List;

/**
 * Created by zhuzeng on 11/19/15.
 */
public class ProductOrder<T extends BaseData> {

    public List<T> product;
    public String updatedAt;
    public String createdAt;
    public String user;
    public double total;
    public String _id;
    public int status;
    public int count;
}
