package com.onyx.jdread.reader.actions;

import android.content.ClipboardManager;
import android.content.Context;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/1/20.
 */

public class SelectTextCopyToClipboardAction extends BaseReaderAction {
    private Annotation annotation;

    public SelectTextCopyToClipboardAction(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        String text = "";
        if(annotation != null){
            text = annotation.getQuote();
        }else {
            text = readerDataHolder.getReaderSelectionInfo().getSelectText();
        }

        ClipboardManager clipboardManager = (ClipboardManager) readerDataHolder.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
        ToastMessage.showMessageCenter(readerDataHolder.getAppContext(),readerDataHolder.getAppContext().getString(R.string.reader_copy_success));
    }
}
