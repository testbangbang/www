package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.catalog.event.GetDocumentInfoResultEvent;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.GetDocumentInfoRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class GetDocumentInfoAction extends BaseReaderAction {
    private ReaderUserDataInfo readerUserDataInfo;

    public GetDocumentInfoAction(ReaderUserDataInfo readerUserDataInfo) {
        this.readerUserDataInfo = readerUserDataInfo;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        final GetDocumentInfoRequest request = new GetDocumentInfoRequest(readerUserDataInfo, readerDataHolder);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetDocumentInfoResultEvent event = new GetDocumentInfoResultEvent(request.getResult());
                EventBus.getDefault().post(event);
            }
        });
    }
}
