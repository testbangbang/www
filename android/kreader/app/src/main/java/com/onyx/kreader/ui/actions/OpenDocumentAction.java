package com.onyx.kreader.ui.actions;

import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.request.CreateViewRequest;
import com.onyx.kreader.host.request.GotoInitPositionRequest;
import com.onyx.kreader.host.request.GotoLocationRequest;
import com.onyx.kreader.host.request.OpenRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class OpenDocumentAction extends BaseAction {

    private String documentPath;

    public OpenDocumentAction(final String path) {
        documentPath = path;
    }

    public void execute(final ReaderActivity readerActivity) {
        showLoadingDialog(readerActivity);
        final Reader reader = ReaderManager.getReader(documentPath);
        BaseRequest openRequest = new OpenRequest(documentPath, readerActivity.getDocumentOptions(), readerActivity.getPluginOptions());
        reader.submitRequest(readerActivity, openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    cleanup(readerActivity);
                    return;
                }
                onFileOpenSucceed(readerActivity, reader);
            }
        });
    }

    private void onFileOpenSucceed(final ReaderActivity readerActivity, final Reader reader) {
        readerActivity.getHandlerManager().setEnable(true);
        BaseRequest config = new CreateViewRequest(readerActivity.getDisplayWidth(), readerActivity.getDisplayHeight());
        reader.submitRequest(readerActivity, config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    cleanup(readerActivity);
                    return;
                }
                restore(readerActivity);
            }
        });
    }

    private void showLoadingDialog(final ReaderActivity readerActivity) {
    }

    private void showPasswordDialog(final ReaderActivity readerActivity) {
    }

    private void cleanup(final ReaderActivity readerActivity) {
    }

    private void restore(final ReaderActivity readerActivity) {
        BaseRequest gotoPosition = new GotoInitPositionRequest();
        readerActivity.submitRenderRequest(gotoPosition);
    }

}
