package com.onyx.kreader.scribble.request;


import android.graphics.*;
import android.util.Size;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.scribble.ShapeManager;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.onyx.kreader.scribble.data.ShapeDataProvider;
import com.onyx.kreader.scribble.data.ShapePage;
import com.onyx.kreader.scribble.shape.ShapeFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

/**
 * Created by zengzhu on 4/18/16.
 * load and render shape with scale and offset.
 */
public class LoadShapesRequest extends BaseScribbleRequest {


    public LoadShapesRequest(final String id, final List<PageInfo> pages, final Rect size) {
        setViewportSize(size);
        setDocUniqueId(id);
        setVisiblePages(pages);
    }

    public void execute(final ShapeManager parent) throws Exception {
        loadShapeData(parent);
        renderShape(parent);
    }

    public void loadShapeData(final ShapeManager parent) {
        try {
            getShapeDataInfo().loadUserShape(getContext(), getDocUniqueId(), getVisiblePages());
        } catch (Exception e) {

        }
    }

    public void renderShape(final ShapeManager parent) {
        Bitmap bitmap = parent.updateBitmap(getViewportSize());
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3.0f);

        for(Map.Entry<String, ShapePage> entry: getShapeDataInfo().getShapePageMap().entrySet()) {
            entry.getValue().render(canvas, paint);
        }


        // draw test path.
        Path path = new Path();
        path.moveTo(100, 100);
        path.quadTo(123, 500, 230, 220);
        canvas.drawPath(path, paint);
    }


}
