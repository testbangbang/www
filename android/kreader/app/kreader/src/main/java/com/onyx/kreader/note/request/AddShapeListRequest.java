package com.onyx.kreader.note.request;

import android.graphics.Rect;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/20/16.
 */
public class AddShapeListRequest extends ReaderBaseNoteRequest {

    private volatile String pageName;
    private volatile int subPageIndex;
    private volatile List<Shape> shapeList;

    public AddShapeListRequest(final List<Shape> list, final String pn, int spi) {
        setAbortPendingTasks(true);
        pageName = pn;
        subPageIndex = spi;
        shapeList = list;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        final ReaderNotePage readerNotePage = noteManager.getNoteDocument().ensurePage(getContext(), pageName, subPageIndex);
        readerNotePage.addShapeList(shapeList);
        getShapeDataInfo().setContentRendered(renderVisiblePages(noteManager));
        updateShapeDataInfo(noteManager);
    }

}
