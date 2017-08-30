package com.onyx.android.sdk.scribble.data;

import android.graphics.Rect;

import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.utils.SpanUtils;
import com.onyx.android.sdk.scribble.view.LinedEditText;

/**
 * Created by ming on 2016/12/22.
 */

public class LineLayoutArgs {

    private int lineCount;
    private int baseLine;
    private int lineHeight;

    private int spanTextFontHeight;

    public LineLayoutArgs() {
    }

    public LineLayoutArgs(int baseLine, int lineCount, int lineHeight) {
        this.baseLine = baseLine;
        this.lineCount = lineCount;
        this.lineHeight = lineHeight;
    }

    public int getBaseLine() {
        return baseLine;
    }

    public void setBaseLine(int baseLine) {
        this.baseLine = baseLine;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public static LineLayoutArgs create(int baseLine, int lineCount, int lineHeight) {
        return new LineLayoutArgs(baseLine, lineCount, lineHeight);
    }

    public int getLineBottom(int line) {
        int bottom = baseLine;
        for (int i = 0; i < line; i++) {
            bottom += lineHeight;
        }
        return bottom;
    }

    public int getLineTop(int line) {
        int top = line == 0 ? 0 : getLineBottom(line - 1);
        return top;
    }

    public int getSpanTextFontHeight() {
        return spanTextFontHeight;
    }

    public void updateLineLayoutArgs(LinedEditText spanTextView) {
        int height = spanTextView.getHeight();
        this.lineHeight = spanTextView.getLineHeight();
        int lineCount = spanTextView.getLineCount();
        int count = height / lineHeight;
        if (lineCount <= count) {
            lineCount = count;
        }
        Rect r = new Rect();
        spanTextView.getLineBounds(0, r);
        this.baseLine = r.bottom;
        this.lineCount = lineCount;
        spanTextFontHeight = SpanUtils.calculateSpanTextFontHeight(spanTextView, ShapeSpan.SHAPE_SPAN_MARGIN);
    }
}
