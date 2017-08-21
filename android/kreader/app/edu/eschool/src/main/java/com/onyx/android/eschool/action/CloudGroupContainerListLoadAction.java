package com.onyx.android.eschool.action;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.GroupContainer;
import com.onyx.android.sdk.data.request.cloud.v2.CloudGroupContainerListRequest;
import com.onyx.android.eschool.holder.LibraryDataHolder;

import java.util.List;

/**
 * Created by suicheng on 2017/7/22.
 */
public class CloudGroupContainerListLoadAction extends BaseAction<LibraryDataHolder> {

    private List<GroupContainer> containerList;

    public List<GroupContainer> getContainerList() {
        return containerList;
    }

    @Override
    public void execute(final LibraryDataHolder dataHolder, final BaseCallback baseCallback) {
        final CloudGroupContainerListRequest groupListRequest = new CloudGroupContainerListRequest();
        dataHolder.getCloudManager().submitRequest(dataHolder.getContext(), groupListRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                containerList = groupListRequest.getContainerList();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
