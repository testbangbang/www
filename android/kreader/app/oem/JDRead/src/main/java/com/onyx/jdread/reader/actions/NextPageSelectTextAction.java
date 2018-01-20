package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.ReaderSelectionManager;
import com.onyx.jdread.reader.request.NextPageSelectTextRequest;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class NextPageSelectTextAction extends BaseReaderAction {
    private ReaderSelectionManager readerSelectionManager;

    public NextPageSelectTextAction(ReaderSelectionManager readerSelectionManager) {
        this.readerSelectionManager = readerSelectionManager;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        ReaderTextStyle style = readerDataHolder.getStyleCopy();
        final String pagePosition = readerDataHolder.getCurrentPagePosition();

        final NextPageSelectTextRequest request = new NextPageSelectTextRequest(readerDataHolder.getReader(), style, readerSelectionManager);
        readerDataHolder.getReaderSelectionManager().incrementSelectCount();
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onFinally() {
                readerDataHolder.getReaderSelectionManager().decrementSelectCount();
                if (request.isSuccess) {
                    updateData(readerDataHolder, request, pagePosition);
                }
                if (baseCallback != null) {
                    baseCallback.onFinally();
                }
            }
        });
    }

    private void updateData(ReaderDataHolder readerDataHolder, ReaderBaseRequest request, String pagePosition) {
        readerDataHolder.setReaderUserDataInfo(request.getReaderUserDataInfo());
        readerDataHolder.setReaderViewInfo(request.getReaderViewInfo());

    }
}
