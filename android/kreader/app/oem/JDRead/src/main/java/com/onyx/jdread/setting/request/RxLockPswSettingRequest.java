package com.onyx.jdread.setting.request;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.setting.model.PswSettingData;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Response;

/**
 * Created by suicheng on 2018/2/7.
 */
public class RxLockPswSettingRequest extends RxBaseCloudRequest {

    private PswSettingData data;
    private boolean succeed;

    public boolean isSucceed() {
        return this.succeed;
    }

    public RxLockPswSettingRequest(PswSettingData data) {
        this.data = data;
    }

    @Override
    public RxLockPswSettingRequest call() throws Exception {
        succeed = passwordSaveToCloud();
        if (succeed) {
            passwordSaveToLocal();
        }
        return this;
    }

    private void passwordSaveToLocal() {
        JDPreferenceManager.setStringValue(R.string.phone_key, data.phone);
        JDPreferenceManager.setStringValue(R.string.password_key, FileUtils.computeMD5(data.password));
    }

    private boolean passwordSaveToCloud() throws Exception {
        Response response = RetrofitUtils.executeCall(CloudApiContext.getOnyxService(
                CloudApiContext.ONYX_EINK_API).passwordSetting(data));
        return response.isSuccessful();
    }
}
