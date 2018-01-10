package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.event.UpdateReaderViewInfoEvent;
import com.onyx.jdread.reader.request.UpdateViewPageRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class UpdateViewPageAction extends BaseReaderAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        final UpdateViewPageRequest request = new UpdateViewPageRequest(readerDataHolder);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(request);
            }
        });
    }
}
