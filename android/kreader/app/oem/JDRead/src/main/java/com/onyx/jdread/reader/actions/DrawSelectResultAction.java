package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.DrawSelectResultRequest;

/**
 * Created by huxiaomao on 2018/2/8.
 */

public class DrawSelectResultAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        final DrawSelectResultRequest request = new DrawSelectResultRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });
    }
}
