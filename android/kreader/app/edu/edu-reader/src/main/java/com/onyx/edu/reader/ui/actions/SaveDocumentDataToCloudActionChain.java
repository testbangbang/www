package com.onyx.edu.reader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/5/31.
 */

public class SaveDocumentDataToCloudActionChain extends BaseAction {

    private String errorMessage;

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {

        ActionChain actionChain = new ActionChain();

        AccountLoadFromLocalAction accountLoadFromLocalAction = AccountLoadFromLocalAction.create();
        GetFileFullMd5Action getFileFullMd5Action = GetFileFullMd5Action.create(readerDataHolder.getDocumentPath());
        ExportNoteDataAction exportNoteDataAction = ExportNoteDataAction.create(getFileFullMd5Action.getFullFileMd5());
        final SaveDocumentDataToCloudAction saveDocumentDataToCloudAction = SaveDocumentDataToCloudAction.create(exportNoteDataAction.getExportDBFilePath(),
                getFileFullMd5Action.getFullFileMd5(),
                accountLoadFromLocalAction.getToken());

        actionChain.addAction(accountLoadFromLocalAction);
        actionChain.addAction(getFileFullMd5Action);
        actionChain.addAction(exportNoteDataAction);
        actionChain.addAction(saveDocumentDataToCloudAction);
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                errorMessage = saveDocumentDataToCloudAction.getErrorMessage();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
