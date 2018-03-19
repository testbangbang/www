package com.onyx.android.note.action;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.note.event.PenEvent;
import com.onyx.android.note.event.data.UpdateNoteDrawingArgsEvent;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.CreateDocumentRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.data.DocumentOptionArgs;

/**
 * Created by lxm on 2018/2/25.
 */

public class CreateDocumentAction extends BaseNoteAction {

    private String documentUniqueId;
    private String parentUniqueId;
    private DocumentOptionArgs optionArgs;

    public CreateDocumentAction(NoteManager noteManager) {
        super(noteManager);
    }

    public CreateDocumentAction setDocumentUniqueId(String documentUniqueId) {
        this.documentUniqueId = documentUniqueId;
        return this;
    }

    public CreateDocumentAction setParentUniqueId(String parentUniqueId) {
        this.parentUniqueId = parentUniqueId;
        return this;
    }

    public CreateDocumentAction setOptionArgs(DocumentOptionArgs optionArgs) {
        this.optionArgs = optionArgs;
        return this;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        final CreateDocumentRequest request = new CreateDocumentRequest(getNoteManager())
                .setDocumentUniqueId(documentUniqueId)
                .setParentUniqueId(parentUniqueId)
                .setDocumentOptionArgs(optionArgs);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<CreateDocumentRequest>() {
            @Override
            public void onNext(@NonNull CreateDocumentRequest createDocumentRequest) {
                RxCallback.onNext(rxCallback, createDocumentRequest);
                getNoteManager().post(new UpdateNoteDrawingArgsEvent(createDocumentRequest.getDrawingArgs()));
                getNoteManager().post(PenEvent.resumeDrawingRender());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                super.onError(e);
                RxCallback.onError(rxCallback, e);
            }
        });
    }
}
