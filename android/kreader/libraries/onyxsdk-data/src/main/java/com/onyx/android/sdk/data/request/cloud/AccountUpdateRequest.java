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
public class AccountUpdateRequest extends BaseCloudRequest {

    private OnyxAccount updateAccount;

    public AccountUpdateRequest(OnyxAccount updateAccount) {
        this.updateAccount = updateAccount;
    }

    public OnyxAccount getUpdateAccount() {
        return updateAccount;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(updateAccount.email)) {
            throw new Exception("email is blank");
        }

        Call<OnyxAccount> call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .updateAccountInfo(updateAccount, getAccountSessionToken());
        Response<OnyxAccount> response = executeCall(call);
        if (response.isSuccessful()) {
            updateAccount = response.body();
            updateAccount.sessionToken = getAccountSessionToken();
        }
    }
}
