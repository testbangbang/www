package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.ModifyPasswordRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordFragmentData {
    public void modifyPassword(ModifyPasswordRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
