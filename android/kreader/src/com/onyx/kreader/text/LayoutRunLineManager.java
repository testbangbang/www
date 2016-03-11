package com.onyx.kreader.text;

import android.graphics.Rect;
import android.graphics.RectF;
import com.onyx.kreader.formats.model.TextModelPosition;
import com.onyx.kreader.utils.UnicodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 3/6/16.
 */
public class LayoutRunLineManager {

    static public int LAYOUT_ADDED = 0;
    static public int LAYOUT_FINISHED = 1;
    static public int LAYOUT_BREAK = 2;
    static public int LAYOUT_FAIL = 3;

    private float contentHeight = 0;
    private float contentWidth = 0;
    private float totalSpacing = 0;
    private float averageCharacterWidth = 0;
    private float x = 0;
    private float y = 0;
    private int direction = 1;
    private RectF lineRect;
    private List<LayoutRun> runList = new ArrayList<LayoutRun>();

    public LayoutRunLineManager(final RectF line) {
        lineRect = new RectF(line);
        reset();
    }

    public void reset() {
        runList.clear();
        contentWidth = 0;
        contentHeight = 0;
        totalSpacing = 0;
        x = lineRect.left;
        y = lineRect.top;
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

    public float getCharacterSpacing() {
        return averageCharacterWidth * 3;
    }

    public int layoutRun(final LayoutRun run) {
        if (run.isParagraphEnd()) {
            return LAYOUT_FINISHED;
        }

        if (getAvailableWidth() <= 0 || getAvailableHeight() < run.originHeight()) {
            return LAYOUT_FAIL;
        }
        float leftWidth = getAvailableWidth() - run.originWidth();

        if (leftWidth >= 0) {
            addRun(run);
            return LAYOUT_ADDED;
        }

        if (getAvailableWidth() > getCharacterSpacing()) {
            return LAYOUT_BREAK;
        }

        adjustifyLine();
        return LAYOUT_FINISHED;
    }

    public boolean nextLine(final RectF parent, final RectF next, final float  lineSpacing) {
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
        run.moveTo(x, y);
        runList.add(run);
        x += run.originWidth();
        contentWidth += run.originWidth();
        contentHeight = Math.max(contentHeight, run.originHeight());
        averageCharacterWidth = run.singleCharacterWidth();
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

        x = lineRect.left;
        y = lineRect.top;
        for(LayoutRun run : runList) {
            run.moveTo(x, y);
            x += run.originWidth();
            if (run.isSpacing()) {
                x += spacing;
            }
        }
    }


    private void adjustifyAllRuns() {
        float margin = totalSpacing / runList.size();
        x = lineRect.left;
        y = lineRect.top; // use baseline
        for(LayoutRun run : runList) {
            run.moveTo(x, y);
            x += run.originWidth();
            x += margin;
        }

    }

    private void alignAllRunsToLeft() {
        float margin = totalSpacing / runList.size();
        x = lineRect.left;
        y = lineRect.top; // use baseline
        for(LayoutRun run : runList) {
            run.moveTo(x, y);
            x += run.originWidth();
        }
    }

    public static List<LayoutRun> split(final String text, final TextModelPosition position, final Style style) {
        List<LayoutRun> list = new ArrayList<LayoutRun>();
        int last = 0;
        for(int i = 0; i < text.length(); ++i) {
            Character character = text.charAt(i);
            if (UnicodeUtils.isWhitespace(character)) {
                addLayoutRun(list, text, style, last, i, LayoutRun.TYPE_WORD);
                addLayoutRun(list, text, style, i, i + 1, LayoutRun.TYPE_SPACING);
                last = i + 1;
            } else if (UnicodeUtils.isCJKCharacter(character)) {
                addLayoutRun(list, text, style, last, i, LayoutRun.TYPE_WORD);
                addLayoutRun(list, text, style, i, i + 1, LayoutRun.TYPE_WORD);
                last = i + 1;
            } else if (UnicodeUtils.isPunctuation(character)) {
                addLayoutRun(list, text, style, last, i, LayoutRun.TYPE_WORD);
                addLayoutRun(list, text, style, i, i + 1, LayoutRun.TYPE_PUNCTUATION);
                last = i + 1;
            }
        }
        addLayoutRun(list, text, style, last, text.length(), LayoutRun.TYPE_WORD);
        addParagraphEndRun(list);
        return list;
    }

    public static boolean addParagraphEndRun(final List<LayoutRun> list) {
        LayoutRun run = LayoutRun.createParagraphEnd();
        list.add(run);
        return true;
    }

    public static boolean addLayoutRun(final List<LayoutRun> list, final String text, final Style style, final int start, final int end, final byte type) {
        if (end <= start) {
            return false;
        }
        Rect rect = new Rect();
        style.getPaint().getTextBounds(text, start, end, rect);
        LayoutRun run = LayoutRun.create(text.substring(start, end), start, end, rect.width(), rect.height(), type);
        list.add(run);
        return true;
    }



}
