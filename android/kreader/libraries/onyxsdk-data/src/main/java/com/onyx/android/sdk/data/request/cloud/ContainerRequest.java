package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.v1.OnyxBookStoreService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class ContainerRequest extends BaseCloudRequest {

    private Category category;
    private String id;

    public ContainerRequest(String categoryId) {
        id = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(id)) {
            throw new Exception("categoryId is blank");
        }
        fetchFromCloud(parent);
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        OnyxBookStoreService service = ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase());
        Response<Category> response = executeCall(service.bookContainer(id));
        if (response.isSuccessful()) {
            category = response.body();
        }
    }
}
