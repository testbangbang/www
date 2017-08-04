package com.onyx.edu.reader.note.actions;

import android.graphics.RectF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.request.LoadSignatureToFormRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/8/3.
 */

public class LoadSignatureToFormAction extends BaseAction {

    private String accountId;
    private RectF targetRect;

    public LoadSignatureToFormAction(String accountId, RectF targetRect) {
        this.accountId = accountId;
        this.targetRect = targetRect;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        LoadSignatureToFormRequest signatureShapesRequest = new LoadSignatureToFormRequest(accountId, targetRect, readerDataHolder.getFirstPageInfo());
        noteManager.submit(readerDataHolder.getContext(), signatureShapesRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
