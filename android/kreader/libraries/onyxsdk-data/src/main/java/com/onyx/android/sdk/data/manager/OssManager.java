package com.onyx.android.sdk.data.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.dataprovider.BuildConfig;
import com.onyx.android.sdk.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wang.suicheng on 2017/1/21.
 */
public class OssManager {

    private OSS ossClient;
    private OssConfig ossConfig;

    private Handler handler = new Handler(Looper.getMainLooper());
    private static Map<OssWrapRequest, BaseCallback.ProgressInfo> progressMap = new HashMap<>();

    public OssManager(Context context, @NonNull OssConfig config) {
        ossClient = buildOssClient(context, config);
        setOssConfig(config);
    }

    private void setOssConfig(OssConfig config) {
        ossConfig = new OssConfig();
        ossConfig.setBucketName(config.getBucketName());
        ossConfig.setEndPoint(config.getEndPoint());
    }

    static private OSSClient buildOssClient(Context context, OssConfig ossConfig) {
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(ossConfig.getKeyId(), ossConfig.getKeySecret());
        return new OSSClient(context.getApplicationContext(), ossConfig.getEndPoint(), credentialProvider);
    }

    public OSS getOssClient() {
        return ossClient;
    }

    public OssConfig getOssConfig() {
        return ossConfig;
    }

    public String getOssBucketName() {
        return ossConfig.getBucketName();
    }

    public String getOssEndPoint() {
        return ossConfig.getEndPoint();
    }

    public String getOssFileObjectKey(String fileName) {
        String objectKey = UUID.randomUUID().toString().replaceAll("-", "") + "." + FileUtils.getFileExtension(fileName);
        Log.d("oss-objectKey", String.valueOf(objectKey));
        return objectKey;
    }

    private PutObjectRequest getPutObjectRequest(String bucketName, String uploadFilePath) {
        return new PutObjectRequest(
                bucketName,
                getOssFileObjectKey(uploadFilePath),
                uploadFilePath);
    }

    public void asyncUploadFile(Context context, String uploadFilePath, final BaseCallback callback) {
        asyncUploadFile(context, getOssBucketName(), uploadFilePath, callback);
    }

    public void asyncUploadFile(Context context, String bucketName, String uploadFilePath, final BaseCallback callback) {
        OSS oss = getOssClient();
        PutObjectRequest putRequest = getPutObjectRequest(bucketName, uploadFilePath);
        final OssWrapRequest wrapRequest = new OssWrapRequest<>(putRequest);

        putRequest.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
                reportProgress(wrapRequest, callback, currentSize, totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(putRequest, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                reportTaskDone(wrapRequest, callback, null);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                Exception exception = clientException;
                if (clientException != null) {
                    clientException.printStackTrace();
                }
                if (serviceException != null) {
                    dumpServiceException(serviceException);
                    exception = serviceException;
                }
                reportTaskDone(wrapRequest, callback, exception);
            }
        });
    }

    public String syncUploadFile(Context context, String uploadFilePath) throws Exception {
        return syncUploadFile(context, getOssBucketName(), uploadFilePath);
    }

    public String syncUploadFile(Context context, String bucketName, String uploadFilePath) throws Exception {
        PutObjectRequest putRequest = getPutObjectRequest(bucketName, uploadFilePath);
        PutObjectResult putResult = getOssClient().putObject(putRequest);
        if (putResult.getStatusCode() == 200) {
            return putRequest.getObjectKey();
        } else {
            return null;
        }
    }

    private BaseCallback.ProgressInfo getProgressInfo(OssWrapRequest request) {
        if (!progressMap.containsKey(request)) {
            BaseCallback.ProgressInfo progressInfo = new BaseCallback.ProgressInfo();
            progressMap.put(request, progressInfo);
        }
        return progressMap.get(request);
    }

    private void removeProgressInfo(OssWrapRequest request) {
        progressMap.remove(request);
    }

    private void reportProgress(final OssWrapRequest ossRequest, final BaseCallback callback, long currentSize, long totalSize) {
        dumpProgress(currentSize, totalSize);
        if (callback != null) {
            final BaseCallback.ProgressInfo progressInfo = getProgressInfo(ossRequest);
            progressInfo.soFarBytes = currentSize;
            progressInfo.totalBytes = totalSize;
            progressInfo.progress = currentSize * 1.0f / totalSize * 100;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.progress(ossRequest, progressInfo);
                }
            });
        }
    }

    private void reportTaskDone(final OssWrapRequest ossRequest, final BaseCallback callback, final Exception exception) {
        removeProgressInfo(ossRequest);
        handler.post(new Runnable() {
            @Override
            public void run() {
                BaseCallback.invoke(callback, ossRequest, exception);
            }
        });
    }

    private void dumpProgress(long currentSize, long totalSize) {
        if (BuildConfig.DEBUG) {
            Log.i("onProgress", "currentSize: " + currentSize + " totalSize: " + totalSize);
        }
    }

    private void dumpServiceException(ServiceException serviceException) {
        if (BuildConfig.DEBUG && serviceException != null) {
            Log.i("ErrorCode", serviceException.getErrorCode());
            Log.i("RequestId", serviceException.getRequestId());
            Log.i("HostId", serviceException.getHostId());
            Log.i("RawMessage", serviceException.getRawMessage());
        }
    }

    public static class OssWrapRequest<T extends OSSRequest> extends BaseCloudRequest {
        private T request;

        public OssWrapRequest(T request) {
            this.request = request;
        }

        public T getRequest() {
            return request;
        }

        @Override
        public void execute(CloudManager parent) throws Exception {
        }
    }

    public static class OssConfig {
        private String keyId;
        private String keySecret;
        private String endPoint;
        private String bucketName;

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getKeySecret() {
            return keySecret;
        }

        public void setKeySecret(String keySecret) {
            this.keySecret = keySecret;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }
    }
}
