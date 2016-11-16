package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.PushRecord;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import org.json.JSONObject;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/10/14.
 */

public class PushMessageRequest extends BaseCloudRequest {

    private String jsonString;
    private PushRecord record;

    public PushMessageRequest(String json) {
        this.jsonString = json;
    }

    public PushRecord getResult() {
        return record;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        JSONObject object = new JSONObject(jsonString);
        Response<PushRecord> response = executeCall(ServiceFactory.getPushService(parent.getCloudConf().getApiBase())
                .pushMessage(object, getAccountSessionToken()));
        if (response.isSuccessful()) {
            record = response.body();
        }
    }
}
