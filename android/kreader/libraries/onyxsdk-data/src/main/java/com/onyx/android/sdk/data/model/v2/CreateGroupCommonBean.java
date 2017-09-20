package com.onyx.android.sdk.data.model.v2;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/9/15.
 */
public class CreateGroupCommonBean {
    public String _id;
    public String creator;
    public String library;
    public String updatedAt;
    public String createdAt;
    public String name;
    public String parent;
    public List<String> ancestors;
    public List<CreateGroupCommonBean> children;
    public List<String> admin;
}
