package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.cloud.DeleteNewMessageRequest;
import com.onyx.android.plato.requests.cloud.GetNewMessageRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/10/20.
 */

public class MainActivityData {
    public void getNewMessage(GetNewMessageRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void deleteMessage(DeleteNewMessageRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
