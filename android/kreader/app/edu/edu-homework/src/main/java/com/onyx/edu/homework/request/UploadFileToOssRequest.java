package com.onyx.edu.homework.request;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.homework.utils.OssManagerUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/3/19 14:58
 *     desc   :
 * </pre>
 */

public class UploadFileToOssRequest extends BaseCloudRequest {

    @NonNull
    public String uploadFileUrl;
    private String uploadFilePath;

    public UploadFileToOssRequest(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (!FileUtils.fileExist(uploadFilePath)) {
            throw new FileNotFoundException();
        }

        String objectKey = OssManagerUtils.getOssManager().syncUploadFile(getContext(), uploadFilePath);
        if (StringUtils.isNullOrEmpty(objectKey)) {
           throw new RuntimeException("oss upload failure");
        }

        uploadFileUrl = OssManagerUtils.getOssManager().getOssEndPoint() + File.separator + objectKey;
    }
}
