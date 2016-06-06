package com.onyx.kreader.ui.actions;

import android.view.View;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.dataprovider.request.LoadDocumentOptionsRequest;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.CreateViewRequest;
import com.onyx.kreader.host.request.OpenRequest;
import com.onyx.kreader.host.request.RestoreRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogLoading;
import com.onyx.kreader.ui.dialog.DialogPassword;
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 5/17/16.
 * steps:
 * 1. load document options.
 * 2. open with options.
 * 3. create view
 * 4. restoreWithOptions.
 */
public class OpenDocumentAction extends BaseAction {

    private String documentPath;
    private DialogLoading dialogLoading;

    public OpenDocumentAction(final String path) {
        documentPath = path;
    }

    public void execute(final ReaderActivity readerActivity) {
        showLoadingDialog(readerActivity);
        final Reader reader = ReaderManager.getReader(documentPath);
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(documentPath,
                readerActivity.getReader().getDocumentMd5());
        readerActivity.getDataProvider().submit(readerActivity, loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    cleanup(readerActivity);
                    return;
                }
                openWithOptions(readerActivity, reader, loadDocumentOptionsRequest.getDocumentOptions());
            }
        });
    }

    private void openWithOptions(final ReaderActivity readerActivity, final Reader reader, final BaseOptions options) {
        final BaseReaderRequest openRequest = new OpenRequest(documentPath, options);
        reader.submitRequest(readerActivity, openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                if (e != null) {
                    processOpenException(readerActivity, reader, options, e);
                    return;
                }
                onFileOpenSucceed(readerActivity, reader, options);
            }
        });
    }

    private void onFileOpenSucceed(final ReaderActivity readerActivity, final Reader reader, final BaseOptions options) {
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
                restoreWithOptions(readerActivity, options);
            }
        });
    }

    private void showPasswordDialog(final ReaderActivity readerActivity) {
        hideLoadingDialog(readerActivity);
        final DialogPassword dlg = new DialogPassword(readerActivity);
        dlg.setOnPasswordEnteredListener(new DialogPassword.OnPasswordEnteredListener() {
            @Override
            public void onPasswordEntered(boolean success, String password) {
                dlg.dismiss();
                if (!success) {
                    readerActivity.quitApplication();
                } else {
                    readerActivity.getReader().getDocumentOptions().setPassword(password);
                    openWithOptions(readerActivity, readerActivity.getReader(), readerActivity.getReader().getDocumentOptions());
                }
            }
        });
        dlg.show();
    }

    private DialogLoading showLoadingDialog(final ReaderActivity activity) {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(activity, activity.getResources().getString(R.string.loading_document), true);
            dialogLoading.setCancelButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.quitApplication();
                }
            });
        }
        dialogLoading.show();
        return dialogLoading;
    }

    private void hideLoadingDialog(final ReaderActivity activity) {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }

    private void cleanup(final ReaderActivity readerActivity) {
        hideLoadingDialog(readerActivity);
    }

    private void restoreWithOptions(final ReaderActivity readerActivity, final BaseOptions options) {
        final RestoreRequest restoreRequest = new RestoreRequest(options);
        readerActivity.submitRequest(restoreRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Exception e) {
                hideLoadingDialog(readerActivity);
            }
        });
    }

    private void processOpenException(final ReaderActivity readerActivity, final Reader reader, final BaseOptions options, final Exception e) {
        if (!(e instanceof ReaderException)) {
            return;
        }
        final ReaderException readerException = (ReaderException)e;
        if (readerException.getCode() == ReaderException.PASSWORD_REQUIRED) {
            showPasswordDialog(readerActivity);
            return;
        }
        readerActivity.quitApplication();
    }

}
