package com.onyx.jdread.reader.actions;

import android.content.DialogInterface;
import android.util.Log;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.DialogReaderLoading;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.CloseDocumentEvent;
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
    private DialogReaderLoading dlgLoading;

    public InitPageViewAction(DialogReaderLoading dlgLoading) {
        this.dlgLoading = dlgLoading;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        final InitFirstPageViewRequest request = new InitFirstPageViewRequest(readerDataHolder.getReader());
        dlgLoading.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                request.setAbort(true);
                readerDataHolder.getEventBus().post(new CloseDocumentEvent());
            }
        });

        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                dlgLoading.dismiss();
                if (request.getAbort()) {
                    return;
                }
                readerDataHolder.setInitViewPage(true);
                readerDataHolder.setSettingInfo(request.getSettingInfo());
                readerDataHolder.setGammaInfo(request.getGammaInfo());
                readerDataHolder.getEventBus().post(new InitPageViewInfoEvent(request.getReaderViewInfo()));
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);
                if(!readerDataHolder.isPreload()) {
                    PreloadNextScreenRequest preloadNextScreenRequest = new PreloadNextScreenRequest(readerDataHolder.getReader());
                    preloadNextScreenRequest.execute(null);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                dlgLoading.dismiss();
                if (request.getAbort()) {
                    return;
                }
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }
}
