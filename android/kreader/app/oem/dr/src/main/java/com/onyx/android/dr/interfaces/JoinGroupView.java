package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.model.v2.JoinGroupBean;
import com.onyx.android.sdk.data.model.v2.SearchGroupBean;

import java.util.List;


/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface JoinGroupView {
    void setSearchGroupResult(List<SearchGroupBean> list);
    void setJoinGroupResult(List<JoinGroupBean> list);
}
