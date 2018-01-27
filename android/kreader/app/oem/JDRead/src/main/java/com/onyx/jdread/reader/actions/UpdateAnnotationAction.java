package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.UpdateAnnotationRequest;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateAnnotationAction extends BaseReaderAction {
    private NoteInfo noteInfo;
    private String pagePosition;

    public UpdateAnnotationAction(NoteInfo noteInfo, String pagePosition) {
        this.noteInfo = noteInfo;
        this.pagePosition = pagePosition;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        SelectionInfo readerSelectionInfo = readerDataHolder.getReaderSelectionInfo().getReaderSelectionInfo(pagePosition);

        final UpdateAnnotationRequest request = new UpdateAnnotationRequest(readerDataHolder.getReader(), readerSelectionInfo, noteInfo);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
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
                if (baseCallback != null) {
                    baseCallback.onNext(null);
                }
            }
        });
    }
}
