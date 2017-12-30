package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingTypefaceAction extends BaseAction {
    private String typefacePath;
    private ReaderTextStyle style;

    public SettingTypefaceAction(ReaderTextStyle style, String typefacePath) {
        this.typefacePath = typefacePath;
        this.style = style;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        style.setFontFace(typefacePath);

        new SettingTextStyleRequest(readerDataHolder, style).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
