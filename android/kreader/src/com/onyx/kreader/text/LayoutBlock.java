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

    public int layout(final RectF blockRect, final LayoutRunGenerator generator, final Style textStyle) {
        lineList.clear();

        RectF lineRect = new RectF(blockRect);

        LayoutRunLine lastLine = null;
        LayoutRunLine layoutLine = createLine(lineList, blockRect);

        float lineSpacing;
        boolean stop = false;
        while (!stop && generator.hasNext()) {
            final LayoutRun layoutRun = generator.getRun(textStyle);
            beforeLayout(lastLine, layoutLine, layoutRun);
            LayoutRunLine.LayoutResult result = layoutLine.addLayoutRun(layoutRun);
            switch (result) {
                case LAYOUT_ADDED:
                    generator.moveToNextRun();
                    break;
                case LAYOUT_FINISHED:
                    lineSpacing = Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A"));
                    if (!layoutRun.isParagraphEnd()) {
                        lineSpacing = 10;
                    }
                    if (!layoutLine.nextLine(blockRect, lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    lastLine = layoutLine;
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    generator.moveToNextRun();
                    break;
                case LAYOUT_FAIL:
                    lineSpacing = Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A"));
                    if (!layoutRun.isParagraphEnd()) {
                        lineSpacing = 10;
                    }

                    if (!layoutLine.nextLine(blockRect, lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    lastLine = layoutLine;
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    break;
                case LAYOUT_BREAK:
                    final LayoutRun another = breakRunByWidth(layoutRun, layoutLine.getAvailableWidth(), textStyle);
                    generator.breakRun(another);
                    break;
            }
        }
        return 0;
    }

    private void beforeLayout(final LayoutRunLine oldLine, final LayoutRunLine newLine, final LayoutRun layoutRun) {
        if (layoutRun.isPunctuation() && newLine.isEmpty() && oldLine != null) {
            newLine.addLayoutRun(oldLine.removeLastRun());
        }
    }

    public final LayoutRunLine createLine(final List<LayoutRunLine> lineList, final RectF lineRect) {
        LayoutRunLine layoutLine = new LayoutRunLine(lineRect);
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
}
