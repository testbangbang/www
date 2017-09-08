package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.request.LoadFormShapeByIdRequest;
import com.onyx.edu.reader.note.request.LoadFormShapesRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by lxm on 2017/8/22.
 */

public class LoadFormShapeByIdAction extends BaseAction {

    private String formId;
    private ReaderFormShapeModel formShapeModel;

    public LoadFormShapeByIdAction(String formId) {
        this.formId = formId;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final LoadFormShapeByIdRequest shapesRequest = new LoadFormShapeByIdRequest(readerDataHolder.getReader().getDocumentMd5(), formId);
        readerDataHolder.submitNonRenderRequest(shapesRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                formShapeModel = shapesRequest.getFormShapeModel();
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    public ReaderFormShapeModel getFormShapeModel() {
        return formShapeModel;
    }
}
