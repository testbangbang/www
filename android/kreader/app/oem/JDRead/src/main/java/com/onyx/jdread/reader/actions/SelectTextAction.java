package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.common.SelectWordInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.ReaderBaseRequest;
import com.onyx.jdread.reader.request.SelectRequest;

/**
 * Created by huxiaomao on 2016/6/3.
 */
public class SelectTextAction extends BaseReaderAction {
    private SelectWordInfo selectWordInfo;

    public SelectTextAction(SelectWordInfo selectWordInfo) {
        this.selectWordInfo = selectWordInfo;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final SelectRequest request = new SelectRequest(readerDataHolder.getReader(),
                selectWordInfo.pagePosition,
                selectWordInfo.startPoint,
                selectWordInfo.endPoint,
                selectWordInfo.touchPoint,
                ReaderHitTestOptionsImpl.create(false),
                readerDataHolder.getReaderViewInfo().getPageInfo(selectWordInfo.pagePosition),false);

        final String pagePosition = readerDataHolder.getCurrentPagePosition();
        readerDataHolder.getReaderSelectionInfo().increaseSelectCount();
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getReaderSelectionInfo().updateSelectInfo(request.getSelectionInfoManager().getReaderSelectionInfos());
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }

            @Override
            public void onFinally() {
                readerDataHolder.getReaderSelectionInfo().decreaseSelectCount();
                updateData(readerDataHolder, request, pagePosition, baseCallback);
            }
        });
    }

    private void updateData(ReaderDataHolder readerDataHolder, ReaderBaseRequest request, String pagePosition, RxCallback baseCallback) {
        readerDataHolder.setReaderUserDataInfo(request.getReaderUserDataInfo());
        readerDataHolder.setReaderViewInfo(request.getReaderViewInfo());
        if (baseCallback != null) {
            baseCallback.onFinally();
        }
    }
}