package com.onyx.jdread.reader.actions;

import android.content.Intent;

import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.common.DocumentInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.OpenDocumentRequest;
import com.onyx.jdread.reader.request.ReaderDocumentCoverRequest;

/**
 * Created by huxiaomao on 2018/1/25.
 */

public class ReaderDocumentCoverAction extends BaseReaderAction {
    private static final String TAG = ReaderDocumentCoverAction.class.getSimpleName();
    private int width;
    private int height;

    public ReaderDocumentCoverAction(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        BaseOptions options = new BaseOptions();
        OpenDocumentRequest request = new OpenDocumentRequest(readerDataHolder.getReader(), options, readerDataHolder.getEventBus());
        OpenDocumentRequest.setAppContext(readerDataHolder.getAppContext());
        readerDataHolder.setDocumentOpeningState();
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerCover(readerDataHolder, baseCallback);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });
    }

    public void readerCover(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final ReaderDocumentCoverRequest request = new ReaderDocumentCoverRequest(readerDataHolder.getReader(), width, height);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (baseCallback != null) {
                    baseCallback.onNext(request.getCover().getBitmap());
                }
            }

            @Override
            public void onFinally() {
                if (request.getCover() != null && request.getCover().getBitmap() != null) {
                    request.getCover().getBitmap().recycle();
                }
                closeDocument(readerDataHolder);
            }
        });
    }

    private void closeDocument(ReaderDataHolder readerDataHolder) {
        CloseDocumentAction closeDocumentAction = new CloseDocumentAction(false);
        closeDocumentAction.execute(readerDataHolder, null);
    }

    public static ReaderDataHolder initReaderDataHolder(String bookPath) {
        final ReaderDataHolder readerDataHolder = new ReaderDataHolder(JDReadApplication.getInstance());

        Intent intent = new Intent();
        intent.putExtra(DocumentInfo.BOOK_PATH, bookPath);
        final ParserOpenDocumentInfoAction parserOpenDocumentInfoAction = new ParserOpenDocumentInfoAction(intent);
        parserOpenDocumentInfoAction.execute(readerDataHolder, null);
        readerDataHolder.initReaderDataHolder(parserOpenDocumentInfoAction.getDocumentInfo());

        return readerDataHolder;
    }
}
