package com.onyx.kreader.text;

import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

/**
 * Created by zengzhu on 3/6/16.
 */
public class LayoutRun {

    static public int TYPE_LEADING  = 0x1;
    static public int TYPE_NORMAL   = 0x2;
    static public int TYPE_SPACING  = 0x3;
    static public int TYPE_PUNCTUATION = 0x4;
    static public int TYPE_END      = 0x5;

    private float originWidth;
    private float originHeight;
    private RectF position = new RectF();
    private int runType;
    private int start, end;

    static public LayoutRun create(final int start, final int end, final float width, final float height, final int type) {
        LayoutRun run = new LayoutRun();
        run.originWidth = width;
        run.originHeight = height;
        run.runType = type;
        run.start = start;
        run.end = end;
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
        return (runType == TYPE_NORMAL) ;
    }

    public boolean isLeading() {
        return (runType == TYPE_LEADING);
    }

    public boolean isEnd() {
        return (runType == TYPE_END);
    }

    public final RectF getPosition() {
        return position;
    }

    public void moveTo(final float x, final float y) {
        position.offsetTo(x, y);
    }


}
