package com.onyx.kreader.ui.actions;

import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.host.request.DeleteAnnotationRequest;
import com.onyx.kreader.host.request.UpdateAnnotationRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by joy on 7/7/16.
 */
public class DeleteAnnotationAction extends BaseAction {

    private Annotation annotation;

    public DeleteAnnotationAction(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        readerActivity.submitRequest(new DeleteAnnotationRequest(annotation));
    }
}
