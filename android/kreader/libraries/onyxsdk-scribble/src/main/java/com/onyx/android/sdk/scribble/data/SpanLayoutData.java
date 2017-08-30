package com.onyx.android.sdk.scribble.data;

import android.text.Layout;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.view.LinedEditText;

/**
 * Created by lxm on 2017/8/30.
 */

public class SpanLayoutData {

    private LineLayoutArgs lineLayoutArgs = new LineLayoutArgs();
    private Shape cursorShape;

    public LineLayoutArgs getLineLayoutArgs() {
        return lineLayoutArgs;
    }

    public void updateLineLayoutCursor(final LinedEditText spanTextView) {
        int pos = spanTextView.getSelectionStart();
        Layout layout = spanTextView.getLayout();
        int line = layout.getLineForOffset(pos);
        int x = (int) layout.getPrimaryHorizontal(pos);
        int top = lineLayoutArgs.getLineTop(line);
        int bottom = lineLayoutArgs.getLineBottom(line);
        updateCursorShape(x, top + 1, x, bottom);
    }

    public void updateLineLayoutArgs(final LinedEditText spanTextView) {
        lineLayoutArgs.updateLineLayoutArgs(spanTextView);
    }

    public Shape getCursorShape() {
        if (cursorShape == null) {
            cursorShape = createNewShape(true, ShapeFactory.SHAPE_LINE);
        }
        return cursorShape;
    }


    private void updateCursorShape(final int left, final int top, final int right, final int bottom) {
        TouchPointList touchPointList = new TouchPointList();
        TouchPoint downPoint = new TouchPoint();
        downPoint.offset(left, top);
        TouchPoint currentPoint = new TouchPoint();
        currentPoint.offset(right, bottom);
        touchPointList.add(downPoint);
        touchPointList.add(currentPoint);
        getCursorShape().addPoints(touchPointList);
    }

    private Shape createNewShape(boolean isSpanTextMode, int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(NoteModel.getDefaultStrokeWidth());
        shape.setColor(NoteDrawingArgs.defaultColor());
        shape.setLayoutType(isSpanTextMode ? ShapeFactory.POSITION_LINE_LAYOUT : ShapeFactory.POSITION_FREE);
        return shape;
    }

}
