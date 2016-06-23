package com.onyx.android.sdk.scribble.request.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 6/4/16.
 */
public class ShapeRenderRequest extends BaseNoteRequest {

    private List<Shape> shapes;
    private Bitmap bitmap;
    private Paint paint;

    public ShapeRenderRequest(final List<Shape> list, final Bitmap b, final Paint p) {
        shapes = list;
        bitmap = b;
        paint = p;
    }

    public void execute(final ShapeViewHelper parent) throws Exception {
        bitmap.eraseColor(Color.argb(0, 0xff, 0xff, 0xff));
        Canvas canvas = new Canvas(bitmap);

        for(Shape shape : shapes) {
            shape.render(null, canvas, paint);
        }
    }
}
