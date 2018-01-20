package com.onyx.jdread.reader.highlight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.jdread.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 4/6/14
 * Time: 3:00 PM
 * * update selection
 * * move by user
 * * move by caller
 * * draw
 */
public class ReaderSelectionManager {
    private Map<String, ReaderSelectionInfo> readerSelectionInfos = new HashMap<>();
    private int moveSelectCount = 0;

    public synchronized void incrementSelectCount() {
        moveSelectCount++;
    }

    public synchronized void decrementSelectCount() {
        moveSelectCount--;
    }

    public synchronized int getMoveSelectCount() {
        return moveSelectCount;
    }

    public synchronized void setMoveSelectCount(int selectCount) {
        this.moveSelectCount = selectCount;
    }

    public ReaderSelectionManager() {
        super();
    }

    public ReaderSelection getCurrentSelection(String pagePosition) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            return readerSelectionInfo.getCurrentSelection();
        }
        return null;
    }

    public ReaderSelectionInfo getReaderSelectionInfo(String pagePosition) {
        return readerSelectionInfos.get(pagePosition);
    }

    public HighlightCursor getHighlightCursor(String pagePosition, int index) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            if (index >= 0 && index < readerSelectionInfo.getCursors().size()) {
                return readerSelectionInfo.getCursors().get(index);
            }
        }
        return null;
    }

    public boolean normalize(String pagePosition) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            RectF begin = readerSelectionInfo.getCursors().get(0).getHotPoint();
            RectF end = readerSelectionInfo.getCursors().get(1).getHotPoint();
            if ((begin.top > end.top) || (begin.top == end.top && begin.left > end.left)) {
                HighlightCursor beginCursor = readerSelectionInfo.getCursors().get(0);
                beginCursor.setCursorType(HighlightCursor.Type.END_CURSOR);
                HighlightCursor endCursor = readerSelectionInfo.getCursors().get(1);
                endCursor.setCursorType(HighlightCursor.Type.BEGIN_CURSOR);
                readerSelectionInfo.getCursors().clear();
                readerSelectionInfo.getCursors().add(endCursor);
                readerSelectionInfo.getCursors().add(beginCursor);
                return true;
            }
        }
        return false;
    }

    public int tracking(String pagePosition, final float sx, final float sy, final float ex, final float ey) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (int i = 0; i < readerSelectionInfo.getCursors().size(); ++i) {
                if (readerSelectionInfo.getCursors().get(i).tracking(sx, sy, ex, ey)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void draw(String pagePosition, Canvas canvas, Paint paint) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                if(cursor.getShowState()) {
                    cursor.draw(canvas, paint);
                }
            }
        }
    }

    public void clear() {
        readerSelectionInfos.clear();
    }

    public synchronized boolean update(String pagePosition, final Context context, ReaderSelection readerSelection,PointF lastPoint) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo == null || readerSelectionInfo.getCurrentSelection() == null) {
            readerSelectionInfo = addPageSelection(pagePosition, readerSelection);
        } else {
            readerSelectionInfo.setCurrentSelection(readerSelection);
        }
        readerSelectionInfo.setTouchPoint(lastPoint);
        List<RectF> rects = readerSelectionInfo.getCurrentSelection().getRectangles();
        if (rects == null || rects.size() <= 0) {
            return false;
        }
        if (readerSelectionInfo.getCursors().size() <= 0) {
            readerSelectionInfo.getCursors().add(new HighlightCursor(context, R.mipmap.ic_choose_left, R.mipmap.ic_choose_right, HighlightCursor.Type.BEGIN_CURSOR));
            readerSelectionInfo.getCursors().add(new HighlightCursor(context, R.mipmap.ic_choose_left, R.mipmap.ic_choose_right, HighlightCursor.Type.END_CURSOR));
        }
        HighlightCursor cursor = readerSelectionInfo.getCursors().get(0);
        float fontHeight = rects.get(0).bottom - rects.get(0).top;
        cursor.setFontHeight(fontHeight);
        PointF beginBottom = RectUtils.getBeginTop(rects);
        cursor.setOriginPosition(beginBottom.x, beginBottom.y);
        cursor.setCursorType(HighlightCursor.Type.BEGIN_CURSOR);

        cursor = readerSelectionInfo.getCursors().get(1);
        PointF endBottom = RectUtils.getEndRight(rects);
        cursor.setFontHeight(fontHeight);
        cursor.setOriginPosition(endBottom.x, endBottom.y);
        cursor.setCursorType(HighlightCursor.Type.END_CURSOR);
        return true;
    }

    private ReaderSelectionInfo addPageSelection(String pagePosition, ReaderSelection readerSelection) {
        ReaderSelectionInfo readerSelectionInfo = new ReaderSelectionInfo();
        readerSelectionInfo.setCurrentSelection(readerSelection);
        readerSelectionInfos.put(pagePosition, readerSelectionInfo);

        return readerSelectionInfo;
    }

    public void deletePageSelection(String pagePosition){
        readerSelectionInfos.remove(pagePosition);
    }

    public void updateDisplayPosition(String pagePosition) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                cursor.updateDisplayPosition();
            }
        }
    }

    public void setEnable(String pagePosition, boolean enable) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                cursor.setEnable(enable);
            }
        }
    }

    public boolean isEnable(String pagePosition) {
        ReaderSelectionInfo readerSelectionInfo = readerSelectionInfos.get(pagePosition);
        if (readerSelectionInfo != null) {
            for (HighlightCursor cursor : readerSelectionInfo.getCursors()) {
                if (cursor != null) {
                    return cursor.isEnable();
                }
            }
        }
        return false;
    }
}
