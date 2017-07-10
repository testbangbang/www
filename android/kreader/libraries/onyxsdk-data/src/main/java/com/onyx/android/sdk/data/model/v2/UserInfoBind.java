package com.onyx.android.sdk.data.model.v2;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.model.BaseData;

/**
 * Created by suicheng on 2017/6/21.
 */
public class UserInfoBind extends BaseData {
    public String _id;
    @JSONField(name = "user")
    public String userId;
    public String name;
    public String phone;
    public String address;
}
