package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.InitPageViewInfoEvent;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.request.InitFirstPageViewRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class InitPageViewAction extends BaseReaderAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        final InitFirstPageViewRequest request = new InitFirstPageViewRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                EventBus.getDefault().post(new InitPageViewInfoEvent(request.getReaderViewInfo()));
                ReaderActivityEventHandler.updateReaderViewInfo(request);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
