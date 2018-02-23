package com.onyx.jdread.reader.actions;

import android.content.ClipboardManager;
import android.content.Context;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class SelectTextCopyToClipboardAction extends BaseReaderAction {
    private String text;
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        text = readerDataHolder.getReaderSelectionInfo().getSelectText();

        new CleanSelectionAction().execute(readerDataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                showMessage(readerDataHolder);
            }
        });


    }

    private void showMessage(ReaderDataHolder readerDataHolder){
        ClipboardManager clipboardManager = (ClipboardManager) readerDataHolder.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
        ToastMessage.showMessageCenter(readerDataHolder.getAppContext(),readerDataHolder.getAppContext().getString(R.string.reader_copy_success));
    }
}
