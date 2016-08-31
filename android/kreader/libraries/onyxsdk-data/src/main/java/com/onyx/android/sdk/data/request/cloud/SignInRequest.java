package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;

import com.onyx.android.sdk.data.v1.ServiceFactory;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/21/15.
 */
public class SignInRequest extends BaseCloudRequest {
    private static final String TAG = SignInRequest.class.getSimpleName();
    private OnyxAccount account;
    private OnyxAccount accountSignIn;

    public SignInRequest(final OnyxAccount value) {
        account = value;
    }

    public final OnyxAccount getAccountSignIn() {
        return accountSignIn;
    }

    public void execute(final CloudManager parent) throws Exception {
        Call<ResponseBody> call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .signin(account);
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
            accountSignIn = JSONObjectParseUtils.parseOnyxAccount(response.body().string());
        } else {
            String errorCode = JSONObjectParseUtils.httpStatus(response.code(), new JSONObject(response.errorBody().string()));
            throw new Exception(errorCode);
        }
    }

}
