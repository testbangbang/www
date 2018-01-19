package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.common.SelectWordInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;
import com.onyx.jdread.reader.request.SelectRequest;

/**
 * Created by huxiaomao on 2016/6/3.
 */
public class SelectTextAction extends BaseReaderAction {
    private SelectWordInfo selectWordInfo;

    public SelectTextAction(SelectWordInfo selectWordInfo,ActionCallBack callBack) {
        this.selectWordInfo = selectWordInfo;
        this.callBack = callBack;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        final SelectRequest request = new SelectRequest(readerDataHolder.getReader(),
                selectWordInfo.pagePosition,
                selectWordInfo.startPoint,
                selectWordInfo.endPoint,
                selectWordInfo.touchPoint,
                ReaderHitTestOptionsImpl.create(false),
                readerDataHolder.getReaderSelectionManager());

        final String pagePosition = readerDataHolder.getCurrentPagePosition();
        readerDataHolder.getReaderSelectionManager().incrementSelectCount();
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getReaderSelectionManager().decrementSelectCount();
                updateData(readerDataHolder,request,pagePosition);
            }

            @Override
            public void onError(Throwable throwable) {
                readerDataHolder.getReaderSelectionManager().decrementSelectCount();
            }
        });
    }

    private void updateData(ReaderDataHolder readerDataHolder, ReaderBaseRequest request,String pagePosition){
        readerDataHolder.setReaderUserDataInfo(request.getReaderUserDataInfo());
        readerDataHolder.setReaderViewInfo(request.getReaderViewInfo());
        if(callBack != null){
            callBack.onFinally(pagePosition);
        }
    }
}