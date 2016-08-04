package com.onyx.kreader.ui.actions;

import android.app.Activity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.dataprovider.DataProvider;
import com.onyx.kreader.dataprovider.request.LoadDocumentOptionsRequest;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.CreateViewRequest;
import com.onyx.kreader.host.request.OpenRequest;
import com.onyx.kreader.host.request.RestoreRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogLoading;
import com.onyx.kreader.ui.dialog.DialogPassword;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;
import com.onyx.kreader.ui.events.DocumentOpenEvent;
import com.onyx.kreader.ui.events.QuitEvent;

/**
 * Created by zhuzeng on 5/17/16.
 * steps:
 * 1. load document options.
 * 2. open with options.
 * 3. create view
 * 4. restoreWithOptions.
 */
public class OpenDocumentAction extends BaseAction {
    private Activity activity;
    private String documentPath;
    private DialogLoading dialogLoading;
    private DataProvider dataProvider;

    public OpenDocumentAction(final Activity activity, final String path) {
        this.activity = activity;
        documentPath = path;
        dataProvider = new DataProvider();
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.initReaderFromPath(documentPath);
        showLoadingDialog(readerDataHolder);
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(documentPath,
                readerDataHolder.getReader().getDocumentMd5());
        dataProvider.submit(readerDataHolder.getContext(), loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    cleanup();
                    return;
                }
                if (loadDocumentOptionsRequest.getDocumentOptions() != null &&
                        loadDocumentOptionsRequest.getDocumentOptions().getOrientation() > 0) {
                    int orientation = loadDocumentOptionsRequest.getDocumentOptions().getOrientation();
                    Debug.d("current orientation: " + activity.getRequestedOrientation() +
                            ", target orientation: " + orientation);
                    if (activity.getRequestedOrientation() != orientation) {
                        readerDataHolder.getEventBus().post(new ChangeOrientationEvent(orientation));
                        hideLoadingDialog();
                        return;
                    }
                }
                openWithOptions(readerDataHolder, loadDocumentOptionsRequest.getDocumentOptions());
            }
        });
    }

    private void openWithOptions(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        final BaseReaderRequest openRequest = new OpenRequest(documentPath, options);
        readerDataHolder.submitNonRenderRequest(openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    processOpenException(readerDataHolder, options, e);
                    return;
                }
                onFileOpenSucceed(readerDataHolder, options);
            }
        });
    }

    private void onFileOpenSucceed(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        readerDataHolder.onDocumentOpened();
        readerDataHolder.getHandlerManager().setEnable(true);
        final BaseReaderRequest config = new CreateViewRequest(readerDataHolder.getDisplayWidth(), readerDataHolder.getDisplayHeight());
        readerDataHolder.submitNonRenderRequest(config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    cleanup();
                    return;
                }
                restoreWithOptions(readerDataHolder, options);
            }
        });
    }

    private void showPasswordDialog(final ReaderDataHolder readerDataHolder,final BaseOptions options) {
        hideLoadingDialog();
        final DialogPassword dlg = new DialogPassword(readerDataHolder.getContext());
        dlg.setOnPasswordEnteredListener(new DialogPassword.OnPasswordEnteredListener() {
            @Override
            public void onPasswordEntered(boolean success, String password) {
                dlg.dismiss();
                if (!success) {
                    postQuitEvent(readerDataHolder);
                } else {
                    options.setPassword(password);
                    openWithOptions(readerDataHolder, options);
                }
            }
        });
        dlg.show();
    }

    private DialogLoading showLoadingDialog(final ReaderDataHolder holder) {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(holder.getContext(),
                    holder.getContext().getResources().getString(R.string.loading_document),
                    true, new DialogLoading.Callback() {
                @Override
                public void onCanceled() {
                    postQuitEvent(holder);
                }
            });
        }
        dialogLoading.show();
        return dialogLoading;
    }

    private void postQuitEvent(final ReaderDataHolder holder) {
        holder.getEventBus().post(new QuitEvent());
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

    private void restoreWithOptions(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        final RestoreRequest restoreRequest = new RestoreRequest(options);
        readerDataHolder.submitRenderRequest(restoreRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
            }
        });
    }

    private void processOpenException(final ReaderDataHolder holder, final BaseOptions options, final Throwable e) {
        if (!(e instanceof ReaderException)) {
            return;
        }
        final ReaderException readerException = (ReaderException)e;
        if (readerException.getCode() == ReaderException.PASSWORD_REQUIRED) {
            showPasswordDialog(holder, options);
            return;
        }
        postQuitEvent(holder);
    }

}
