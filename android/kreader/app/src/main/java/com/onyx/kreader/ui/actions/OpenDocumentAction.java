package com.onyx.kreader.ui.actions;

import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.dataprovider.request.LoadDocumentOptionsRequest;
import com.onyx.kreader.host.request.CreateViewRequest;
import com.onyx.kreader.host.request.GotoInitPositionRequest;
import com.onyx.kreader.host.request.OpenRequest;
import com.onyx.kreader.host.request.RestoreRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class OpenDocumentAction extends BaseAction {

    private String documentPath;

    public OpenDocumentAction(final String path) {
        documentPath = path;
    }

    private ReaderPluginOptions getPluginOptions() {
        return null;
    }

    public void execute(final ReaderActivity readerActivity) {
        showLoadingDialog(readerActivity);
        final Reader reader = ReaderManager.getReader(documentPath);
        final BaseReaderRequest openRequest = new OpenRequest(documentPath, null);
        reader.submitRequest(readerActivity, openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    processOpenException(readerActivity, reader, e);
                    return;
                }
                onFileOpenSucceed(readerActivity, reader);
            }
        });
    }

    private void onFileOpenSucceed(final ReaderActivity readerActivity, final Reader reader) {
        readerActivity.onDocumentOpened(documentPath);
        readerActivity.getHandlerManager().setEnable(true);
        final BaseReaderRequest config = new CreateViewRequest(readerActivity.getDisplayWidth(), readerActivity.getDisplayHeight());
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
        hideLoadingDialog(readerActivity);
    }

    private void hideLoadingDialog(final ReaderActivity readerActivity) {
    }

    private void cleanup(final ReaderActivity readerActivity) {
        hideLoadingDialog(readerActivity);
    }

    private void restore(final ReaderActivity readerActivity) {
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(documentPath,
                readerActivity.getReader().getDocumentMd5());
        readerActivity.getDataProvider().submit(readerActivity, loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                final RestoreRequest restoreRequest = new RestoreRequest(loadDocumentOptionsRequest.getDocumentOptions());
                readerActivity.submitRenderRequest(restoreRequest);
            }
        });
    }

    private void processOpenException(final ReaderActivity readerActivity, final Reader reader, final Exception e) {
        if (StringUtils.isNullOrEmpty(reader.getDocumentOptions().getPassword())) {
            cleanup(readerActivity);
            return;
        }

        if (!(e instanceof ReaderException)) {
            return;
        }
        final ReaderException readerException = (ReaderException)e;
        if (readerException.getCode() == ReaderException.PASSWORD_REQUIRED) {
            showPasswordDialog(readerActivity);
            return;
        }
    }

}
