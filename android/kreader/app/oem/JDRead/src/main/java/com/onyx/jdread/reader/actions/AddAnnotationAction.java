package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.request.AddAnnotationRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AddAnnotationAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        Map<String,SelectionInfo> readerSelectionInfos = new HashMap<>();
        readerSelectionInfos.putAll(readerDataHolder.getReaderSelectionInfo().getReaderSelectionInfos());

        final AddAnnotationRequest request = new AddAnnotationRequest(readerDataHolder.getReader(),readerSelectionInfos);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
            }
        });
    }
}
