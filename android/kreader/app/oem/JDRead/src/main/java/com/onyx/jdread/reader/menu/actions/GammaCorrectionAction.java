package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.common.GammaInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.GammaCorrectionRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class GammaCorrectionAction extends BaseReaderAction {
    private GammaInfo gammaInfo;

    public GammaCorrectionAction(GammaInfo gammaInfo) {
        this.gammaInfo = gammaInfo;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        new GammaCorrectionRequest(readerDataHolder,gammaInfo).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
