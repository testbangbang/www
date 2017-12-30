package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.InitFirstPageViewRequest;
import com.onyx.jdread.reader.request.UpdateViewPageRequest;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class InitPageViewAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        InitFirstPageViewRequest initFirstPageViewRequest = new InitFirstPageViewRequest(readerDataHolder);
        initFirstPageViewRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                new UpdateViewPageAction().execute(readerDataHolder);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
