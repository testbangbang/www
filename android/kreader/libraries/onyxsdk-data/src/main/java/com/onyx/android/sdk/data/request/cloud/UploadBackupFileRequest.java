package com.onyx.android.sdk.data.request.cloud;

import android.os.Handler;
import android.os.Looper;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.utils.ProgressRequestBody;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by ming on 2017/7/22.
 */

public class UploadBackupFileRequest extends BaseCloudRequest {

    private String backupFilePath;

    public UploadBackupFileRequest(String backupFilePath) {
        this.backupFilePath = backupFilePath;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        File dbFile = new File(backupFilePath);
        String md5 = FileUtils.computeFullMD5Checksum(dbFile);
        String name = DeviceUtils.getApplicationName(getContext());
        String packageName = DeviceUtils.getPackageName(getContext());
        String version = DeviceUtils.getPackageVersionName(getContext());
        String mac = NetworkUtil.getMacAddress(getContext());

        ProgressRequestBody requestBody = new ProgressRequestBody(dbFile, "form-data", new ProgressRequestBody.UploadCallbacks() {
            @Override
            public void onProgressUpdate(int percentage) {
                BaseCallback.ProgressInfo progressInfo = new BaseCallback.ProgressInfo();
                progressInfo.progress = percentage;
                BaseCallback.invokeProgress(parent.getRequestManager().getLooperHandler(), getCallback(), UploadBackupFileRequest.this, progressInfo);
            }

            @Override
            public void onError() {

            }

            @Override
            public void onFinish() {

            }
        });
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData(Constant.FILE_TAG, dbFile.getName(), requestBody);
        MultipartBody.Part md5Body = MultipartBody.Part.createFormData(Constant.MD5_TAG, md5);
        MultipartBody.Part nameBody = MultipartBody.Part.createFormData(Constant.NAME_TAG, name);
        MultipartBody.Part packageBody = MultipartBody.Part.createFormData(Constant.PACKAGE_TAG, packageName);
        MultipartBody.Part versionBody = MultipartBody.Part.createFormData(Constant.VERSION_TAG, version);
        MultipartBody.Part macBody = MultipartBody.Part.createFormData(Constant.MAC_TAG, mac);

        Response<JsonRespone> responseCall = executeCall(ServiceFactory.getBackupService(parent.getCloudConf().getApiBase()).uploadBackupFile(fileBody,
                nameBody,
                packageBody,
                versionBody,
                md5Body,
                macBody));

        if (responseCall != null && !responseCall.isSuccessful()) {
            throw new Exception(responseCall.message());
        }
    }
}
