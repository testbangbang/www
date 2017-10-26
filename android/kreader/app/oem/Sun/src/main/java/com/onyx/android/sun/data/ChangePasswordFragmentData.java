package com.onyx.android.sun.data;

import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.cloud.ChangePasswordRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordFragmentData {
    public void changePassword(ChangePasswordRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
