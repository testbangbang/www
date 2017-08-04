package com.onyx.edu.reader.ui.handler.form;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.actions.FlushSignatureShapesAction;
import com.onyx.edu.reader.note.actions.FlushSignatureShapesChain;
import com.onyx.edu.reader.ui.actions.ShowReaderMenuAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.handler.BaseHandler;
import com.onyx.edu.reader.ui.handler.HandlerManager;

/**
 * Created by ming on 2017/8/2.
 */

public class FormSignHandler extends FormBaseHandler {

    public FormSignHandler(HandlerManager parent) {
        super(parent);
    }

    @Override
    public void onActivate(ReaderDataHolder readerDataHolder, HandlerInitialState initialState) {
        super.onActivate(readerDataHolder, initialState);
    }

    @Override
    public boolean isEnableNoteDrawing() {
        return true;
    }

    @Override
    public void close(final ReaderDataHolder readerDataHolder) {
        new FlushSignatureShapesChain().execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                FormSignHandler.super.close(readerDataHolder);
            }
        });
    }
}
