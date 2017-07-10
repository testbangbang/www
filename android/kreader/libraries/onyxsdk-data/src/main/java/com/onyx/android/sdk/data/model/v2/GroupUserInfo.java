package com.onyx.android.sdk.data.model.v2;

import com.onyx.android.sdk.data.model.BaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/7/8.
 */

public class GroupUserInfo extends BaseData {
    public List<CloudGroup> groups = new ArrayList<>();
    public NeoAccountBase user;
    public DeviceBind device;
    public UserInfoBind userBind;
}
