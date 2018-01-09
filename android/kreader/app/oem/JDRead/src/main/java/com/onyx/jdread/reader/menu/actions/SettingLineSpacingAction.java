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

public class SettingLineSpacingAction extends BaseReaderAction {
    private int lineSpacing;
    private ReaderTextStyle style;

    public SettingLineSpacingAction(ReaderTextStyle style, int lineSpacing) {
        this.lineSpacing = lineSpacing + ReaderTextStyle.SMALL_LINE_SPACING.getPercent();
        this.style = style;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        ReaderTextStyle.Percentage oldLineSpacing = style.getLineSpacing();
        oldLineSpacing.setPercent(lineSpacing);
        final SettingTextStyleRequest request = new SettingTextStyleRequest(readerDataHolder,style);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(request);
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
