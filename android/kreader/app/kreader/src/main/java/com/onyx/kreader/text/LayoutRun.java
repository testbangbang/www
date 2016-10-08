package com.onyx.kreader.text;

import android.graphics.RectF;

/**
 * Created by zengzhu on 3/6/16.
 * Mininum layout unit, used by LayoutRunLine and layout engine
 */
public class LayoutRun {

    public static enum Type {
        TYPE_PARAGRAPH_BEGIN,
        TYPE_WORD,
        TYPE_IMAGE,
        TYPE_SPACING,
        TYPE_PUNCTUATION,
        TYPE_PARAGRAPH_END,
    }

    private float originWidth;
    private float originHeight;
    private RectF positionRect = new RectF();
    private Type runType;
    private int start, end;
    private String text;

    static public LayoutRun create(final String text, final int start, final int end, final float width, final float height, final Type type) {
        LayoutRun run = new LayoutRun();
        run.text = text;
        run.originWidth = width;
        run.originHeight = height;
        run.positionRect.set(0, 0, width - 1, height - 1);
        run.runType = type;
        run.start = start;
        run.end = end;
        return run;
    }

    static public LayoutRun createParagraphBegin() {
        LayoutRun run = new LayoutRun();
        run.runType = Type.TYPE_PARAGRAPH_BEGIN;
        return run;
    }

    static public LayoutRun createParagraphEnd() {
        LayoutRun run = new LayoutRun();
        run.runType = Type.TYPE_PARAGRAPH_END;
        return run;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int length() {
        return end - start;
    }

    public float originWidth() {
        return originWidth;
    }

    public float singleCharacterWidth() {
        if (end > start) {
            return originWidth() / length();
        }
        return originWidth();
    }

    public float originHeight() {
        return originHeight;
    }

    public boolean isSpacing() {
        return (runType == Type.TYPE_SPACING);
    }

    public boolean isPunctuation() {
        return runType == Type.TYPE_PUNCTUATION;
    }

    public boolean isWord() {
        return (runType == Type.TYPE_WORD) ;
    }

    public boolean isParagraphBegin() {
        return (runType == Type.TYPE_PARAGRAPH_BEGIN);
    }

    public boolean isParagraphEnd() {
        return (runType == Type.TYPE_PARAGRAPH_END);
    }

    public final RectF getPositionRect() {
        return positionRect;
    }

    public final float getOriginWidth() {
        return originWidth;
    }

    public final float getOriginHeight() {
        return originHeight;
    }

    public final String getText() {
        return text;
    }

    /**
     * getById real text without hyphenation symbol.
     * @return
     */
    public final String getRealText() {
        if (text != null) {
            return text.substring(start, end);
        }
        return null;
    }

    public void moveTo(final float x, final float y) {
        positionRect.offsetTo(x, y);
    }

    public final LayoutRun breakRun(int count, final float newOriginWidth) {
        LayoutRun anotherRun = null;
        if (count < (end - start)) {
            anotherRun = LayoutRun.create(text, start + count, end, originWidth - newOriginWidth, originHeight, Type.TYPE_WORD);
            end = start + count;
            originWidth = newOriginWidth;
            positionRect.set(positionRect.left, positionRect.top, positionRect.left + originWidth, positionRect.bottom);
        }
        return anotherRun;
    }

}
