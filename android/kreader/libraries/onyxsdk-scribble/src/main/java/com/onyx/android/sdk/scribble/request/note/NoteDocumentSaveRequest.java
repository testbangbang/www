package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentSaveRequest extends BaseNoteRequest {

    private volatile String title;
    private volatile boolean close;
    private volatile boolean resume = true;

    public NoteDocumentSaveRequest(final String t, boolean c) {
        this(t, c, true);
    }

    public NoteDocumentSaveRequest(final String t, boolean c , boolean r) {
        title = t;
        close = c;
        resume = r;
        setPauseInputProcessor(true);
        setResumeInputProcessor(!close && resume);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        renderCurrentPage(parent);
        resetEraserShapeTypeIfCloseDocument(parent);
        NoteDataProvider.saveThumbnailWithSize(getContext(),
                parent.getNoteDocument().getDocumentUniqueId(),
                parent.getRenderBitmap(),
                512,
                512);
        parent.save(getContext(), title, close);
    }

    private void resetEraserShapeTypeIfCloseDocument(NoteViewHelper helper) {
        if (close && helper.getCurrentShapeType() == ShapeFactory.SHAPE_ERASER) {
            helper.setCurrentShapeType(NoteDrawingArgs.defaultShape());
        }
    }

}
