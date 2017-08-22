package com.onyx.edu.reader.note.request;

import android.util.Log;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNotePage;

import java.util.List;

/**
 * Created by zhuzeng on 9/30/16.
 */

public class RemoveShapesByTouchPointListRequest extends ReaderBaseNoteRequest {

    private volatile TouchPointList touchPointList;
    private List<String> removedShapeList;
    public RemoveShapesByTouchPointListRequest(final List<PageInfo> pageInfoList, final TouchPointList pointList) {
        setVisiblePages(pageInfoList);
        touchPointList = pointList;
        setResetNoteDataInfo(false);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        if (noteManager.getParent().getHandlerManager().lockShapeByDocumentStatus() && noteManager.getNoteDocument().isLock()) {
            return;
        }
        benchmarkStart();
        final PageInfo pageInfo = getVisiblePages().get(0);
        final float radius = noteManager.getNoteDrawingArgs().eraserRadius / pageInfo.getActualScale();
        final ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(), pageInfo.getName(), 0);
        boolean changed = false;
        boolean lockShapeByRevision = noteManager.getParent().getHandlerManager().lockShapeByRevision();
        int documentReviewRevision = noteManager.getNoteDocument().getReviewRevision();
        if (notePage != null) {
            changed |= notePage.removeShapesByTouchPointList(touchPointList, radius, lockShapeByRevision, documentReviewRevision);
            removedShapeList = notePage.getRemovedShapeIdList();
        }
        changed |= renderVisiblePages(noteManager);
        getNoteDataInfo().setContentRendered(changed);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
        Log.e("############", "erase takes: " + benchmarkEnd() + " changed: " + changed);
    }

    public List<String> getRemovedShapeList() {
        return removedShapeList;
    }
}
