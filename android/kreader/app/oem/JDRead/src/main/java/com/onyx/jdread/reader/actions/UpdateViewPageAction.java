package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.UpdateViewPageRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class UpdateViewPageAction extends BaseAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        new UpdateViewPageRequest(readerDataHolder).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
