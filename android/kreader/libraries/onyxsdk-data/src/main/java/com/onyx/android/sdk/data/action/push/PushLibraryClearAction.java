package com.onyx.android.sdk.data.action.push;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.model.v2.PushLibraryClearEvent;
import com.onyx.android.sdk.data.request.cloud.v2.PushLibraryClearRequest;

/**
 * Created by suicheng on 2017/8/3.
 */
public class PushLibraryClearAction {

    private PushLibraryClearEvent libraryClear;

    public PushLibraryClearAction(PushLibraryClearEvent libraryClear) {
        this.libraryClear = libraryClear;
    }

    public void execute(final ActionContext actionContext, final BaseCallback baseCallback) {
        PushLibraryClearRequest libraryClearRequest = new PushLibraryClearRequest(libraryClear);
        actionContext.cloudManager.submitRequest(actionContext.context, libraryClearRequest, baseCallback);
    }
}
