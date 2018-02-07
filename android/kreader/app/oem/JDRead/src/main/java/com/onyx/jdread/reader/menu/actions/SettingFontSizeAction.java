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
    public ReaderConfig.PresetSixStyle presetSixStyle;
    private ReaderTextStyle style;

    public SettingFontSizeAction(ReaderTextStyle style,ReaderConfig.PresetSixStyle presetSixStyle) {
        this.presetSixStyle = presetSixStyle;
        this.style = style;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ReaderTextStyle.SPUnit oldFontSize = style.getFontSize();
        oldFontSize.setValue(presetSixStyle.fontSize);

        ReaderTextStyle.Percentage lineSpacing = style.getLineSpacing();
        lineSpacing.setPercent(presetSixStyle.lineSpacing);

        ReaderTextStyle.Percentage paragraphSpacing = style.getParagraphSpacing();
        paragraphSpacing.setPercent(presetSixStyle.paragraphSpacing);

        ReaderTextStyle.PageMargin pageMargin = style.getPageMargin();
        pageMargin.setLeftMargin(ReaderTextStyle.Percentage.create(presetSixStyle.marginLeft));
        pageMargin.setTopMargin(ReaderTextStyle.Percentage.create(presetSixStyle.marginTop));
        pageMargin.setRightMargin(ReaderTextStyle.Percentage.create(presetSixStyle.marginRight));
        pageMargin.setBottomMargin(ReaderTextStyle.Percentage.create(presetSixStyle.marginBottom));

        final  SettingTextStyleRequest request = new SettingTextStyleRequest(readerDataHolder.getReader(),style,readerDataHolder.getSettingInfo());
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
