package com.onyx.android.eschool.request;

import com.onyx.android.eschool.manager.PushMessageHandler;
import com.onyx.android.eschool.model.MessageInfo;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

import java.util.List;

/**
 * Created by suicheng on 2018/1/29.
 */
public class PushMessageFetchRequest extends BaseCloudRequest {

    private PushMessageHandler pushHandler;
    public List<MessageInfo> list;

    public PushMessageFetchRequest(PushMessageHandler handler) {
        this.pushHandler = handler;
    }

    public List<MessageInfo> geMessageInfoList() {
        return list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        list = pushHandler.getAndRemoveMessageInfoList();
    }
}
