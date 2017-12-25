package com.onyx.edu.reader.note.request;

import android.util.Log;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNotePage;

import java.util.ArrayList;
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
        List<TouchPointList> normalizedList = normalizeOnPage(pageInfo);

        final float radius = noteManager.getNoteDrawingArgs().eraserRadius / pageInfo.getActualScale();
        final ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(), pageInfo.getName(), 0);
        boolean changed = false;
        if (notePage != null) {
            for (TouchPointList list : normalizedList) {
                changed |= notePage.removeShapesByTouchPointList(list, radius);
            }
        }
        changed |= renderVisiblePages(noteManager);
        getNoteDataInfo().setContentRendered(changed);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
        Log.e("############", "erase takes: " + benchmarkEnd() + " changed: " + changed);
    }

    private List<TouchPointList> normalizeOnPage(PageInfo pageInfo) {
        List<TouchPointList> result = new ArrayList<>();
        TouchPointList currentList = null;
        for (TouchPoint p : touchPointList.getPoints()) {
            if (!pageInfo.getDisplayRect().contains(p.getX(), p.getY())) {
                if (currentList != null) {
                    result.add(currentList);
                }
                currentList = null;
            } else {
                if (currentList == null) {
                    currentList = new TouchPointList();
                }
                TouchPoint pp = new TouchPoint(p);
                pp.normalize(pageInfo);
                currentList.add(pp);
            }
        }
        if (currentList != null) {
            result.add(currentList);
        }
        return result;
    }
}
