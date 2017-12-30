package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingUpAndDownSpacingAction extends BaseReaderAction {
    private int margin;
    private ReaderTextStyle style;

    public SettingUpAndDownSpacingAction(ReaderTextStyle style, int margin) {
        this.margin = margin;
        this.style = style;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        ReaderTextStyle.PageMargin pageMargin = style.getPageMargin();

        ReaderTextStyle.Percentage topMargin = pageMargin.getTopMargin();
        topMargin.setPercent(margin);
        ReaderTextStyle.Percentage bottomMargin = pageMargin.getBottomMargin();
        bottomMargin.setPercent(margin);
        new SettingTextStyleRequest(readerDataHolder,style).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
