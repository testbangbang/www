package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.NoteInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.ReaderSelectionInfo;
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
    public void execute(ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        ReaderSelectionInfo readerSelectionInfo = readerDataHolder.getReaderSelectionManager().getReaderSelectionInfo(pagePosition);

        final UpdateAnnotationRequest request = new UpdateAnnotationRequest(readerDataHolder.getReader(), readerSelectionInfo, noteInfo);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if (baseCallback != null) {
                    baseCallback.onNext(null);
                }
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
