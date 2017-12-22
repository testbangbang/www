package com.onyx.android.sdk.data.request.cloud.v2;

import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.common.DeviceInfoShowConfig;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.StringUtils;


/**
 * Created by zhuzeng on 03/06/2017.
 */

public class GenerateAccountInfoRequest extends BaseCloudRequest {
    private NeoAccountBase authAccount;
    private DeviceInfoShowConfig infoShowConfig;

    public GenerateAccountInfoRequest(NeoAccountBase authAccount, DeviceInfoShowConfig infoShowConfig) {
        this.authAccount = authAccount;
        this.infoShowConfig = infoShowConfig;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        boolean needRotation = true;
        int rotationAngle = infoShowConfig == null ? 90 : infoShowConfig.rotationAngle;
        String targetString = authAccount.getNameAppendRole() + " " + authAccount.getFirstGroup();
        Bitmap bitmap = BitmapUtils.buildBitmapFromText(StringUtils.getBlankStr(targetString).trim(),
                100, 25, true,
                true, true, needRotation, rotationAngle,
                "data/local/assets/info.png");
        if (infoShowConfig == null || bitmap == null) {
            Log.e("GenerateAccountInfo", "qrShowConfig or bitmap detects null");
            return;
        }
        int textWidth = needRotation ? bitmap.getHeight() : bitmap.getWidth();
        Device.currentDevice().setInfoShowConfig(infoShowConfig.orientation,
                infoShowConfig.startX,
                infoShowConfig.startY + (infoShowConfig.isScreenPositive() ? -textWidth : 0));
    }

}