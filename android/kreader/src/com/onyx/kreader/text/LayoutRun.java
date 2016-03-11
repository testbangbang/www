package com.onyx.kreader.text;

import android.graphics.RectF;

/**
 * Created by zengzhu on 3/6/16.
 * Mininum layout unit.
 */
public class LayoutRun {

    static public byte TYPE_PARAGRAPH_LEADING   = 0x1;
    static public byte TYPE_WORD                = 0x2;
    static public byte TYPE_SPACING             = 0x3;
    static public byte TYPE_PUNCTUATION         = 0x4;
    static public byte TYPE_PARAGRAPH_END       = 0x5;

    private float originWidth;
    private float originHeight;
    private RectF position = new RectF();
    private int runType;
    private int start, end;
    private String text;

    static public LayoutRun create(final String text, final int start, final int end, final float width, final float height, final byte type) {
        LayoutRun run = new LayoutRun();
        run.text = text;
        run.originWidth = width;
        run.originHeight = height;
        run.position.set(0, 0, width - 1, height - 1);
        run.runType = type;
        run.start = start;
        run.end = end;
        return run;
    }

    static public LayoutRun createParagraphEnd() {
        LayoutRun run = new LayoutRun();
        run.runType = TYPE_PARAGRAPH_END;
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
        return (runType == TYPE_SPACING);
    }

    public boolean isNormal() {
        return (runType == TYPE_WORD) ;
    }

    public boolean isParagraphLeading() {
        return (runType == TYPE_PARAGRAPH_LEADING);
    }

    public boolean isParagraphEnd() {
        return (runType == TYPE_PARAGRAPH_END);
    }

    public final RectF getPosition() {
        return position;
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

    public void moveTo(final float x, final float y) {
        position.offsetTo(x, y);
    }


}
