package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingFontSizeAction extends BaseAction {
    private int fontSize;

    public SettingFontSizeAction(int fontSize) {
        this.fontSize = fontSize;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        ReaderTextStyle style = readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle();
        ReaderTextStyle.SPUnit oldFontSize = style.getFontSize();
        oldFontSize.setValue(fontSize);
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
