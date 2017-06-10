package com.onyx.edu.reader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/5/31.
 */

public class ExportDocumentDataActionChain extends BaseAction {

    private String errorMessage;

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {

        ActionChain actionChain = new ActionChain();

        AccountLoadFromLocalAction accountLoadFromLocalAction = accountLoadFromLocalAction(readerDataHolder);
        GetFileFullMd5Action getFileFullMd5Action = getFileFullMd5(readerDataHolder);
        ExportNoteDataAction exportNoteDataAction = exportNoteData(readerDataHolder, getFileFullMd5Action.getFullFileMd5());
        final SaveDocumentDataToCloudAction saveDocumentDataToCloudAction = SaveDocumentDataToCloud(readerDataHolder, exportNoteDataAction.getExportDBFilePath(), getFileFullMd5Action.getFullFileMd5(), accountLoadFromLocalAction.getToken());

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

    private AccountLoadFromLocalAction accountLoadFromLocalAction(final ReaderDataHolder readerDataHolder) {
        return new AccountLoadFromLocalAction();
    }

    private GetFileFullMd5Action getFileFullMd5(final ReaderDataHolder readerDataHolder) {
        return new GetFileFullMd5Action(readerDataHolder.getDocumentPath());
    }

    private ExportNoteDataAction exportNoteData(final ReaderDataHolder readerDataHolder, final StringBuffer fullFileMd5) {
        return new ExportNoteDataAction(fullFileMd5);
    }

    private SaveDocumentDataToCloudAction SaveDocumentDataToCloud(ReaderDataHolder readerDataHolder, StringBuffer exportDBFilePath, StringBuffer fileFullMd5, StringBuffer token) {
        // TODO: 2017/6/1 for test
        String cloudDocId = "59351d5327c70b8b8ec481bc";
        return new SaveDocumentDataToCloudAction(exportDBFilePath,
                fileFullMd5,
                cloudDocId,
                token);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
