package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.host.request.UpdateAnnotationRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by joy on 7/7/16.
 */
public class UpdateAnnotationAction extends BaseAction {

    private Annotation annotation;
    private String note;

    public UpdateAnnotationAction(Annotation annotation, String note) {
        this.annotation = annotation;
        this.note = note;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        annotation.setNote(note);
        readerDataHolder.submitRenderRequest(new UpdateAnnotationRequest(annotation), baseCallback);
    }
}
