package com.onyx.kreader.text;

import android.graphics.RectF;
import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/12/16.
 */
public class LayoutController {

    /**
     * Algorithm: try element at first, add the element to layout if possible.
     * if there is no enough room, try to compare spacing and required width
     * if required width less than spacing, reduce the spacing among words of this line
     * otherwise try to break element if possible and adjust spacing
     * if hyphenation is not possible, move to next line and adjust word spacing of current line.
     * check punctuation if the first element is punctuation, move the last element of last line to the new line.
     * average the last line.
     * @param layoutRect the layout rect.
     * @param textStyle the text style.
     * @param bookModel book model
     * @return layout lines
     */
    public void layout(final RectF layoutRect, final Style textStyle, final BookModel bookModel) {

        RectF lineRect = new RectF(layoutRect);
        List<LayoutRunLine> lineList = new ArrayList<LayoutRunLine>();
        LayoutRunLine layoutLine = new LayoutRunLine(lineRect);
        lineList.add(layoutLine);
        final List<LayoutRun> runlist = new ArrayList<LayoutRun>();

        int count = bookModel.getTextModel().paragraphCount();
        final List<Paragraph> paragraphList = bookModel.getTextModel().getParagraphList();
        for (int p = 0; p < paragraphList.size(); ++p) {
            final Paragraph paragraph = paragraphList.get(p);
            for (ParagraphEntry entry : paragraph.getEntryList()) {
                if (entry instanceof TextParagraphEntry) {
                    TextParagraphEntry textParagraphEntry = (TextParagraphEntry) entry;
                    LayoutRunGenerator.split(runlist, textParagraphEntry.getText(), textStyle);
                }
            }
        }

        float lineSpacing;
        boolean stop = false;
        int index = 0;
        while (!stop && index < runlist.size()) {
            final LayoutRun layoutRun = runlist.get(index);
            LayoutRunLine.LayoutResult result = layoutLine.addLayoutRun(layoutRun);
            switch (result) {
                case LAYOUT_ADDED:
                    ++index;
                    break;
                case LAYOUT_FINISHED:
                    lineSpacing = Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A"));
                    if (!layoutRun.isParagraphEnd()) {
                        lineSpacing = 10;
                    }
                    if (!layoutLine.nextLine(layoutRect, lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    ++index;
                    break;
                case LAYOUT_FAIL:
                    lineSpacing = Math.max(layoutLine.getContentHeight(), textStyle.measureHeight("A"));
                    if (!layoutRun.isParagraphEnd()) {
                        lineSpacing = 10;
                    }

                    if (!layoutLine.nextLine(layoutRect, lineRect, lineSpacing)) {
                        stop = true;
                        break;
                    }
                    layoutLine = new LayoutRunLine(lineRect);
                    lineList.add(layoutLine);
                    break;
                case LAYOUT_BREAK:
                    final LayoutRun another = breakRunByWidth(layoutRun, layoutLine.getAvailableWidth(), textStyle);
                    runlist.add(index + 1, another);
                    break;
            }
        }

    }


    private final LayoutRun breakRunByWidth(final LayoutRun layoutRun, final float width, final Style textStyle) {
        final float characterWidth = layoutRun.singleCharacterWidth();
        int count = (int)(width /characterWidth);
        float newWidth = textStyle.getPaint().measureText(layoutRun.getText(), layoutRun.getStart(), layoutRun.getStart() + count);
        return layoutRun.breakRun(count, newWidth);
    }
}
