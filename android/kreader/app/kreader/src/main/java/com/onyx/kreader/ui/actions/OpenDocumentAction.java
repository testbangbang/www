package com.onyx.kreader.ui.actions;

import android.view.View;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderException;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.dataprovider.DataProvider;
import com.onyx.kreader.dataprovider.request.LoadDocumentOptionsRequest;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.CreateViewRequest;
import com.onyx.kreader.host.request.OpenRequest;
import com.onyx.kreader.host.request.RestoreRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.host.wrapper.ReaderManager;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogLoading;
import com.onyx.kreader.ui.dialog.DialogPassword;
import com.onyx.kreader.ui.events.MainMessageEvent;

import org.greenrobot.eventbus.EventBus;

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
    private DataProvider dataProvider;
    private ReaderActivity readerActivity;
    private ReaderDataHolder readerDataHolder;

    public OpenDocumentAction(final String path,final ReaderActivity readerActivity) {
        documentPath = path;
        this.readerActivity = readerActivity;
        dataProvider = new DataProvider();
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        showLoadingDialog(readerActivity);
        final Reader reader = ReaderManager.getReader(documentPath);
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(documentPath,
                readerDataHolder.getReader().getDocumentMd5());
        dataProvider.submit(readerDataHolder.getContext(), loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    cleanup();
                    return;
                }
                openWithOptions(readerActivity,reader, loadDocumentOptionsRequest.getDocumentOptions());
            }
        });
    }

    private void openWithOptions(final ReaderActivity readerActivity, final Reader reader, final BaseOptions options) {
        final BaseReaderRequest openRequest = new OpenRequest(documentPath, options);
        reader.submitRequest(readerActivity, openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
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
        final BaseReaderRequest config = new CreateViewRequest(readerDataHolder.getDisplayWidth(), readerDataHolder.getDisplayHeight());
        reader.submitRequest(readerActivity, config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    cleanup();
                    return;
                }
                restoreWithOptions(options);
            }
        });
    }

    private void showPasswordDialog(final ReaderActivity readerActivity) {
        hideLoadingDialog();
        final DialogPassword dlg = new DialogPassword(readerActivity);
        dlg.setOnPasswordEnteredListener(new DialogPassword.OnPasswordEnteredListener() {
            @Override
            public void onPasswordEntered(boolean success, String password) {
                dlg.dismiss();
                if (!success) {
                    EventBus.getDefault().post(MainMessageEvent.quitApplication());
                } else {
                    readerDataHolder.getReader().getDocumentOptions().setPassword(password);
                    openWithOptions(readerActivity, readerDataHolder.getReader(), readerDataHolder.getReader().getDocumentOptions());
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
                    EventBus.getDefault().post(MainMessageEvent.quitApplication());
                }
            });
        }
        dialogLoading.show();
        return dialogLoading;
    }

    private void hideLoadingDialog() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }

    private void cleanup() {
        hideLoadingDialog();
    }

    private void restoreWithOptions(final BaseOptions options) {
        final RestoreRequest restoreRequest = new RestoreRequest(options);
        readerDataHolder.submitRequest(restoreRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
            }
        });
    }

    private void processOpenException(final ReaderActivity readerActivity, final Reader reader, final BaseOptions options, final Throwable e) {
        if (!(e instanceof ReaderException)) {
            return;
        }
        final ReaderException readerException = (ReaderException)e;
        if (readerException.getCode() == ReaderException.PASSWORD_REQUIRED) {
            showPasswordDialog(readerActivity);
            return;
        }
        EventBus.getDefault().post(MainMessageEvent.quitApplication());
    }

}
