package com.onyx.cloud.store.request;

import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.DownloadLink;
import com.onyx.cloud.service.v1.ServiceFactory;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/27/15.
 */
public class QueryDownloadLink extends BaseCloudRequest {

    private List<DownloadLink> downloadLinkList;
    private String objectId;

    public QueryDownloadLink(final String id) {
        objectId = id;
    }

    public final List<DownloadLink> getDownloadLinkList() {
        return downloadLinkList;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        fetchFromCloud(parent);
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        Call<List<DownloadLink>> call = ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .bookDownloadLink(objectId);
        Response<List<DownloadLink>> response = call.execute();
        if (response.isSuccessful()) {
            downloadLinkList = response.body();
        }
    }
}
