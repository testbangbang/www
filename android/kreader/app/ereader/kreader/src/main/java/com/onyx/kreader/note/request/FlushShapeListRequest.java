package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import org.apache.lucene.analysis.cn.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/20/16.
 */
public class FlushShapeListRequest extends ReaderBaseNoteRequest {

    private volatile int subPageIndex;
    private volatile List<Shape> shapeList = new ArrayList<>();
    private volatile boolean saveDocument;
    private volatile int count;
    private volatile boolean pause = false;

    public FlushShapeListRequest(final List<PageInfo> pages, final List<Shape> list, int spi, boolean r, boolean t, boolean save) {
        setAbortPendingTasks(false);
        setRender(r);
        setTransfer(t);
        setVisiblePages(pages);
        subPageIndex = spi;
        shapeList.addAll(list);
        saveDocument = save;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape() && !isPause());
        ensureDocumentOpened(noteManager);
        for(Shape shape : shapeList) {
            final ReaderNotePage readerNotePage = noteManager.getNoteDocument().ensurePageExist(getContext(), shape.getPageUniqueId(), shape.getSubPageUniqueId());
            if (readerNotePage != null) {
                readerNotePage.addShape(shape, true);
                count += readerNotePage.getNewAddedShapeList().size();
            }
        }

        if (count != shapeList.size() && BuildConfig.DEBUG) {
            Debug.e(this.getClass(),  "adding error: " + " origin size: " + shapeList.size() + " result: " + count);
        }
        if (isRender()) {
            getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        }
        if (saveDocument) {
            noteManager.getNoteDocument().save(getContext(), "title");
        }
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
}
