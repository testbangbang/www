package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;
import com.onyx.jdread.reader.request.AddAnnotationRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class AddAnnotationAction extends BaseReaderAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        Map<String,ReaderSelectionInfo> readerSelectionInfos = new HashMap<>();
        readerSelectionInfos.putAll(readerDataHolder.getSelectionInfoManager().getReaderSelectionInfos());

        final AddAnnotationRequest request = new AddAnnotationRequest(readerDataHolder.getReader(),readerSelectionInfos);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
