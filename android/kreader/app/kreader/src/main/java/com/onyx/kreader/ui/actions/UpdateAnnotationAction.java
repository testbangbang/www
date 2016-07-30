package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.host.request.UpdateAnnotationRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

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
    public void execute(ReaderDataHolder readerDataHolder) {
        execute(readerDataHolder,null);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        annotation.setNote(note);
        readerDataHolder.submitRenderRequest(new UpdateAnnotationRequest(annotation), baseCallback);
    }
}
