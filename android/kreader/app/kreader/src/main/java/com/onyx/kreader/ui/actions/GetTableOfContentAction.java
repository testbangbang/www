package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.host.request.GetTableOfContentRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;

public class GetTableOfContentAction extends BaseAction {

    public void execute(final ReaderActivity readerActivity) {
        final GetTableOfContentRequest tocRequest = new GetTableOfContentRequest();
        readerActivity.submitRequest(tocRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showTableOfContentDialog(readerActivity, tocRequest.getReaderViewInfo().getTableOfContent());
            }
        });
    }

    private void showTableOfContentDialog(final ReaderActivity activity, final ReaderDocumentTableOfContent tableOfContent) {
        DialogTableOfContent dlg = new DialogTableOfContent(activity, tableOfContent);
        dlg.show();
    }

}
