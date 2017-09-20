package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface ExitGroupView {
    void setAllGroupResult(List<CreateGroupCommonBean> list, ArrayList<Boolean> checkList);
    void setExitGroupResult(boolean result);
}
