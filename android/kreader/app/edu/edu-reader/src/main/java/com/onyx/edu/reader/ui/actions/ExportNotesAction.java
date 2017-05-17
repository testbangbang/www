package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.requests.ExportNotesRequest;
import com.onyx.edu.reader.note.actions.GetAllShapesAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * relying on the data of proceeding GetDocumentInfoAction and GetAllShapesAction
 *
 * Created by zhuzeng on 5/17/16.
 */
public class ExportNotesAction extends BaseAction {

    GetDocumentInfoAction documentInfoAction;
    GetAllShapesAction shapesAction;

    public ExportNotesAction(GetDocumentInfoAction documentInfoAction, GetAllShapesAction shapesAction) {
        this.documentInfoAction = documentInfoAction;
        this.shapesAction = shapesAction;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        final ExportNotesRequest request = new ExportNotesRequest(documentInfoAction.getAnnotations(), shapesAction.getShapes());
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), request, baseCallback);
    }
}
