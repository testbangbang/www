package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingParagraphSpacingAction extends BaseReaderAction {
    private ReaderTextStyle style;
    private int paragraphSpacing;

    public SettingParagraphSpacingAction(ReaderTextStyle style, int paragraphSpacing) {
        this.style = style;
        this.paragraphSpacing = paragraphSpacing;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ReaderTextStyle.Percentage oldParagraphSpacing = style.getParagraphSpacing();
        oldParagraphSpacing.setPercent(paragraphSpacing);
        final SettingTextStyleRequest request = new SettingTextStyleRequest(readerDataHolder.getReader(),style);
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
