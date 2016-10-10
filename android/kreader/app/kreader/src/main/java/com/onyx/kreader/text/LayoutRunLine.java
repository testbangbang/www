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
        /**
         * layout run has been added successfully
         */
        LAYOUT_ADDED,
        LAYOUT_IGNORED,

        /**
         * Received end-of-line
         */
        LAYOUT_FINISHED,

        /**
         * caller should break word
         */
        LAYOUT_BREAK,

        /**
         * not enough width room to add layout to this line.
         */
        LAYOUT_WIDTH_FAIL,

        /**
         * not enough height room to add layout to this line.
         */
        LAYOUT_HEIGHT_FAIL,

    }

    static public class Args {
        public float indent;
        public RectF lineRect;
        public int direction = 0;

        public Args() {
        }

        public Args(final Args another) {
            indent = another.indent;
            lineRect = new RectF(another.lineRect);
            direction = another.direction;
        }

        public final float width() {
            return lineRect.width();
        }

        public final float height() {
            return lineRect.height();
        }
    }

    static public final byte PARAGRAPH_BEGIN = 0x01;
    static public final byte PARAGRAPH_END = 0x02;

    private float contentHeight = 0;
    private float contentWidth = 0;
    private float averageCharacterWidth = 0;
    private byte lineType = 0;
    private PointF currentPoint = new PointF();
    private Args lineArgs;
    private List<LayoutRun> runList = new ArrayList<LayoutRun>();

    public LayoutRunLine(final Args args) {
        lineArgs = new Args(args);
        reset();
    }

    public void reset() {
        runList.clear();
        contentWidth = 0;
        contentHeight = 0;
        resetCurrentPoint();
    }

    public boolean isEmpty() {
        return runList.isEmpty();
    }

    public float getAvailableWidth() {
        return lineArgs.width() - contentWidth;
    }

    public final float getAvailableHeight() {
        return lineArgs.height();
    }

    public final RectF getLineRect() {
        return lineArgs.lineRect;
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
        return lineArgs.indent;
    }

    public LayoutResult addLayoutRun(final LayoutRun run) {
        if (run.isParagraphEnd()) {
            addParagraphEndRun(run);
            return LayoutResult.LAYOUT_FINISHED;
        }

        if (getAvailableHeight() < run.originHeight()) {
            return LayoutResult.LAYOUT_HEIGHT_FAIL;
        }

        final float availableWidth = getAvailableWidth();
        if (availableWidth <= 0) {
            return LayoutResult.LAYOUT_WIDTH_FAIL;
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
            beautifyLine(isParagraphEnd());
            return LayoutResult.LAYOUT_WIDTH_FAIL;
        }

        return LayoutResult.LAYOUT_BREAK;
    }

    public final LayoutRun removeLastRun() {
        if (runList.isEmpty()) {
            return null;
        }
        final LayoutRun layoutRun = runList.remove(runList.size() - 1);
        contentWidth -= layoutRun.originWidth();
        beautifyLine(isParagraphEnd());
        return layoutRun;
    }

    public final LayoutRun getLastRun() {
        if (runList.isEmpty()) {
            return null;
        }
        final LayoutRun layoutRun = runList.get(runList.size() - 1);
        return layoutRun;
    }

    public final List<LayoutRun> getRunList() {
        return runList;
    }

    private void addParagraphEndRun(final LayoutRun run) {
        lineType |= PARAGRAPH_END;
        beautifyLine(isParagraphEnd());
    }

    private void addParagraphBeginRun(final LayoutRun run) {
        lineType |= PARAGRAPH_BEGIN;
        currentPoint.offset(getIndent(), 0);
        contentWidth += getIndent();
    }

    public boolean isParagraphBegin() {
        return (lineType & PARAGRAPH_BEGIN) != 0;
    }

    public boolean isParagraphEnd() {
        return (lineType & PARAGRAPH_END) != 0;
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
            alignToLeft();
        } else {
            adjustifyLine();
        }
    }

    /**
     * distribute the spacing to runs. selectText spacing run at first, if not possible
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
        if (runList.size() <= 1) {
            return;
        }
        resetCurrentPoint();
        float margin = getAvailableWidth() / (runList.size() - 1);
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
            currentPoint.offset(margin, 0);
        }
    }

    private void alignToLeft() {
        resetCurrentPoint();
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
        }
    }

    private void alignToCenter() {
        float margin = getAvailableWidth() / 2;
        currentPoint.set(lineArgs.lineRect.left + margin, lineArgs.lineRect.top);
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
        }
    }

    private void alignToRight() {
        float margin = getAvailableWidth();
        currentPoint.set(lineArgs.lineRect.left + margin, lineArgs.lineRect.top);
        for(LayoutRun run : runList) {
            run.moveTo(currentPoint.x, currentPoint.y);
            currentPoint.offset(run.originWidth(), 0);
        }
    }

    private void resetCurrentPoint() {
        currentPoint.set(lineArgs.lineRect.left, lineArgs.lineRect.top);
        if (isParagraphBegin()) {
            currentPoint.offset(getIndent(), 0);
        }
    }




}
