package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.note.actions.GetAllShapesAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ExportNotesActionChain extends BaseAction {

    private boolean exportAnnotation = false;
    private boolean exportScribble = false;

    public ExportNotesActionChain(boolean exportAnnotation, boolean exportScribble) {
        this.exportAnnotation = exportAnnotation;
        this.exportScribble = exportScribble;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        showLoadingDialog(readerDataHolder, R.string.exporting);
        ActionChain chain = new ActionChain();
        GetDocumentInfoAction documentInfoAction = new GetDocumentInfoAction();
        GetAllShapesAction shapesAction = new GetAllShapesAction();
        ExportNotesAction exportNotesAction = new ExportNotesAction(documentInfoAction, shapesAction);
        if (exportAnnotation) {
            chain.addAction(documentInfoAction);
        }
        if (exportScribble) {
            chain.addAction(shapesAction);
        }
        chain.addAction(exportNotesAction);
        chain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                if (baseCallback != null) {
                    baseCallback.done(request, e);
                }
            }
        });
    }
}
