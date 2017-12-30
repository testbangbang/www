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

    public SettingTypefaceAction(String typefacePath) {
        this.typefacePath = typefacePath;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        ReaderTextStyle style = readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle();
        style.setFontFace(typefacePath);

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
