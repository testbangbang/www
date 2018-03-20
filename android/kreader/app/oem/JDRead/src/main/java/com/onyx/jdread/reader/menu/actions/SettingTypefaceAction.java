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

public class SettingTypefaceAction extends BaseReaderAction {
    private String typefacePath;
    private ReaderTextStyle style;
    private int styleIndex;
    private int settingType;
    private int currentLineSpacing;

    public SettingTypefaceAction(ReaderTextStyle style, String typefacePath,int styleIndex,int settingType,int currentLineSpacing) {
        this.typefacePath = typefacePath;
        this.style = style;
        this.styleIndex = styleIndex;
        this.settingType = settingType;
        this.currentLineSpacing = currentLineSpacing;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        style.setFontFace(typefacePath);

        int spacing = ReaderConfig.getAdditionalSpacing(style.getFontFace(),styleIndex,
                currentLineSpacing,settingType);

        style.setLineSpacing(ReaderTextStyle.Percentage.create(spacing));

        final SettingTextStyleRequest request = new SettingTextStyleRequest(readerDataHolder.getReader(), style,readerDataHolder.getSettingInfo());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder, request);
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }
}
