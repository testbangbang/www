package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/6/16.
 */
public class CloudGroupUserListRequest extends BaseCloudRequest {

    private String groupId;
    private List<NeoAccountBase> groupUserList;

    public CloudGroupUserListRequest(String groupId) {
        this.groupId = groupId;
    }

    public List<NeoAccountBase> getGroupUserList() {
        return groupUserList;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<NeoAccountBase>> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(
                parent.getCloudConf().getApiBase()).getGroupUserList(groupId));
        if (response.isSuccessful()) {
            groupUserList = response.body();
            if (CollectionUtils.isNullOrEmpty(groupUserList)) {
                return;
            }
            for (NeoAccountBase accountBase : groupUserList) {
                NeoAccountBase.parseInfo(accountBase);
            }
        }
    }
}
