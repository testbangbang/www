package com.onyx.android.sdk.scribble.request;

import android.graphics.*;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.BuildConfig;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/3/16.
 * Requests are used in standalone application or separate page rendering from
 * shape rendering.
 */
public class BaseNoteRequest extends BaseRequest {

    private ShapeDataInfo shapeDataInfo;
    private String docUniqueId;
    private Rect viewportSize;
    private List<PageInfo> visiblePages = new ArrayList<PageInfo>();
    private boolean debugPathBenchmark = true;
    private boolean pauseInputProcessor = true;
    private boolean resumeInputProcessor = true;

    public boolean isResumeInputProcessor() {
        return resumeInputProcessor;
    }

    public void setResumeInputProcessor(boolean resumeInputProcessor) {
        this.resumeInputProcessor = resumeInputProcessor;
    }

    public boolean isPauseInputProcessor() {
        return pauseInputProcessor;
    }

    public void setPauseInputProcessor(boolean pauseInputProcessor) {
        this.pauseInputProcessor = pauseInputProcessor;
    }

    public BaseNoteRequest() {
        setAbortPendingTasks();
    }

    public void setDocUniqueId(final String id) {
        docUniqueId = id;
    }

    public final String getDocUniqueId() {
        return docUniqueId;
    }

    public void setViewportSize(final Rect size) {
        viewportSize = size;
    }

    public final Rect getViewportSize() {
        return viewportSize;
    }

    public void setVisiblePages(final List<PageInfo> pages) {
        visiblePages.addAll(pages);
    }

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public void beforeExecute(final NoteViewHelper helper) {
        if (isPauseInputProcessor()) {
            helper.stopDrawing();
        }
        helper.getRequestManager().acquireWakeLock(getContext());
        benchmarkStart();
        invokeStartCallback(helper.getRequestManager());
    }

    private void invokeStartCallback(final RequestManager requestManager) {
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(BaseNoteRequest.this);
            }
        };
        if (isRunInBackground()) {
            requestManager.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public void execute(final NoteViewHelper helper) throws Exception {
    }

    public void afterExecute(final NoteViewHelper helper) {
        if (getException() != null) {
            getException().printStackTrace();
        }
        benchmarkEnd();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isResumeInputProcessor()) {
                    helper.startDrawing();
                }
                if (getCallback() != null) {
                    getCallback().done(BaseNoteRequest.this, getException());
                }
                helper.getRequestManager().releaseWakeLock();
            }};

        if (isRunInBackground()) {
            helper.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public final ShapeDataInfo getShapeDataInfo() {
        if (shapeDataInfo == null) {
            shapeDataInfo = new ShapeDataInfo();
        }
        return shapeDataInfo;
    }

    public void renderVisiblePages(final NoteViewHelper parent) {
        Bitmap bitmap = parent.updateBitmap(getViewportSize());
        bitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3.0f);

        for(PageInfo page: getVisiblePages()) {
            final NotePage notePage = parent.getNoteDocument().getNotePage(getContext(), page.getName());
            notePage.render(canvas, paint, page.normalizeMatrix(), new NotePage.RenderCallback() {
                @Override
                public boolean isRenderAbort() {
                    return isAbort();
                }
            });
        }

        // draw test path.
        drawRandomTestPath(canvas, paint);
    }

    private boolean isRenderRandomTestPath() {
        return debugPathBenchmark;
    }

    private void drawRandomTestPath(final Canvas canvas, final Paint paint) {
        if (!isRenderRandomTestPath()) {
            return;
        }
        Path path = new Path();
        int width = getViewportSize().width();
        int height = getViewportSize().height();
        int max = TestUtils.randInt(0, 50);
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

    public void currentPageAsVisiblePage(final NoteViewHelper helper) {
        final NotePage notePage = helper.getNoteDocument().getCurrentPage(getContext());
        getVisiblePages().clear();
        PageInfo pageInfo = new PageInfo(notePage.getPageUniqueId(), getViewportSize().width(), getViewportSize().height());
        pageInfo.updateDisplayRect(new RectF(0, 0, getViewportSize().width(), getViewportSize().height()));
        getVisiblePages().add(pageInfo);
    }

    public void renderCurrentPage(final NoteViewHelper helper) {
        currentPageAsVisiblePage(helper);
        renderVisiblePages(helper);
    }

    public void updateShapeDataInfo(final NoteViewHelper parent) {
        getShapeDataInfo().updateShapePageMap(
                parent.getNoteDocument().getPageNameList(),
                parent.getNoteDocument().getCurrentPageIndex());
    }

    public void ensureDocumentOpened(final NoteViewHelper parent) {
        if (!parent.getNoteDocument().isOpen()) {
            parent.getNoteDocument().open(getContext(), getDocUniqueId());
        }
    }



}
