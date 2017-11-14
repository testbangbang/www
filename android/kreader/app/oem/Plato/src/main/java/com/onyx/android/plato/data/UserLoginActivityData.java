package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.UserLoginRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/10/21.
 */

public class UserLoginActivityData {
    public void userLogin(UserLoginRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
