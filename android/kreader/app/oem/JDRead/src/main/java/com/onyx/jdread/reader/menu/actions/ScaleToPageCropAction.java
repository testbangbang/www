package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.ScaleToPageCropRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ScaleToPageCropAction extends BaseReaderAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        new ScaleToPageCropRequest(readerDataHolder).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
