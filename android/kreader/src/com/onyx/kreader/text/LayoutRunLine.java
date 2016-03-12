package com.onyx.kreader.text;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.onyx.kreader.formats.model.TextPosition;
import com.onyx.kreader.utils.UnicodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/6/16.
 * add LayoutRun into the line to see if it's possible to layout it or not.
 */
public class LayoutRunLine {

    static public enum LayoutResult {
        LAYOUT_ADDED,
        LAYOUT_FINISHED,
        LAYOUT_BREAK,
        LAYOUT_FAIL,
    }

    private float contentHeight = 0;
    private float contentWidth = 0;
    private float totalSpacing = 0;
    private float averageCharacterWidth = 0;
    private PointF currentPoint = new PointF();
    private int direction = 1;
    private RectF lineRect;
    private List<LayoutRun> runList = new ArrayList<LayoutRun>();

    public LayoutRunLine(final RectF line) {
        lineRect = new RectF(line);
        reset();
    }

    public void reset() {
        runList.clear();
        contentWidth = 0;
        contentHeight = 0;
        totalSpacing = 0;
        currentPoint.set(lineRect.left, lineRect.top);
    }

    public float getAvailableWidth() {
        return lineRect.width() - contentWidth;
    }

    public final float getAvailableHeight() {
        return lineRect.height();
    }

    public final float getContentHeight() {
        return contentHeight;
    }

    public final float getContentWidth() {
        return contentWidth;
    }

    public float getCharacterSpacing() {
        return averageCharacterWidth * 3;
    }

    public final float getIndent() {
        return 30;
    }

    public LayoutResult layoutRun(final LayoutRun run) {
        if (run.isParagraphEnd()) {
            beautifyLine(true);
            return LayoutResult.LAYOUT_FINISHED;
        }

        if (getAvailableWidth() <= 0 || getAvailableHeight() < run.originHeight()) {
            return LayoutResult.LAYOUT_FAIL;
        }
        float leftWidth = getAvailableWidth() - run.originWidth();

        if (run.isParagraphBegin() && leftWidth > getIndent()) {
            currentPoint.offset(getIndent(), 0);
            contentWidth += getIndent();
            return LayoutResult.LAYOUT_ADDED;
        }

        if (leftWidth >= 0) {
            addRun(run);
            return LayoutResult.LAYOUT_ADDED;
        }

        // if not possible to layout even one single character
        if (getAvailableWidth() < run.singleCharacterWidth()) {
            beautifyLine(false);
            return LayoutResult.LAYOUT_FAIL;
        }

        return LayoutResult.LAYOUT_BREAK;
    }

    public boolean nextLine(final RectF parent, final RectF next, final float lineSpacing) {
        if (lineRect.top + contentHeight +  lineSpacing >= parent.bottom) {
            return false;
        }
        next.set(lineRect.left, lineRect.top + contentHeight + lineSpacing, lineRect.right, parent.bottom);
        return true;
    }

    public final List<LayoutRun> getRunList() {
        return runList;
    }

    private void addRun(final LayoutRun run) {
        run.moveTo(currentPoint.x, currentPoint.y);
        runList.add(run);
        currentPoint.offset(run.originWidth(), 0);
        contentWidth += run.originWidth();
        contentHeight = Math.max(contentHeight, run.originHeight());
        averageCharacterWidth = run.singleCharacterWidth();
    }

    public void beautifyLine(boolean lastLine) {
        if (lastLine) {
            alignAllRunsToLeft();
        } else {
            adjustifyLine();
        }
    }

    public void adjustifyLine() {
        totalSpacing = getAvailableWidth();
        int count = 0;
        for(LayoutRun run : runList) {
            if (run.isSpacing()) {
                count++;
            }
        }

        if (count <= 0) {
            adjustifyAllRuns();
            return;
        }

        float spacing = totalSpacing / count;
        if (spacing >= getCharacterSpacing()) {
            adjustifyAllRuns();
            return;
        }

        currentPoint.set(lineRect.left, lineRect.top);
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
            if (run.isSpacing()) {
                currentPoint.offset(spacing, 0);
            }
        }
    }


    private void adjustifyAllRuns() {
        float margin = totalSpacing / runList.size();
        currentPoint.set(lineRect.left, lineRect.top);
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
            currentPoint.offset(margin, 0);
        }
    }

    private void alignAllRunsToLeft() {
        currentPoint.set(lineRect.left, lineRect.top);
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
        }
    }




}
