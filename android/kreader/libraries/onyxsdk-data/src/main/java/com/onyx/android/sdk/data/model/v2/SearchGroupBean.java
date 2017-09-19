package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.CreatorBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/9/15.
 */
public class SearchGroupBean {
    public String _id;
    public String library;
    public String updatedAt;
    public String createdAt;
    public String name;
    public List<String> ancestors;
    public List<SearchGroupBean> children;
    public List<String> admin;
    public CreatorBean creator;
}
