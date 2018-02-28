package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.GotoPositionRequest;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GotoPositionAction extends BaseReaderAction {
    private String pagePosition;

    public GotoPositionAction(final String pagePosition) {
        this.pagePosition = pagePosition;
    }

    public GotoPositionAction(final int position) {
        this(PagePositionUtils.fromPosition(position));
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        final GotoPositionRequest request = new GotoPositionRequest(readerDataHolder.getReader(), pagePosition);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
                if(baseCallback != null){
                    baseCallback.onNext(o);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });

    }
}
