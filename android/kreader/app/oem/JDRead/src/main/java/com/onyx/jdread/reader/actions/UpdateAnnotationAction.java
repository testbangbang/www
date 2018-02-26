package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.UpdateAnnotationRequest;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateAnnotationAction extends BaseReaderAction {
    private NoteInfo noteInfo;
    private Annotation annotation;

    public UpdateAnnotationAction(NoteInfo noteInfo,Annotation annotation) {
        this.noteInfo = noteInfo;
        this.annotation = annotation;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {

        final UpdateAnnotationRequest request = new UpdateAnnotationRequest(readerDataHolder.getReader(), annotation, noteInfo);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);
                if (baseCallback != null) {
                    baseCallback.onNext(null);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }

            @Override
            public void onFinally() {
            }
        });
    }
}
