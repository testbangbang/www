package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;

import com.onyx.android.sdk.data.v1.ServiceFactory;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/21/15.
 */
public class AccountSignUpRequest extends BaseCloudRequest {
    private static final String TAG = AccountSignUpRequest.class.getSimpleName();
    private OnyxAccount account;
    private OnyxAccount accountSignUp;

    public AccountSignUpRequest(final OnyxAccount value) {
        account = value;
    }

    public final OnyxAccount getAccountSignUp() {
        return accountSignUp;
    }

    public void execute(final CloudManager parent) throws Exception {
        Call<OnyxAccount> call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .signup(account);
        Response<OnyxAccount> response = executeCall(call);
        if (response.isSuccessful()) {
            accountSignUp = response.body();
        }
    }

}
