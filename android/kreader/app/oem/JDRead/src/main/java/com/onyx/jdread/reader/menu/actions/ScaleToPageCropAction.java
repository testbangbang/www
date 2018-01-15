package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.request.ScaleToPageCropRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ScaleToPageCropAction extends BaseReaderAction {
    private ReaderViewInfo readerViewInfo;

    public ScaleToPageCropAction(ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        final ScaleToPageCropRequest request = new ScaleToPageCropRequest(readerDataHolder,readerViewInfo);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(request);
            }
        });
    }
}
