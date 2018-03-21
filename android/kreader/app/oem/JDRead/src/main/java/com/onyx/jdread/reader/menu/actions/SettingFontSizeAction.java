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
    private int settingType;

    public SettingFontSizeAction(ReaderTextStyle style,int styleIndex,int settingType) {
        this.styleIndex = styleIndex;
        this.style = style;
        this.settingType = settingType;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ReaderTextStyle presetStyle = ReaderConfig.presetStyle.get(styleIndex);

        int spacing = ReaderConfig.getAdditionalSpacing(style.getFontFace(),styleIndex,
                presetStyle.getLineSpacing().getPercent(),settingType);

        style.setLineSpacing(ReaderTextStyle.Percentage.create(spacing));
        style.setFontSize(presetStyle.getFontSize());
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
