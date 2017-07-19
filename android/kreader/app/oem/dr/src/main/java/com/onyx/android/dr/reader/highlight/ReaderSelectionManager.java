package com.onyx.android.dr.reader.highlight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelXorXfermode;
import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.dr.R;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.utils.RectUtils;

import java.util.ArrayList;
import java.util.List;

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
    private ReaderSelection currentSelection;

    private List<HighlightCursor> cursors = new ArrayList<HighlightCursor>();

    public ReaderSelectionManager() {
        super();
    }

    public void setCurrentSelection(ReaderSelection selection) {
        currentSelection = selection;
    }

    public ReaderSelection getCurrentSelection() {
        return currentSelection;
    }

    public HighlightCursor getHighlightCursor(int index) {
        if (index >= 0 && index < cursors.size())  {
            return cursors.get(index);
        }
        return null;
    }


    public boolean normalize() {
        RectF begin = cursors.get(0).getHotPoint();
        RectF end = cursors.get(1).getHotPoint();
        if ((begin.top > end.top) || (begin.top == end.top && begin.left > end.left)) {
            HighlightCursor beginCursor = cursors.get(0);
            beginCursor.setCursorType(HighlightCursor.Type.END_CURSOR);
            HighlightCursor endCursor = cursors.get(1);
            endCursor.setCursorType(HighlightCursor.Type.BEGIN_CURSOR);
            cursors.clear();
            cursors.add(endCursor);
            cursors.add(beginCursor);
            return true;
        }
        return false;
    }

    public int tracking(final float sx, final float sy, final float ex, final float ey) {
        for(int i = 0; i < cursors.size(); ++i) {
            if (cursors.get(i).tracking(sx, sy, ex, ey)) {
                return i;
            }
        }
        return -1;
    }

    public void draw(Canvas canvas, Paint paint, PixelXorXfermode xor) {
        for(HighlightCursor cursor : cursors) {
            cursor.draw(canvas, paint, xor);
        }
    }

    public void clear() {
        for(HighlightCursor cursor : cursors) {
            cursor.clear();
        }
    }

    public boolean update(final Context context) {
        if (currentSelection == null) {
            return false;
        }
        List<RectF> rects = currentSelection.getRectangles();
        if (rects == null || rects.size() <= 0) {
            return false;
        }
        if (cursors.size() <= 0) {
            cursors.add(new HighlightCursor(context, R.drawable.ic_choose_left, R.drawable.ic_choose_right, HighlightCursor.Type.BEGIN_CURSOR));
            cursors.add(new HighlightCursor(context, R.drawable.ic_choose_left, R.drawable.ic_choose_right, HighlightCursor.Type.END_CURSOR));
        }
        HighlightCursor cursor = cursors.get(0);
        float fontHeight = rects.get(0).bottom - rects.get(0).top;
        cursor.setFontHeight(fontHeight);
        PointF beginBottom = RectUtils.getBeginBottom(rects);
        cursor.setOriginPosition(beginBottom.x, beginBottom.y);
        cursor.setCursorType(HighlightCursor.Type.BEGIN_CURSOR);

        cursor = cursors.get(1);
        PointF endBottom = RectUtils.getEndBottom(rects);
        cursor.setFontHeight(fontHeight);
        cursor.setOriginPosition(endBottom.x , endBottom.y);
        cursor.setCursorType(HighlightCursor.Type.END_CURSOR);
        return true;
    }

    public void updateDisplayPosition() {
        for(HighlightCursor cursor : cursors) {
            cursor.updateDisplayPosition();
        }
    }

    public void setEnable(boolean enable){
        for(HighlightCursor cursor : cursors) {
            cursor.setEnable(enable);
        }
    }

    public boolean isEnable(){
        for(HighlightCursor cursor : cursors) {
            if (cursor != null){
                return cursor.isEnable();
            }
        }
        return false;
    }
}
