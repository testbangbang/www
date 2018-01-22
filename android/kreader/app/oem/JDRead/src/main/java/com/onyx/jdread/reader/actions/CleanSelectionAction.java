package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.CleanSelectionRequest;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class CleanSelectionAction extends BaseReaderAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        new CleanSelectionRequest(readerDataHolder.getReader()).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
