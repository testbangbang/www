package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingFontSizeAction extends BaseReaderAction {
    private int styleIndex;
    private ReaderTextStyle style;

    public SettingFontSizeAction(ReaderTextStyle style,int styleIndex) {
        this.styleIndex = styleIndex;
        this.style = style;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ReaderTextStyle presetStyle = ReaderConfig.presetStyle.get(styleIndex);

        style.setFontSize(presetStyle.getFontSize());
        style.setLineSpacing(presetStyle.getLineSpacing());
        style.setPageMargin(presetStyle.getPageMargin());
        style.setParagraphSpacing(presetStyle.getParagraphSpacing());

        final  SettingTextStyleRequest request = new SettingTextStyleRequest(readerDataHolder.getReader(), this.style,readerDataHolder.getSettingInfo());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }
}
