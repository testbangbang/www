package com.onyx.kreader.text;

import android.graphics.Rect;
import com.onyx.kreader.utils.UnicodeUtils;

import java.util.List;

/**
 * Created by zengzhu on 3/12/16.
 * Split model into run list.
 */
public class LayoutRunSplitter {

    /**
     * fetch more data within limit
     * @param list
     * @param position
     * @param textStyle
     * @param limit
     */
    public static void fetchMore(final List<LayoutRun> list, final TextPosition position, final Style textStyle, final int limit) {
        if (position.available() < limit) {
            position.fetchMore();
        }
        split(list, position, textStyle);
    }

    public static void split(final List<LayoutRun> list, final TextPosition textPosition, final Style style) {
        while (textPosition.hasNext()) {
            final String text = textPosition.next();
            if (text != null) {
                LayoutRunSplitter.split(list, text, style);
            }
        }
    }

    public static void split(final List<LayoutRun> list, final String text, final Style style) {
        addParagraphBeginRun(list);
        int last = 0;
        for(int i = 0; i < text.length(); ++i) {
            Character character = text.charAt(i);
            if (UnicodeUtils.isWhitespace(character)) {
                addLayoutTextRun(list, text, style, last, i, LayoutRun.Type.TYPE_WORD);
                addLayoutTextRun(list, text, style, i, i + 1, LayoutRun.Type.TYPE_SPACING);
                last = i + 1;
            } else if (UnicodeUtils.isCJKCharacter(character)) {
                addLayoutTextRun(list, text, style, last, i, LayoutRun.Type.TYPE_WORD);
                addLayoutTextRun(list, text, style, i, i + 1, LayoutRun.Type.TYPE_WORD);
                last = i + 1;
            } else if (UnicodeUtils.isPunctuation(character)) {
                addLayoutTextRun(list, text, style, last, i, LayoutRun.Type.TYPE_WORD);
                addLayoutTextRun(list, text, style, i, i + 1, LayoutRun.Type.TYPE_PUNCTUATION);
                last = i + 1;
            }
        }
        addLayoutTextRun(list, text, style, last, text.length(), LayoutRun.Type.TYPE_WORD);
        addParagraphEndRun(list);
    }

    public static boolean addParagraphBeginRun(final List<LayoutRun> list) {
        LayoutRun run = LayoutRun.createParagraphBegin();
        list.add(run);
        return true;
    }

    public static boolean addParagraphEndRun(final List<LayoutRun> list) {
        LayoutRun run = LayoutRun.createParagraphEnd();
        list.add(run);
        return true;
    }

    public static boolean addLayoutTextRun(final List<LayoutRun> list, final String text, final Style style, final int start, final int end, final LayoutRun.Type type) {
        if (end <= start) {
            return false;
        }
        Rect rect = new Rect();
        style.getPaint().getTextBounds(text, start, end, rect);
        float width = style.getPaint().measureText(text, start, end);
        LayoutRun run = LayoutRun.create(text, start, end, width, rect.height(), type);
        list.add(run);
        return true;
    }

}
