package com.onyx.android.sun.data;

import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.cloud.GetSubjectAbilityRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/11/2.
 */

public class GoalAdvancedFragmentData {
    public void getSubjectAbility(final GetSubjectAbilityRequest req, final BaseCallback baseCallback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), req, baseCallback);
    }
}
