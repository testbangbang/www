package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.note.request.FlushSignatureShapesRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/8/3.
 */

public class FlushSignatureShapesAction extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        if (!readerDataHolder.inSignatureFormProvider()) {
            return;
        }
        if (readerDataHolder.getAccount() == null) {
            return;
        }
        FlushSignatureShapesRequest signatureShapesRequest = new FlushSignatureShapesRequest(
                readerDataHolder.getAccount()._id,
                readerDataHolder.getReader().getDocumentMd5());
        readerDataHolder.submitNonRenderRequest(signatureShapesRequest, baseCallback);
    }
}
