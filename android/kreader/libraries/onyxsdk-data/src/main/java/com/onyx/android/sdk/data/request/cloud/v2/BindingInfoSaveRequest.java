package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.InstallationIdBinding;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/8/4.
 */
public class BindingInfoSaveRequest extends BaseCloudRequest {

    private InstallationIdBinding infoBinding;
    private boolean resultSuccess;

    public BindingInfoSaveRequest(InstallationIdBinding binding) {
        this.infoBinding = binding;
    }

    public boolean isResultSuccess() {
        return resultSuccess;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        bindInstallationIdByMac(getContext(), parent);
    }

    private void bindInstallationIdByMac(Context context, CloudManager parent) throws Exception {
        if (!NetworkUtil.isWiFiConnected(context) || !infoBinding.checkBindingInfoValid()) {
            return;
        }
        String macAddress = NetworkUtil.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(macAddress)) {
            return;
        }
        Response<ResponseBody> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .updateInstallationIdByMac(infoBinding));
        resultSuccess = response.isSuccessful();
    }
}
