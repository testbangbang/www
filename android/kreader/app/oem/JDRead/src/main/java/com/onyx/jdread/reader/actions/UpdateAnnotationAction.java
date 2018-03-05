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
    private Annotation annotation;
    public String newNote;
    public String srcNote;
    public int srcNoteState;

    public UpdateAnnotationAction(Annotation annotation, String newNote, String srcNote, int srcNoteState) {
        this.annotation = annotation;
        this.newNote = newNote;
        this.srcNote = srcNote;
        this.srcNoteState = srcNoteState;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {

        final UpdateAnnotationRequest request = new UpdateAnnotationRequest(readerDataHolder.getReader(), annotation, newNote,srcNote,srcNoteState);
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
