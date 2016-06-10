package com.onyx.kreader.scribble.request;


import android.graphics.*;
import android.util.Log;
import android.util.Size;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.scribble.ShapeManager;
import com.onyx.kreader.scribble.data.ShapeModel;
import com.onyx.kreader.scribble.data.ShapeDataProvider;
import com.onyx.kreader.scribble.data.ShapePage;
import com.onyx.kreader.scribble.shape.ShapeFactory;
import com.onyx.kreader.utils.TestUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

/**
 * Created by zengzhu on 4/18/16.
 * load and render shape with scale and offset.
 */
public class LoadShapesRequest extends BaseScribbleRequest {


    public LoadShapesRequest(final String id, final List<PageInfo> pages, final Rect size) {
        setAbortPendingTasks();
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
            e.printStackTrace();
        }
    }

    public void renderShape(final ShapeManager parent) {
        Log.d("######", "render shape:  " + this);
        Bitmap bitmap = parent.updateBitmap(getViewportSize());
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3.0f);

        for(Map.Entry<String, ShapePage> entry: getShapeDataInfo().getShapePageMap().entrySet()) {
            entry.getValue().render(canvas, paint, new ShapePage.RenderCallback() {
                @Override
                public boolean isRenderAbort() {
                    return isAbort();
                }
            });
        }

        // draw test path.
        drawRandomPath(canvas, paint);
        Log.d("######", "render shape finished:  " + this);
    }

    private void drawRandomPath(final Canvas canvas, final Paint paint) {
        Path path = new Path();
        int width = getViewportSize().width();
        int height = getViewportSize().height();
        int max = TestUtils.randInt(1000, 5000);
        path.moveTo(TestUtils.randInt(0, width), TestUtils.randInt(0, height));
        for(int i = 0; i < max; ++i) {
            float xx = TestUtils.randInt(0, width);
            float yy = TestUtils.randInt(0, height);
            float xx2 = TestUtils.randInt(0, width);
            float yy2 = TestUtils.randInt(0, height);
            path.quadTo((xx + xx2) / 2, (yy + yy2) / 2, xx2, yy2);
            if (isAbort()) {
                Log.d("###########", "aborted detected: " + this);
                return;
            }
        }
        Log.d("###########", "path generated: " + this);
        canvas.drawPath(path, paint);
    }

}
