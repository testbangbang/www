package com.onyx.jdread.reader.menu.actions;


import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.request.ChangeLayoutRequest;
import com.onyx.jdread.reader.menu.request.GammaCorrectionRequest;

/**
 * Created by huxiaomao on 2018/1/1.
 */

public class ResetNavigationAction extends BaseReaderAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        resetGammaCorrection(readerDataHolder, baseCallback);
    }

    private void resetGammaCorrection(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        GammaInfo gammaInfo = new GammaInfo();
        gammaInfo.setEmboldenLevel(ReaderConfig.TypefaceColorDepth.LEVEL_ONE);
        final GammaCorrectionRequest request = new GammaCorrectionRequest(readerDataHolder.getReader(), gammaInfo, readerDataHolder.getSettingInfo());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                changeLayout(readerDataHolder, baseCallback);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });
    }

    private void changeLayout(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        ChangeLayoutParameter parameter = new ChangeLayoutParameter(PageConstants.SINGLE_PAGE, new NavigationArgs());
        final ChangeLayoutRequest request = new ChangeLayoutRequest(readerDataHolder.getReader(), parameter, null,
                readerDataHolder.getCurrentPageName());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable, this.getClass().getSimpleName(), readerDataHolder.getEventBus());
            }
        });
    }
}
