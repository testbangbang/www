package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class AccountInfoRequest extends BaseCloudRequest {

    private OnyxAccount account;

    public AccountInfoRequest() {
    }

    public OnyxAccount getAccount() {
        return account;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Call<OnyxAccount> call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .getAccountInfo(getAccountSessionToken());
        Response<OnyxAccount> response = call.execute();
        if (response.isSuccessful()) {
            account = response.body();
            account.sessionToken = getAccountSessionToken();
        } else {
            String errorCode = JSONObjectParseUtils.httpStatus(response.code(), new JSONObject(response.errorBody().string()));
            throw new Exception(errorCode);
        }
    }
}
