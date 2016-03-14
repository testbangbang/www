package com.onyx.kreader.text;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/6/16.
 * add LayoutRun into the line to see if it's possible to layout it or not.
 * hyphenation and consider the first run of next line.
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
    private float indent = 0;
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
        indent = 0;
        resetCurrentPoint();
    }

    public boolean isEmpty() {
        return runList.isEmpty();
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
        return averageCharacterWidth;
    }

    public final float getIndent() {
        return 50;
    }

    public LayoutResult addLayoutRun(final LayoutRun run) {
        if (run.isParagraphEnd()) {
            addParagraphEndRun(run);
            return LayoutResult.LAYOUT_FINISHED;
        }

        final float availableWidth = getAvailableWidth();
        if (availableWidth <= 0 || getAvailableHeight() < run.originHeight()) {
            return LayoutResult.LAYOUT_FAIL;
        }

        if (run.isParagraphBegin() && availableWidth > getIndent()) {
            addParagraphBeginRun(run);
            return LayoutResult.LAYOUT_ADDED;
        }

        if (availableWidth >= run.originWidth()) {
            addRun(run);
            return LayoutResult.LAYOUT_ADDED;
        }

        // if not possible to layout even one single character
        if (availableWidth < getCharacterSpacing()) {
            beautifyLine(false);
            return LayoutResult.LAYOUT_FAIL;
        }

        return LayoutResult.LAYOUT_BREAK;
    }

    public final LayoutRun removeLastRun() {
        if (runList.isEmpty()) {
            return null;
        }
        final LayoutRun layoutRun = runList.remove(runList.size() - 1);
        contentWidth -= layoutRun.originWidth();
        beautifyLine(false);
        return layoutRun;
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

    private void addParagraphEndRun(final LayoutRun run) {
        beautifyLine(true);
    }

    private void addParagraphBeginRun(final LayoutRun run) {
        currentPoint.offset(getIndent(), 0);
        contentWidth += getIndent();
        indent = getIndent();
    }

    private void addRun(final LayoutRun run) {
        run.moveTo(currentPoint.x, currentPoint.y);
        runList.add(run);
        currentPoint.offset(run.originWidth(), 0);
        contentWidth += run.originWidth();
        contentHeight = Math.max(contentHeight, run.originHeight());
        final float width = run.singleCharacterWidth();
        if (averageCharacterWidth < width) {
            averageCharacterWidth = width;
        }
    }

    public void beautifyLine(boolean lastLine) {
        if (lastLine) {
            alignAllRunsToLeft();
        } else {
            adjustifyLine();
        }
    }

    /**
     * distribute the spacing to runs. select spacing run at first, if not possible
     * or if it's too large, distribute to all runs instead.
     */
    public void adjustifyLine() {
        int count = 0;
        for (LayoutRun run : runList) {
            if (run.isSpacing() || run.isPunctuation()) {
                count++;
            }
        }

        if (count <= 0) {
            adjustifyAllRuns();
            return;
        }

        // if spacing is too large distribute to all runs.
        float spacing = getAvailableWidth() / count;
        if (spacing >= getCharacterSpacing() / 2) {
            adjustifyAllRuns();
            return;
        }

        adjustifyToSpacing(spacing);
    }

    private void adjustifyToSpacing(final float spacing) {
        resetCurrentPoint();
        float pending = 0;
        for(LayoutRun run : runList) {
            currentPoint.offset(pending, 0);
            pending = 0;
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
            if (run.isSpacing() || run.isPunctuation()) {
                pending = spacing;
            }
        }
    }

    private void adjustifyAllRuns() {
        resetCurrentPoint();
        float margin = getAvailableWidth() / runList.size();
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
            currentPoint.offset(margin, 0);
        }
    }

    private void alignAllRunsToLeft() {
        resetCurrentPoint();
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
        }
    }

    private void resetCurrentPoint() {
        currentPoint.set(lineRect.left, lineRect.top);
        currentPoint.offset(indent, 0);
    }




}
