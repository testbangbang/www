package com.onyx.kreader.ui.actions;

import android.app.Activity;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.CreateViewRequest;
import com.onyx.kreader.host.request.LoadDocumentOptionsRequest;
import com.onyx.kreader.host.request.OpenRequest;
import com.onyx.kreader.host.request.RestoreRequest;
import com.onyx.kreader.host.request.SaveDocumentOptionsRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogPassword;
import com.onyx.kreader.ui.events.ChangeOrientationEvent;
import com.onyx.kreader.ui.events.QuitEvent;
import com.onyx.kreader.utils.DeviceUtils;

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
    private DataManager dataProvider;

    public OpenDocumentAction(final Activity activity, final String path) {
        this.activity = activity;
        documentPath = path;
        dataProvider = new DataManager();
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.initReaderFromPath(documentPath);
        showLoadingDialog(readerDataHolder, R.string.loading_document);
        final LoadDocumentOptionsRequest loadDocumentOptionsRequest = new LoadDocumentOptionsRequest(documentPath,
                readerDataHolder.getReader().getDocumentMd5());
        dataProvider.submit(readerDataHolder.getContext(), loadDocumentOptionsRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    cleanup();
                    return;
                }
                if (!processOrientation(readerDataHolder, loadDocumentOptionsRequest.getDocument())) {
                    return;
                }
                openWithOptions(readerDataHolder, loadDocumentOptionsRequest.getDocument());
            }
        });
    }

    private boolean processOrientation(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        if (options != null && options.getOrientation() > 0) {
            int current = DeviceUtils.getScreenOrientation(activity);
            int target = options.getOrientation();
            Debug.d("current orientation: " + current + ", target orientation: " + target);
            if (current != target) {
                readerDataHolder.getEventBus().post(new ChangeOrientationEvent(target));
                hideLoadingDialog();
                return false;
            }
        }
        return true;
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

    private void postQuitEvent(final ReaderDataHolder holder) {
        cleanup();
        holder.getEventBus().post(new QuitEvent());
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
                readerDataHolder.submitNonRenderRequest(new SaveDocumentOptionsRequest());
                readerDataHolder.onDocumentInitRendered();
            }
        });
    }

    private void processOpenException(final ReaderDataHolder holder, final BaseOptions options, final Throwable e) {
        if (!(e instanceof ReaderException)) {
            postQuitEvent(holder);
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
