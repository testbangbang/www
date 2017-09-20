package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.model.GroupNameExistBean;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface CreateGroupView {
    void setCreateGroupResult(CreateGroupCommonBean bean);
    void setCheckGroupNameResult(GroupNameExistBean bean);
    void setSchoolInfo(List<CreateGroupCommonBean> list);
    void setYearInfo(CreateGroupCommonBean bean);
    void setGradeInfo(CreateGroupCommonBean bean);
    void setClassInfo(CreateGroupCommonBean bean);
}
