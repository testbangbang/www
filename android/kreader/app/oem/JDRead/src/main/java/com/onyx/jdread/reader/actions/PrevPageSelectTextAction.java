package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.ReaderSelectionManager;
import com.onyx.jdread.reader.request.PreviousPageSelectTextRequest;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class PrevPageSelectTextAction extends BaseReaderAction {
    private ReaderSelectionManager readerSelectionManager;

    public PrevPageSelectTextAction(ReaderSelectionManager readerSelectionManager, ActionCallBack callBack) {
        this.readerSelectionManager = readerSelectionManager;
        this.callBack = callBack;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        ReaderTextStyle style = readerDataHolder.getStyleCopy();
        final String pagePosition = readerDataHolder.getCurrentPagePosition();

        final PreviousPageSelectTextRequest request = new PreviousPageSelectTextRequest(readerDataHolder.getReader(), style, readerSelectionManager);
        readerDataHolder.getReaderSelectionManager().incrementSelectCount();
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getReaderSelectionManager().decrementSelectCount();
                if (request.isSuccess) {
                    updateData(readerDataHolder, request, pagePosition);
                }
                if (callBack != null) {
                    callBack.onFinally(pagePosition);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                readerDataHolder.getReaderSelectionManager().decrementSelectCount();
                if (callBack != null) {
                    callBack.onFinally(pagePosition);
                }
            }
        });
    }

    private void updateData(ReaderDataHolder readerDataHolder, ReaderBaseRequest request, String pagePosition) {
        readerDataHolder.setReaderUserDataInfo(request.getReaderUserDataInfo());
        readerDataHolder.setReaderViewInfo(request.getReaderViewInfo());
    }
}
