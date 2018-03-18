package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.CloseDocumentRequest;

/**
 * Created by huxiaomao on 2017/12/21.
 */

public class CloseDocumentAction extends BaseReaderAction {
    private boolean saveOption = true;

    public CloseDocumentAction(boolean saveOption) {
        this.saveOption = saveOption;
    }

    public CloseDocumentAction() {
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        readerDataHolder.setDocumentInitState();

        final CloseDocumentRequest request = new CloseDocumentRequest(readerDataHolder.getReader(), saveOption);
        request.execute(baseCallback);
        long readingTime = readerDataHolder.updateReadingTime();
    }
}
