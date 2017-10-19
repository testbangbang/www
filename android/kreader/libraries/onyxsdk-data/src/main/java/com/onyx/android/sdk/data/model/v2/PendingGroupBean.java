package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.ChildBean;

import java.util.Date;

/**
 * Created by zhouzhiming on 2017/10/19.
 */
public class PendingGroupBean {
    public int __v;
    public String _id;
    public Date createdAt;
    public GroupBean group;
    public String info;
    public int status;
    public Date updatedAt;
    public ChildBean user;
}
