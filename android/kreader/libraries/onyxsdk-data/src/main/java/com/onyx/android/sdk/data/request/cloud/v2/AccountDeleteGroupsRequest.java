package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/8/14.
 */
public class AccountDeleteGroupsRequest extends BaseCloudRequest {

    private NeoAccountBase account;
    private List<String> groupIdList;

    private boolean result;

    public AccountDeleteGroupsRequest(NeoAccountBase account, List<String> groupIdList) {
        this.account = account;
        this.groupIdList = groupIdList;
    }

    public boolean isSuccessful() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        ArrayList<String> list = new ArrayList<>(groupIdList);
        Response<ResponseBody> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).deleteGroupsFromUser(account._id, list));
        result = response.isSuccessful();
    }
}
