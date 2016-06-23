package com.onyx.android.sdk.scribble.request.shape;


import android.graphics.*;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.BuildConfig;
import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by zengzhu on 4/18/16.
 * load and render shape with scale and offset.
 */
public class ShapePageLoadRequest extends BaseNoteRequest {

    private boolean debugPathBenchmark = false;

    public ShapePageLoadRequest(final List<PageInfo> pages, final Rect size) {
        setAbortPendingTasks();
        setViewportSize(size);
        setVisiblePages(pages);
    }

    public void execute(final ShapeViewHelper parent) throws Exception {
        loadShapeData(parent);
        renderShape(parent);
    }

    public void loadShapeData(final ShapeViewHelper parent) {
        try {
            parent.getNoteDocument().loadShapePages(getContext(), getVisiblePages());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderShape(final ShapeViewHelper parent) {
        Bitmap bitmap = parent.updateBitmap(getViewportSize());
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3.0f);

        for(PageInfo page: getVisiblePages()) {
            final NotePage notePage = parent.getNoteDocument().getNotePage(getContext(), page.getName());
            notePage.render(canvas, paint, new NotePage.RenderCallback() {
                @Override
                public boolean isRenderAbort() {
                    return isAbort();
                }
            });
        }

        // draw test path.
        drawRandomTestPath(canvas, paint);
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
                return;
            }
        }
        long ts = System.currentTimeMillis();
        canvas.drawPath(path, paint);
    }

}
