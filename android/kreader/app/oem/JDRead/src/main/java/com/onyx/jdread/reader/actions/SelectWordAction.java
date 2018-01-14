package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.common.SelectWordInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.SelectWordRequest;

/**
 * Created by huxiaomao on 2016/6/3.
 */
public class SelectWordAction extends BaseReaderAction {
    private SelectWordInfo selectWordInfo;

    public SelectWordAction(SelectWordInfo selectWordInfo) {
        this.selectWordInfo = selectWordInfo;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        final SelectWordRequest selectWordRequest = new SelectWordRequest(readerDataHolder,
                selectWordInfo.pagePosition,
                selectWordInfo.startPoint,
                selectWordInfo.endPoint,
                selectWordInfo.touchPoint,
                ReaderHitTestOptionsImpl.create(false));
        selectWordRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
            }
        });
    }
}