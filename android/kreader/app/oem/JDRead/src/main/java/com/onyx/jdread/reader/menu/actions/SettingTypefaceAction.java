package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingTypefaceAction extends BaseReaderAction {
    private String typefacePath;
    private ReaderTextStyle style;

    public SettingTypefaceAction(ReaderTextStyle style, String typefacePath) {
        this.typefacePath = typefacePath;
        this.style = style;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        style.setFontFace(typefacePath);

        final SettingTextStyleRequest request = new SettingTextStyleRequest(readerDataHolder.getReader(), style);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateViewSetting(readerDataHolder, null, request.getStyle(), null);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
