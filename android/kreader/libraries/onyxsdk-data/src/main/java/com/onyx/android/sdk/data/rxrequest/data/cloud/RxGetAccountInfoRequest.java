package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseAccountRequest;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/15.
 */

public class RxGetAccountInfoRequest extends RxBaseAccountRequest {
    private String sessionToken;
    private OnyxAccount result;

    public RxGetAccountInfoRequest(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public RxGetAccountInfoRequest call() throws Exception {
        try {
            Response<OnyxAccount> response = getService().getAccountInfo(sessionToken).execute();
            if (response != null && response.isSuccessful()) {
                result = response.body();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
        return this;
    }

    public OnyxAccount getResult() {
        return result;
    }
}