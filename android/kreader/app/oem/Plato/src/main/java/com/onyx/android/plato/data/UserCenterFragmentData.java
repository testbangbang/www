package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.GetUserInfoRequest;
import com.onyx.android.plato.requests.cloud.UserLogoutRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/10/24.
 */

public class UserCenterFragmentData {
    public void userLogOut(UserLogoutRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void getUserInfo(GetUserInfoRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
