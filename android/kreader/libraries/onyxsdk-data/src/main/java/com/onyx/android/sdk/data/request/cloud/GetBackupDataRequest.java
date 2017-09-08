package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.CloudBackupData;
import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

import retrofit2.Response;

/**
 * Created by ming on 2017/7/22.
 */

public class GetBackupDataRequest extends BaseCloudRequest {

    private CloudBackupData cloudBackupData;

    @Override
    public void execute(CloudManager parent) throws Exception {
        String mac = NetworkUtil.getMacAddress(getContext());
        String packageName = DeviceUtils.getPackageName(getContext());

        Response<CloudBackupData> responseCall = executeCall(ServiceFactory.getBackupService(parent.getCloudConf().getApiBase()).getBackupDatas(packageName, mac));
        if (responseCall.isSuccessful()) {
            cloudBackupData = responseCall.body();
        }else {
            throw new Exception(responseCall.message());
        }
    }

    public CloudBackupData getCloudBackupData() {
        return cloudBackupData;
    }
}
