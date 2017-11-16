package com.onyx.android.plato.data;

import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.requests.local.RecorderRequest;
import com.onyx.android.plato.requests.local.SpeakRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

/**
 * Created by li on 2017/10/26.
 */

public class ParseAnswerData {
    public void startRecord(RecorderRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }

    public void speakRecord(SpeakRequest rq, BaseCallback callback) {
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, callback);
    }
}
