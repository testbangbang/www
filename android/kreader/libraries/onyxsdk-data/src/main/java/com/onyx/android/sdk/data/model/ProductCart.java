package com.onyx.android.sdk.data.model;

/**
 * Created by zhuzeng on 11/19/15.
 */
public class ProductCart<T extends BaseData> {

    public T product;
    public String updatedAt;
    public String createdAt;
    public String user;
    public double total;
    public String _id;
    public int status;
    public int count;
    public int totalCount;
}
