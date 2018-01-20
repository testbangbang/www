package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingLeftAndRightSpacingAction extends BaseReaderAction {
    private int margin;
    private ReaderTextStyle style;

    public SettingLeftAndRightSpacingAction(ReaderTextStyle style,int margin) {
        this.margin = margin;
        this.style = style;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ReaderTextStyle.PageMargin pageMargin = style.getPageMargin();

        ReaderTextStyle.Percentage leftMargin = pageMargin.getLeftMargin();
        leftMargin.setPercent(margin);
        ReaderTextStyle.Percentage rightMargin = pageMargin.getRightMargin();
        rightMargin.setPercent(margin);
        new SettingTextStyleRequest(readerDataHolder.getReader(),style).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
