package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.request.cloud.SaveDocumentDataToCloudRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/6/10.
 */

public class SaveDocumentDataToCloudAction extends BaseAction {

    private StringBuffer exportDBPath;
    private StringBuffer fileFullMd5;
    private StringBuffer token;

    private String errorMessage;

    public SaveDocumentDataToCloudAction(StringBuffer exportDBPath, StringBuffer fileFullMd5, StringBuffer token) {
        this.exportDBPath = exportDBPath;
        this.fileFullMd5 = fileFullMd5;
        this.token = token;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final SaveDocumentDataToCloudRequest saveDocumentDataToCloudRequest = new SaveDocumentDataToCloudRequest(exportDBPath.toString(),
                readerDataHolder.getContext(),
                fileFullMd5.toString(),
                readerDataHolder.getCloudDocId(),
                token.toString());
        readerDataHolder.getCloudManager().submitRequest(readerDataHolder.getContext(), saveDocumentDataToCloudRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                errorMessage = saveDocumentDataToCloudRequest.getErrorMessage();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static SaveDocumentDataToCloudAction create(StringBuffer exportDBPath, StringBuffer fileFullMd5, StringBuffer token) {
        return new SaveDocumentDataToCloudAction(exportDBPath, fileFullMd5, token);
    }
}
