package com.onyx.kreader.text;

import android.graphics.RectF;
import com.onyx.kreader.utils.RectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/13/16.
 * Represents a block of layout. For text page it contains getValuesList of Layout Block.
 * The layout block contains a getValuesList of LayoutRunLine.
 * TextPage
 *    |--LayoutBlock
 *           |--  List<LayoutRunLine>
 *
 * The layout block can be used in page master.
 * http://stackoverflow.com/questions/27631736/meaning-of-top-ascent-baseline-descent-bottom-and-leading-in-androids-font
 */
public class LayoutBlock {

    private List<LayoutRunLine> lineList = new ArrayList<LayoutRunLine>();
    private LayoutRunLine.Args args = new LayoutRunLine.Args();
    private boolean stop = false;
    private LayoutRunLine lastLine;
    private LayoutRunLine currentLayoutLine;
    private RectF blockRect;


    static public abstract class Callback {

        public abstract boolean hasNextRun();

        public abstract LayoutRun getRun();

        public abstract void moveToPrevRun();

        public abstract void moveToNextRun();

        public abstract boolean breakRun(final float width);

        public abstract Style styleForRun(final LayoutRun run);

    }

    private final LayoutRunLine.Args getArgs() {
        return args;
    }

    private void resetArgs(final RectF blockRect) {
        final LayoutRunLine.Args myArgs = getArgs();
        myArgs.indent = 50;
        myArgs.lineRect = new RectF(blockRect);
    }

    public void layoutWithCallback(final RectF rect, final Callback callback) {
        lineList.clear();
        blockRect = rect;
        resetArgs(blockRect);

        lastLine = null;
        currentLayoutLine = createLine(lineList, args);
        stop = false;
        while (!stop && callback.hasNextRun()) {
            final LayoutRun layoutRun = callback.getRun();
            if (!beforeLayout(lastLine, currentLayoutLine, layoutRun, callback)) {
                continue;
            }
            LayoutRunLine.LayoutResult result = currentLayoutLine.addLayoutRun(layoutRun);
            switch (result) {
                case LAYOUT_ADDED:
                case LAYOUT_IGNORED:
                    callback.moveToNextRun();
                    break;
                case LAYOUT_FINISHED:
                    callback.moveToNextRun();
                    if (!nextLine(layoutRun, callback)) {
                        stop = true;
                    }
                    break;
                case  LAYOUT_HEIGHT_FAIL:
                    rollbackLine(currentLayoutLine, callback);
                    if (!nextLine(layoutRun, callback)) {
                        stop = true;
                    }
                    break;
                case LAYOUT_WIDTH_FAIL:
                    if (!nextLine(layoutRun, callback)) {
                        stop = true;
                    }
                    break;
                case LAYOUT_BREAK:
                    currentLayoutLine.beautifyLine(currentLayoutLine.isParagraphEnd());
                    if (!nextLine(layoutRun, callback)) {
                        stop = true;
                        break;
                    }
                    break;
            }
        }
        afterLayout();
    }

    private boolean nextLine(final LayoutRun layoutRun, final Callback callback) {
        float lineSpacing = lineSpacing(currentLayoutLine, layoutRun, callback.styleForRun(layoutRun));
        args.lineRect = RectUtils.remove(blockRect, currentLayoutLine.getLineRect().top, currentLayoutLine.getContentHeight(), lineSpacing);
        if (args.lineRect == null) {
            return false;
        }
        lastLine = currentLayoutLine;
        currentLayoutLine = createLine(lineList, args);
        return true;
    }

    private float lineSpacing(final LayoutRunLine currentLayoutLine, final LayoutRun lastRun, final Style textStyle) {
        float lineSpacing = Math.max(currentLayoutLine.getContentHeight(), textStyle.measureHeight("A"));
        if (!lastRun.isParagraphEnd()) {
            lineSpacing = 10;
        }
        return lineSpacing;
    }

    private boolean beforeLayout(final LayoutRunLine lastLine, final LayoutRunLine newLine, final LayoutRun layoutRun, final Callback callback) {
        if (!newLine.isEmpty() || !layoutRun.isPunctuation() || lastLine == null) {
            return true;
        }
        lastLine.removeLastRun();
        callback.moveToPrevRun();

        int count = 0;
        LayoutRun run;
        while ((run = lastLine.getLastRun()) != null && run.isPunctuation() && count++ < 3) {
            lastLine.removeLastRun();
            callback.moveToPrevRun();
        }
        return false;
    }

    private void rollbackLine(final LayoutRunLine line, final Callback callback) {
        while (line != null && !line.isEmpty()) {
            line.removeLastRun();
            callback.moveToPrevRun();
        }
    }

    public final LayoutRunLine createLine(final List<LayoutRunLine> lineList, final LayoutRunLine.Args args) {
        LayoutRunLine layoutLine = new LayoutRunLine(args);
        lineList.add(layoutLine);
        return layoutLine;
    }

    public final LayoutRun breakRunByWidth(final LayoutRun layoutRun, final float width, final Style textStyle) {
        final float characterWidth = layoutRun.singleCharacterWidth();
        int count = (int)(width /characterWidth);
        float newWidth = textStyle.getPaint().measureText(layoutRun.getText(), layoutRun.getStart(), layoutRun.getStart() + count);
        return layoutRun.breakRun(count, newWidth);
    }

    public final List<LayoutRunLine> getLineList() {
        return lineList;
    }

    private void afterLayout() {

    }

}
