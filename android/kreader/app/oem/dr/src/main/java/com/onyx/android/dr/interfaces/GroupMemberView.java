package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface GroupMemberView {
    void setGroupMemberResult(GroupMemberBean bean);
    void setDeleteGroupMemberResult(DeleteGroupMemberBean bean);
}
