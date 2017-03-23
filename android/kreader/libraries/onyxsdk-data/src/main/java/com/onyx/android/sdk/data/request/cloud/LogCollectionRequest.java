package com.onyx.android.sdk.data.request.cloud;

import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceInfoUtil;
import com.onyx.android.sdk.utils.LogUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/3/11.
 */
public class LogCollectionRequest extends BaseCloudRequest {

    private String uploadFileUrl;
    private OssManager ossManager;
    private String desc;

    public LogCollectionRequest(@NonNull OssManager ossManager, String desc) {
        this.ossManager = ossManager;
        this.desc = desc;
    }

    public String getUploadFileUrl() {
        return uploadFileUrl;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        File zipFile = LogUtils.generateFeedBackFile(getContext());
        if (zipFile == null || !zipFile.exists()) {
            throw new FileNotFoundException();
        }
        try {
            String objectKey = ossManager.syncUploadFile(getContext(), zipFile.getAbsolutePath());
            if (StringUtils.isNullOrEmpty(objectKey)) {
                return;
            }
            reportFeedbackInfo(parent, objectKey);
        } catch (Exception e) {
            setException(e);
        } finally {
            zipFile.delete();
        }
    }

    private void reportFeedbackInfo(CloudManager parent, String objectKey) throws Exception {
        String fileUrl = ossManager.getOssEndPoint() + File.separator + objectKey;
        Firmware firmware = getCurrentFirmware();
        LogCollection logCollection = new LogCollection();
        logCollection.firmware = firmware;
        logCollection.desc = desc;
        logCollection.zipFile = fileUrl;
        Response<ResponseBody> response = ServiceFactory.getLogService(parent.getCloudConf().getApiBase())
                .reportLogCollection(logCollection).execute();
        if (response.isSuccessful()) {
            uploadFileUrl = fileUrl;
        }
    }

    private Firmware getCurrentFirmware() {
        Point point = DeviceInfoUtil.getScreenResolution(getContext());
        Firmware firmware = Firmware.currentFirmware();
        firmware.buildDisplayId = Build.DISPLAY;
        firmware.lang = Locale.getDefault().toString();
        firmware.widthPixels = point.x;
        firmware.heightPixels = point.y;
        Device device = Device.updateCurrentDeviceInfo(getContext());
        if (device != null) {
            firmware.deviceMAC = device.macAddress;
        }
        return firmware;
    }
}
