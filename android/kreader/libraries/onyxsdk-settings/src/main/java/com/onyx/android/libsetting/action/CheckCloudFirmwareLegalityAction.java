package com.onyx.android.libsetting.action;

import android.content.Context;

import com.onyx.android.libsetting.SettingManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;

/**
 * Created by suicheng on 2017/3/10.
 */
public class CheckCloudFirmwareLegalityAction extends BaseSettingAction {

    private Firmware firmware;

    public CheckCloudFirmwareLegalityAction(Firmware firmware) {
        this.firmware = firmware;
    }

    @Override
    public void execute(Context context, SettingManager manager, final BaseCallback callback) {
        FirmwareUpdateRequest updateRequest = new FirmwareUpdateRequest(firmware);
        SettingManager.sharedInstance().submitCloudRequest(context, updateRequest, callback);
    }
}
