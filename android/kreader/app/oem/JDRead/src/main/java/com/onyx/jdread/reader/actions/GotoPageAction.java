package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.GotoPageRequest;
import com.onyx.jdread.reader.request.PreloadNextScreenRequest;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class GotoPageAction extends BaseReaderAction {
    private int page;

    public GotoPageAction(int page) {
        this.page = page;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        final GotoPageRequest request = new GotoPageRequest(readerDataHolder.getReader(), page,readerDataHolder.getSettingInfo());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
                PreloadNextScreenRequest preloadNextScreenRequest = new PreloadNextScreenRequest(readerDataHolder.getReader());
                preloadNextScreenRequest.execute(null);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });
    }
}
