package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.requests.ExportNotesRequest;
import com.onyx.kreader.note.actions.GetAllShapesAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.HashMap;

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
        final ExportNotesRequest request = new ExportNotesRequest(documentInfoAction.getAnnotations(), shapesAction.getShapes(), shapesAction.getSubPageMap());
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), request, baseCallback);
    }
}
