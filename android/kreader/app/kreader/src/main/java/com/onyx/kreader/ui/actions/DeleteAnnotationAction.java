package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.host.request.DeleteAnnotationRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by joy on 7/7/16.
 */
public class DeleteAnnotationAction extends BaseAction {

    private Annotation annotation;

    public DeleteAnnotationAction(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        readerDataHolder.submitRenderRequest(new DeleteAnnotationRequest(annotation), baseCallback);
    }
}
