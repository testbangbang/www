package com.onyx.edu.note.scribble.event;

import android.text.SpannableStringBuilder;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;

import java.util.List;

/**
 * Created by solskjaer49 on 2017/7/24 19:35.
 */

public class SpanFinishedEvent {
    private SpannableStringBuilder builder;
    private List<Shape> spanShapeList;
    private ShapeSpan lastShapeSpan;

    public SpannableStringBuilder getBuilder() {
        return builder;
    }

    public List<Shape> getSpanShapeList() {
        return spanShapeList;
    }

    public ShapeSpan getLastShapeSpan() {
        return lastShapeSpan;
    }

    public SpanFinishedEvent(SpannableStringBuilder builder, List<Shape> spanShapeList, ShapeSpan lastShapeSpan) {
        this.builder = builder;
        this.spanShapeList = spanShapeList;
        this.lastShapeSpan = lastShapeSpan;
    }
}
