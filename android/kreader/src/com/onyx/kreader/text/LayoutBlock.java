package com.onyx.kreader.text;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/13/16.
 * Represents a block of layout. For text page it contains list of Layout Block.
 * The layout block contains a list of LayoutRunLine.
 * TextPage
 *    |--LayoutBlock
 *           |--  List<LayoutRunLine>
 *
 * The layout block can be used in page master.
 */
public class LayoutBlock {

    private List<LayoutRunLine> lineList = new ArrayList<LayoutRunLine>();
    private LayoutRunLine.Args args = new LayoutRunLine.Args();
    private boolean stop = false;

    static public abstract class Callback {

        public abstract boolean hasNextRun();

        public abstract LayoutRun getRun();

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

    public void layoutWithCallback(final RectF blockRect, final Callback callback) {
        lineList.clear();
        resetArgs(blockRect);

        LayoutRunLine lastLine = null;
        LayoutRunLine layoutLine = createLine(lineList, args);
        float lineSpacing;
        stop = false;
        while (!stop && callback.hasNextRun()) {
            final LayoutRun layoutRun = callback.getRun();
            beforeLayout(lastLine, layoutLine, layoutRun);
            LayoutRunLine.LayoutResult result = layoutLine.addLayoutRun(layoutRun);
            switch (result) {
                case LAYOUT_ADDED:
                    callback.moveToNextRun();
                    break;
                case LAYOUT_FINISHED:
                    lineSpacing = lineSpacing(layoutLine, layoutRun, callback.styleForRun(layoutRun));
                    if (!layoutLine.nextLine(blockRect, args.lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    lastLine = layoutLine;
                    layoutLine = new LayoutRunLine(args);
                    lineList.add(layoutLine);
                    callback.moveToNextRun();
                    break;
                case LAYOUT_FAIL:
                    lineSpacing = lineSpacing(layoutLine, layoutRun, callback.styleForRun(layoutRun));
                    if (!layoutLine.nextLine(blockRect, args.lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    lastLine = layoutLine;
                    layoutLine = new LayoutRunLine(args);
                    lineList.add(layoutLine);
                    break;
                case LAYOUT_BREAK:
                    final LayoutRun another = breakRunByWidth(layoutRun, layoutLine.getAvailableWidth(), callback.styleForRun(layoutRun));
                    if (!callback.breakRun(layoutLine.getAvailableWidth())) {

                    }
                    break;
            }
        }
        afterLayout();
    }

    private float lineSpacing(final LayoutRunLine currentLayoutLine, final LayoutRun lastRun, final Style textStyle) {
        float lineSpacing = Math.max(currentLayoutLine.getContentHeight(), textStyle.measureHeight("A"));
        if (!lastRun.isParagraphEnd()) {
            lineSpacing = 15;
        }
        return lineSpacing;
    }

    private void beforeLayout(final LayoutRunLine oldLine, final LayoutRunLine newLine, final LayoutRun layoutRun) {
        if (layoutRun.isPunctuation() && newLine.isEmpty() && oldLine != null) {
            newLine.addLayoutRun(oldLine.removeLastRun());
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
