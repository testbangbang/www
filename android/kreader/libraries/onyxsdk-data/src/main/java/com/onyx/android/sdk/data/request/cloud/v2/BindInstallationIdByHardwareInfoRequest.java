package com.onyx.android.sdk.data.request.cloud.v2;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.InstallationIdBinding;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/8/4.
 */
public class BindInstallationIdByHardwareInfoRequest extends BaseCloudRequest {

    private Map<String, String> installationIdMap;
    private boolean resultSuccess;

    public BindInstallationIdByHardwareInfoRequest(Map<String, String> installationIdMap) {
        this.installationIdMap = installationIdMap;
    }

    public boolean isResultSuccess() {
        return resultSuccess;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        bindInstallationIdByMac(getContext(), parent);
    }

    private void bindInstallationIdByMac(Context context, CloudManager parent) throws Exception {
        if (!NetworkUtil.isWiFiConnected(context) || CollectionUtils.isNullOrEmpty(installationIdMap)) {
            return;
        }
        String macAddress = NetworkUtil.getMacAddress(context);
        if (StringUtils.isNullOrEmpty(macAddress)) {
            return;
        }
        InstallationIdBinding binding = new InstallationIdBinding();
        binding.installationMap = installationIdMap;
        binding.mac = macAddress;
        Response<ResponseBody> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .updateInstallationIdByMac(binding));
        resultSuccess = response.isSuccessful();
    }
}
