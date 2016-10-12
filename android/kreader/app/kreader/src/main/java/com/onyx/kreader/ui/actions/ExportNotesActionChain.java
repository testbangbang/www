package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.host.request.ExportNotesRequest;
import com.onyx.kreader.note.actions.GetAllShapesAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ExportNotesActionChain extends BaseAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        ActionChain chain = new ActionChain();
        GetDocumentInfoAction documentInfoAction = new GetDocumentInfoAction();
        GetAllShapesAction shapesAction = new GetAllShapesAction();
        ExportNotesAction exportNotesAction = new ExportNotesAction(documentInfoAction, shapesAction);
        chain.addAction(documentInfoAction);
        chain.addAction(shapesAction);
        chain.addAction(exportNotesAction);
        chain.execute(readerDataHolder, baseCallback);
    }
}
