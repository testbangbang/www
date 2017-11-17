package com.onyx.android.plato.data;


import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.GetSubjectAbilityRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/11/2.
 */

public class GoalAdvancedFragmentData {
    public void getSubjectAbility(final GetSubjectAbilityRequest req, final BaseCallback baseCallback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), req, baseCallback);
    }
}
