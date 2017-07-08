package com.onyx.android.sdk.data.request.cloud.v2;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.AdminApplyModel;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/7/6.
 */
public class AdministratorApplyRequest extends BaseCloudRequest {

    private AdminApplyModel requestApply;
    private boolean applyResult = false;

    public AdministratorApplyRequest(AdminApplyModel applyModel) {
        this.requestApply = applyModel;
    }

    public boolean isApplySuccess() {
        return applyResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ResponseBody> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .applyAdminRequest(requestApply));
        applyResult = response.isSuccessful();
    }
}
