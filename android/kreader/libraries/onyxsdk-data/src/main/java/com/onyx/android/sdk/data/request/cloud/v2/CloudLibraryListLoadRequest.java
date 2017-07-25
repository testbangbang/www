package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.util.List;

/**
 * Created by suicheng on 2017/5/18.
 */
public class CloudLibraryListLoadRequest extends BaseCloudRequest {

    private String parentId;
    private QueryArgs queryArgs = new QueryArgs();

    private List<Library> libraryList;

    public CloudLibraryListLoadRequest() {
    }

    public CloudLibraryListLoadRequest(String parentId) {
        this.parentId = parentId;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        queryArgs.cloudToken = parent.getToken();
        queryArgs.libraryUniqueId = parentId;
        libraryList = DataManagerHelper.fetchLibraryLibraryList(getContext(), parent.getCloudDataProvider(), queryArgs);
    }

    public QueryArgs getQueryArgs() {
        return queryArgs;
    }
}
