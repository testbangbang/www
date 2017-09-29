package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;

import java.util.List;

/**
 * Created by li on 2017/9/27.
 */

public interface ShareBookReportView {
    void setGroupData(List<GroupBean> groups);

    void setGroupMemberResult(GroupMemberBean groupMembers);
}
