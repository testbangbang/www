package com.onyx.android.sun.data;

import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.cloud.DeleteNewMessageRequest;
import com.onyx.android.sun.requests.cloud.GetNewMessageRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

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
