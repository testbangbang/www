package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.CheckAnnotationRequest;

/**
 * Created by huxiaomao on 2018/3/4.
 */

public class CheckAnnotationAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final CheckAnnotationRequest action = new CheckAnnotationRequest(readerDataHolder.getReader(),readerDataHolder.getReaderSelectionInfo().getReaderSelectionInfos());
        action.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if(baseCallback != null){
                    baseCallback.onNext(o);
                }
            }
        });
    }
}
