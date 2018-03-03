package com.onyx.jdread.reader.actions;

import android.app.Activity;
import android.content.DialogInterface;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.android.sdk.ui.dialog.DialogReaderLoading;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.dialog.DialogPassword;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
import com.onyx.jdread.reader.event.OpenDocumentFailResultEvent;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.LoadDocumentOptionsRequest;
import com.onyx.jdread.reader.request.OpenDocumentRequest;

/**
 * Created by huxiaomao on 17/11/13.
 */

public class OpenDocumentAction extends BaseReaderAction {
    private Activity activity;
    private ReaderDataHolder readerDataHolder;
    private DialogReaderLoading dlgLoading;

    public OpenDocumentAction(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        this.readerDataHolder = readerDataHolder;
        loadDocumentOptions(readerDataHolder);
    }

    private void loadDocumentOptions(final ReaderDataHolder readerDataHolder) {
        final LoadDocumentOptionsRequest request = new LoadDocumentOptionsRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                // save temporary options object
                BaseOptions options = request.getDocumentOptions();
                // always reset password as JD required
                options.setPassword("");
                openDocument(readerDataHolder, options);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }

    private void openDocument(final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        final OpenDocumentRequest openDocumentRequest = new OpenDocumentRequest(readerDataHolder.getReader(),options);
        OpenDocumentRequest.setAppContext(readerDataHolder.getAppContext());
        readerDataHolder.setDocumentOpeningState();

        dlgLoading = new DialogReaderLoading(activity, readerDataHolder.getBookName());
        dlgLoading.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                openDocumentRequest.setAbort(true);
                readerDataHolder.getEventBus().post(new CloseDocumentEvent());
            }
        });
        dlgLoading.show();

        openDocumentRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (openDocumentRequest.getAbort()) {
                    dlgLoading.dismiss();
                    return;
                }
                onDocumentOpened();
            }

            @Override
            public void onError(Throwable throwable) {
                dlgLoading.dismiss();
                if (openDocumentRequest.getAbort()) {
                    return;
                }
                if (throwable instanceof ReaderException &&
                        handleReaderException((ReaderException)throwable, readerDataHolder, options)) {
                    return;
                }
                onDocumentFailed(readerDataHolder,throwable);
            }
        });
    }

    private boolean handleReaderException(final ReaderException ex, final ReaderDataHolder readerDataHolder, final BaseOptions options) {
        switch (ex.getCode()) {
            case ReaderException.PASSWORD_REQUIRED:
                showPasswordDialog(readerDataHolder, options);
                return true;
            default:
                break;
        }
        return false;
    }

    private void showPasswordDialog(final ReaderDataHolder readerDataHolder,final BaseOptions options) {
        final DialogPassword dlg = new DialogPassword(activity);
        dlg.setOnPasswordEnteredListener(new DialogPassword.OnPasswordEnteredListener() {
            @Override
            public void onPasswordEntered(boolean success, String password) {
                dlg.dismiss();
                if (!success) {
                    readerDataHolder.getEventBus().post(new CloseDocumentEvent());
                } else {
                    options.setPassword(password);
                    openDocument(readerDataHolder, options);
                }
            }
        });
        dlg.show();
    }

    private void onDocumentOpened() {
        InitPageViewAction createPageViewAction = new InitPageViewAction(dlgLoading);
        createPageViewAction.execute(readerDataHolder,null);
    }

    private void onDocumentFailed(ReaderDataHolder readerDataHolder, Throwable throwable) {
        OpenDocumentFailResultEvent event = new OpenDocumentFailResultEvent();
        String message = throwable.getMessage();
        if (StringUtils.isNullOrEmpty(message)) {
            message = readerDataHolder.getAppContext().getString(R.string.open_book_fail);
        }
        event.setMessage(message);
        readerDataHolder.getEventBus().post(event);
    }
}
