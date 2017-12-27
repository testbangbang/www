package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.CreatePageViewRequest;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class CreatePageViewAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        CreatePageViewRequest createPageViewRequest = new CreatePageViewRequest(readerDataHolder);
        createPageViewRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                //
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
