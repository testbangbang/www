package com.onyx.jdread.reader.menu.actions;


import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.ChangeChineseConvertTypeRequest;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class ChangeChineseConvertTypeAction extends BaseReaderAction {
    private ReaderChineseConvertType convertType;

    public ChangeChineseConvertTypeAction(final ReaderChineseConvertType convertType) {
        this.convertType = convertType;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        new ChangeChineseConvertTypeRequest(readerDataHolder,convertType).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
