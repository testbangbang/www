package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

import java.util.Iterator;

/**
 * Created by zhuzeng on 4/21/16.
 */
public class BrushScribbleShape extends EPDShape  {

    // render path with width list and generate path list.
    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        paint.setStrokeWidth(getStrokeWidth());
        ShapeUtils.renderShape(canvas, paint, matrix, getNormalizedPoints());
    }

}
