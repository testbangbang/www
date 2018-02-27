package com.onyx.jdread.reader.actions;

import android.util.Log;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.InitPageViewInfoEvent;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.InitFirstPageViewRequest;
import com.onyx.jdread.reader.request.PreloadNextScreenRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class InitPageViewAction extends BaseReaderAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        final InitFirstPageViewRequest request = new InitFirstPageViewRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.setSettingInfo(request.getSettingInfo());
                readerDataHolder.setGammaInfo(request.getGammaInfo());
                readerDataHolder.getEventBus().post(new InitPageViewInfoEvent(request.getReaderViewInfo()));
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);

                PreloadNextScreenRequest preloadNextScreenRequest = new PreloadNextScreenRequest(readerDataHolder.getReader());
                preloadNextScreenRequest.execute(null);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }
}
