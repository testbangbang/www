package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.SettingTextStyleRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingRowSpacingAction extends BaseAction {
    private int lineSpacing;
    private ReaderTextStyle style;

    public SettingRowSpacingAction(ReaderTextStyle style,int lineSpacing) {
        this.lineSpacing = lineSpacing;
        this.style = style;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        ReaderTextStyle.Percentage oldLineSpacing = style.getLineSpacing();
        oldLineSpacing.setPercent(lineSpacing);
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
