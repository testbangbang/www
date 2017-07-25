package com.onyx.einfo.action;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupListRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.einfo.holder.LibraryDataHolder;

import java.util.List;

/**
 * Created by suicheng on 2017/7/22.
 */

public class CloudGroupListLoadAction extends BaseAction<LibraryDataHolder> {

    private List<CloudGroup> cloudGroupList;

    public List<CloudGroup> getCloudGroupList() {
        return cloudGroupList;
    }

    public CloudGroup getFirstCloudGroup() {
        if (CollectionUtils.isNullOrEmpty(cloudGroupList)) {
            return null;
        }
        return cloudGroupList.get(0);
    }

    @Override
    public void execute(LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final CloudGroupListRequest groupListRequest = new CloudGroupListRequest();
        dataHolder.getCloudManager().submitRequest(dataHolder.getContext(), groupListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                cloudGroupList = groupListRequest.getMyGroupList();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
