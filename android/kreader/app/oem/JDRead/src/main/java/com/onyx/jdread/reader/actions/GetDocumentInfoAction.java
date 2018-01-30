package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.catalog.event.GetDocumentInfoResultEvent;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.GetDocumentInfoRequest;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class GetDocumentInfoAction extends BaseReaderAction {

    public GetDocumentInfoAction() {
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final GetDocumentInfoRequest request = new GetDocumentInfoRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.setReaderUserDataInfo(request.getReaderUserDataInfo());
                GetDocumentInfoResultEvent event = new GetDocumentInfoResultEvent();
                readerDataHolder.getEventBus().post(event);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });
    }
}
