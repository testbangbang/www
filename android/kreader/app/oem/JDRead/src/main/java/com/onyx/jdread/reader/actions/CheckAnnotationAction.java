package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.CheckAnnotationRequest;

/**
 * Created by huxiaomao on 2018/3/4.
 */

public class CheckAnnotationAction extends BaseReaderAction {
    public boolean isEquals = false;
    public String userNote = "";
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final CheckAnnotationRequest request = new CheckAnnotationRequest(readerDataHolder.getReader(),readerDataHolder.getReaderSelectionInfo().getReaderSelectionInfos());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                isEquals = request.isEquals;
                userNote = request.userNote;
                if(baseCallback != null){
                    baseCallback.onNext(o);
                }
            }
        });
    }
}
