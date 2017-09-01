package com.onyx.android.dr.interfaces;


import com.onyx.android.dr.bean.GroupMemberBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface GroupMemberView {
    void setGroupMemberResult(List<GroupMemberBean> list);
    void setDeleteGroupMemberResult(boolean result);
}
