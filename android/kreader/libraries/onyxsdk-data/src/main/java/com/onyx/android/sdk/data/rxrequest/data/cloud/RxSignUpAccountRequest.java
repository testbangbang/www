package com.onyx.android.sdk.data.rxrequest.data.cloud;

import android.util.Log;

import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseAccountRequest;

import retrofit2.Response;

/**
 * Created by jackdeng on 2017/11/15.
 */

public class RxSignUpAccountRequest extends RxBaseAccountRequest {
    private OnyxAccount account;
    private OnyxAccount result;

    public RxSignUpAccountRequest(OnyxAccount account) {
        this.account = account;
    }

    @Override
    public RxSignUpAccountRequest call() throws Exception {
        try {
            Response<OnyxAccount> response = getService().signup(account).execute();
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