package com.onyx.android.update.log.request;

import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.manager.OssManager;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceInfoUtil;
import com.onyx.android.sdk.utils.LogUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2018/1/29.
 */

public class RxLogFeedbackRequest extends RxBaseCloudRequest {

    public static final String LOG_REPORT_API = "http://47.90.54.36:8000/api/";

    private String logReportApi = LOG_REPORT_API;

    private OssManager ossManager;
    public String filePath;

    private String uploadFileUrl;

    public RxLogFeedbackRequest(@NonNull OssManager ossManager, @NonNull String filePath) {
        this.ossManager = ossManager;
        this.filePath = filePath;
    }

    public String getUploadFileUrl() {
        return uploadFileUrl;
    }

    public void setLogReportApi(String api) {
        this.logReportApi = api;
    }

    @Override
    public RxLogFeedbackRequest call() throws Exception {
        boolean success = startReport();
        if (success) {
            LogUtils.deleteFiles(new File[]{new File(getDirPath())});
        }
        return this;
    }

    private boolean startReport() throws Exception {
        String objectKey = ossManager.syncUploadFile(getAppContext(), filePath);
        if (StringUtils.isNullOrEmpty(objectKey)) {
            return false;
        }
        return reportFeedbackInfo(objectKey);
    }

    private String getDirPath() {
        return new File(filePath).getParent();
    }

    private String getDescString() {
        String desc = null;
        File descFile = LogUtils.getDescFile(getDirPath());
        if (descFile == null) {
            return null;
        }
        try {
            desc = FileUtils.readFileToString(descFile, Charset.defaultCharset());
        } catch (IOException ignored) {
        }
        return desc;
    }

    private LogCollection getLogCollection() {
        LogCollection logCollection = JSONObjectParseUtils.parseObject(getDescString(), LogCollection.class);
        if (logCollection == null) {
            logCollection = new LogCollection();
        }
        return logCollection;
    }

    private boolean reportFeedbackInfo(String objectKey) throws Exception {
        String fileUrl = ossManager.getOssEndPoint() + File.separator + objectKey;
        LogCollection logCollection = getLogCollection();
        if (logCollection.firmware == null) {
            logCollection.firmware = getCurrentFirmware();
        }
        logCollection.zipFile = fileUrl;
        Response<ResponseBody> response = ServiceFactory.getLogService(logReportApi)
                .reportLogCollection(logCollection).execute();
        if (response.isSuccessful()) {
            uploadFileUrl = fileUrl;
            return true;
        }
        return false;
    }

    private Firmware getCurrentFirmware() {
        Point point = DeviceInfoUtil.getScreenResolution(getAppContext());
        Firmware firmware = Firmware.currentFirmware();
        firmware.buildDisplayId = Build.DISPLAY;
        firmware.lang = Locale.getDefault().toString();
        firmware.widthPixels = point.x;
        firmware.heightPixels = point.y;
        Device device = Device.updateCurrentDeviceInfo(getAppContext());
        if (device != null) {
            firmware.deviceMAC = device.macAddress;
        }
        return firmware;
    }
}
