package com.onyx.kreader.scribble.request;


import android.graphics.*;
import android.util.Log;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.scribble.ShapeManager;
import com.onyx.kreader.scribble.data.ShapePage;
import com.onyx.kreader.utils.TestUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 4/18/16.
 * load and render shape with scale and offset.
 */
public class LoadShapesRequest extends BaseScribbleRequest {

    private boolean debugPathBenchmark = true;

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
        Debug.d("Render shape starts");
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
        drawRandomTestPath(canvas, paint);
        Debug.d("Render shape finished.");
    }

    private void drawRandomTestPath(final Canvas canvas, final Paint paint) {
        if (!(BuildConfig.DEBUG && debugPathBenchmark)) {
            return;
        }
        Path path = new Path();
        int width = getViewportSize().width();
        int height = getViewportSize().height();
        int max = TestUtils.randInt(0, 5000);
        path.moveTo(TestUtils.randInt(0, width), TestUtils.randInt(0, height));
        for(int i = 0; i < max; ++i) {
            float xx = TestUtils.randInt(0, width);
            float yy = TestUtils.randInt(0, height);
            float xx2 = TestUtils.randInt(0, width);
            float yy2 = TestUtils.randInt(0, height);
            path.quadTo((xx + xx2) / 2, (yy + yy2) / 2, xx2, yy2);
            if (isAbort()) {
                Debug.d("Render shape aborted detected: " + this);
                return;
            }
        }
        long ts = System.currentTimeMillis();
        Debug.d("Render shape path generated: " + this);
        canvas.drawPath(path, paint);
        Debug.d("Render shape path draw with: " + max + " finished: " + (System.currentTimeMillis() - ts) + " ms ");
    }

}
