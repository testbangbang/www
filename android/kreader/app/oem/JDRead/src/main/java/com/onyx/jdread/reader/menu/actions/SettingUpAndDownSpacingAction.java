package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingUpAndDownSpacingAction extends BaseAction {
    private int margin;

    public SettingUpAndDownSpacingAction(int margin) {
        this.margin = margin;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        ReaderTextStyle style = readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle();
        ReaderTextStyle.PageMargin pageMargin = style.getPageMargin();

        ReaderTextStyle.Percentage topMargin = pageMargin.getTopMargin();
        topMargin.setPercent(margin);
        ReaderTextStyle.Percentage bottomMargin = pageMargin.getBottomMargin();
        bottomMargin.setPercent(margin);
        new SettingTextStyleRequest(readerDataHolder).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
