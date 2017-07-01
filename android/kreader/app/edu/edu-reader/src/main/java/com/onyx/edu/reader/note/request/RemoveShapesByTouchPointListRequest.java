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
    public RemoveShapesByTouchPointListRequest(final List<PageInfo> pageInfoList, final TouchPointList pointList) {
        setVisiblePages(pageInfoList);
        touchPointList = pointList;
        setResetNoteDataInfo(false);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        benchmarkStart();
        final PageInfo pageInfo = getVisiblePages().get(0);
        final float radius = noteManager.getNoteDrawingArgs().eraserRadius / pageInfo.getActualScale();
        final ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(), pageInfo.getName(), 0);
        boolean changed = false;
        if (notePage != null) {
            changed |= notePage.removeShapesByTouchPointList(touchPointList, radius);
        }
        changed |= renderVisiblePages(noteManager);
        getNoteDataInfo().setContentRendered(changed);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
        Log.e("############", "erase takes: " + benchmarkEnd() + " changed: " + changed);
    }
}
