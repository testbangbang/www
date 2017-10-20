package com.onyx.android.dr.interfaces;


import com.onyx.android.sdk.data.model.v2.ChangePendingGroupBean;
import com.onyx.android.sdk.data.model.v2.PendingGroupBean;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/29.
 */
public interface ApplyForGroupView {
    void setGetPendingGroupResult(List<PendingGroupBean> list);

    void setDisposePendingGroupResult(ChangePendingGroupBean result);
}
