package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.List;

/**
 * Created by zhuzeng on 9/20/16.
 */
public class FlushShapeListRequest extends ReaderBaseNoteRequest {

    private volatile int subPageIndex;
    private volatile List<Shape> shapeList;
    private volatile boolean saveDocument;

    public FlushShapeListRequest(final List<PageInfo> pages, final List<Shape> list, int spi, boolean r, boolean save) {
        setAbortPendingTasks(true);
        setRender(r);
        setVisiblePages(pages);
        subPageIndex = spi;
        shapeList = list;
        saveDocument = save;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        for(Shape shape : shapeList) {
            final ReaderNotePage readerNotePage = noteManager.getNoteDocument().ensurePage(getContext(), shape.getPageUniqueId(), subPageIndex);
            readerNotePage.addShape(shape, true);
        }
        if (isRender()) {
            getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        }
        if (saveDocument) {
            noteManager.getNoteDocument().save(getContext(), "title");
        }
        updateShapeDataInfo(noteManager);
    }

}
