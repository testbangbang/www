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

        AccountLoadFromLocalAction accountLoadFromLocalAction = accountLoadFromLocalAction();
        GetFileFullMd5Action getFileFullMd5Action = getFileFullMd5Action(readerDataHolder);
        ExportNoteDataAction exportNoteDataAction = exportNoteDataAction(getFileFullMd5Action.getFullFileMd5());
        final SaveDocumentDataToCloudAction saveDocumentDataToCloudAction = saveDocumentDataToCloud(exportNoteDataAction.getExportDBFilePath(),
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

    private AccountLoadFromLocalAction accountLoadFromLocalAction() {
        return new AccountLoadFromLocalAction();
    }

    private GetFileFullMd5Action getFileFullMd5Action(final ReaderDataHolder readerDataHolder) {
        return new GetFileFullMd5Action(readerDataHolder.getDocumentPath());
    }

    private ExportNoteDataAction exportNoteDataAction(final StringBuffer fullFileMd5) {
        return new ExportNoteDataAction(fullFileMd5);
    }

    private SaveDocumentDataToCloudAction saveDocumentDataToCloud(StringBuffer exportDBFilePath, StringBuffer fileFullMd5, StringBuffer token) {
        return new SaveDocumentDataToCloudAction(exportDBFilePath,
                fileFullMd5,
                token);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
