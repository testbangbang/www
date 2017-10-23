package com.onyx.android.sun.data;

import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.requests.GetNewMessageRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

/**
 * Created by jackdeng on 2017/10/20.
 */

public class MainActData {
    public void getNewMessage(GetNewMessageRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstence(), rq, callback);
    }
}
