package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.DeleteAnnotationRequest;

/**
 * Created by huxiaomao on 2018/1/29.
 */

public class DeleteAnnotationAction extends BaseReaderAction {
    private Annotation annotation;

    public DeleteAnnotationAction(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final DeleteAnnotationRequest request = new DeleteAnnotationRequest(readerDataHolder.getReader(),annotation);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
                if(baseCallback != null){
                    baseCallback.onNext(o);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }
}
