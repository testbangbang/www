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
public class RenderShapeListInBackgroundRequest extends ReaderBaseNoteRequest {

    private volatile List<Shape> shapeList = new ArrayList<>();

    public RenderShapeListInBackgroundRequest(final List<PageInfo> pages, final List<Shape> list) {
        setPauseRawInputProcessor(false);
        setResumeRawInputProcessor(false);
        setAbortPendingTasks(false);
        setRender(true);
        setTransfer(true);
        setVisiblePages(pages);
        shapeList.addAll(list);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);

        int count = 0;
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

        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
    }

}
