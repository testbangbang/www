package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.model.v2.CreateGroupResultBean;
import com.onyx.android.sdk.data.model.v2.GroupBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface CreateGroupView {
    void setCreateGroupResult(List<CreateGroupResultBean> list);
    void setSchoolInfo(List<GroupBean> list);
}
