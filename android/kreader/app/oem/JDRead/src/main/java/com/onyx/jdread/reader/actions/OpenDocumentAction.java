package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.OpenDocumentFailResultEvent;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.LoadDocumentOptionsRequest;
import com.onyx.jdread.reader.request.OpenDocumentRequest;

/**
 * Created by huxiaomao on 17/11/13.
 */

public class OpenDocumentAction extends BaseReaderAction {
    private ReaderDataHolder readerDataHolder;

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
                openDocument(readerDataHolder, request);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }

    private void openDocument(final ReaderDataHolder readerDataHolder, LoadDocumentOptionsRequest request) {
        OpenDocumentRequest openDocumentRequest = new OpenDocumentRequest(readerDataHolder.getReader(),request.getDocumentOptions());
        OpenDocumentRequest.setAppContext(readerDataHolder.getAppContext());
        readerDataHolder.setDocumentOpeningState();
        openDocumentRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                onDocumentOpened();
            }

            @Override
            public void onError(Throwable throwable) {
                onDocumentFailed(readerDataHolder,throwable);
            }
        });
    }

    private void onDocumentOpened() {
        InitPageViewAction createPageViewAction = new InitPageViewAction();
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
