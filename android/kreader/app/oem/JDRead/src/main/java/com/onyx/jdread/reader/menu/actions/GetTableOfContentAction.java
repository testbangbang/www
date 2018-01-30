package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.request.GetTableOfContentRequest;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GetTableOfContentAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final GetTableOfContentRequest request = new GetTableOfContentRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (baseCallback != null) {
                    baseCallback.onNext(request);
                    ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });
    }
}
