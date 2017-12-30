package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingFontSizeAction extends BaseReaderAction {
    private int fontSize;
    private ReaderTextStyle style;

    public SettingFontSizeAction(ReaderTextStyle style,int fontSize) {
        this.fontSize = fontSize;
        this.style = style;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        ReaderTextStyle.SPUnit oldFontSize = style.getFontSize();
        oldFontSize.setValue(fontSize);
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
