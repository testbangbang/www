package com.onyx.android.sdk.data.action.push;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.manager.PushActionManager;;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.PushProductEvent;
import com.onyx.android.sdk.data.request.cloud.v2.PushProductSaveRequest;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by suicheng on 2017/8/3.
 */
public class PushProductDownloadAction {

    private PushProductEvent pushProduct;
    private boolean md5CheckSuccess;

    public PushProductDownloadAction(PushProductEvent pushProduct) {
        this.pushProduct = pushProduct;
    }

    public boolean isMd5CheckSuccess() {
        return md5CheckSuccess;
    }

    public void execute(final ActionContext actionContext, final BaseCallback baseCallback) {
        if (pushProduct == null || pushProduct.metadata == null) {
            return;
        }
        Metadata metadata = pushProduct.metadata;
        String filePath = getPushDownloadFilePath(actionContext.context, pushProduct);
        String url = metadata.getLocation();
        metadata.setNativeAbsolutePath(filePath);
        final DownloadAction downloadAction = new DownloadAction(metadata.getLocation(), filePath, url, metadata.getHashTag());
        downloadAction.execute(actionContext, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                processMd5Result(downloadAction);
                saveToDatabase(actionContext, pushProduct);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void processMd5Result(DownloadAction action) {
        this.md5CheckSuccess = action.isMd5CheckSuccess();
    }

    private String getPushDownloadFilePath(Context context, PushProductEvent pushProduct) {
        String fileName = FileUtils.fixNotAllowFileName(pushProduct.getFileNameWithExtension());
        if (StringUtils.isNullOrEmpty(fileName)) {
            return null;
        }
        String dirId = pushProduct.metadata.getAssociationId();
        if (StringUtils.isNullOrEmpty(dirId)) {
            dirId = UUID.randomUUID().toString().replace("-", "");
        }
        return new File(CloudUtils.dataCacheDirectory(context, dirId), fileName).getAbsolutePath();
    }

    private void saveToDatabase(ActionContext actionContext, PushProductEvent pushProduct) {
        BaseData.asyncSave(pushProduct);
        PushProductSaveRequest saveRequest = new PushProductSaveRequest(pushProduct);
        actionContext.cloudManager.submitRequest(actionContext.context, saveRequest, null);
    }
}
