package com.onyx.android.dr.interfaces;


import com.onyx.android.dr.bean.GroupInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface ExitGroupView {
    void setAllGroupResult(List<GroupInfoBean> list, ArrayList<Boolean> checkList);
    void setExitGroupResult(boolean result);
}
