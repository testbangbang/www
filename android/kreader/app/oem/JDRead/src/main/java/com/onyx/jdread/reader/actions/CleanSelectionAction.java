package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.CleanSelectionRequest;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class CleanSelectionAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final CleanSelectionRequest request = new CleanSelectionRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateViewPage(readerDataHolder,baseCallback);
            }
        });
    }

    private void updateViewPage(final ReaderDataHolder readerDataHolder,final RxCallback baseCallback){
        final UpdateViewPageAction request =new UpdateViewPageAction();
        request.execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                if(baseCallback != null){
                    baseCallback.onNext(o);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }
}
