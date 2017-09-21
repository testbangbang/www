package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.model.v2.AllGroupBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface ManageGroupView {
    void setGroupMemberResult(List<AllGroupBean> list);
    void setExitGroupResult(DeleteGroupMemberBean result);
}
