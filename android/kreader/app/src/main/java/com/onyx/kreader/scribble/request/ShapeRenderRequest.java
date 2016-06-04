package com.onyx.kreader.scribble.request;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.onyx.kreader.scribble.ScribbleManager;
import com.onyx.kreader.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 6/4/16.
 */
public class ShapeRenderRequest extends BaseScribbleRequest {

    private List<Shape> shapes;
    private Canvas canvas;
    private Paint paint;

    public ShapeRenderRequest(final List<Shape> list, final Canvas c, final Paint p) {
        shapes = list;
        canvas = c;
        paint = p;
    }

    public void execute(final ScribbleManager parent) throws Exception {
        for(Shape shape : shapes) {
            shape.render(canvas, paint);
        }
    }
}
