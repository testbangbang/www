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
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        ReaderTextStyle style = readerDataHolder.getStyleCopy();
        final String pagePosition = readerDataHolder.getCurrentPagePosition();

        final PreviousPageSelectTextRequest request = new PreviousPageSelectTextRequest(readerDataHolder.getReader(), style);
        readerDataHolder.getSelectionInfoManager().incrementSelectCount();
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getSelectionInfoManager().updateSelectInfo(request.getSelectionInfoManager().getReaderSelectionInfos());
            }

            @Override
            public void onFinally() {
                readerDataHolder.getSelectionInfoManager().decrementSelectCount();
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
