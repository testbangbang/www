package com.onyx.kreader.note.request;

import android.graphics.*;
import com.hanvon.core.Algorithm;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/2/16.
 */
public class ReaderBaseNoteRequest extends BaseRequest {

    private volatile ReaderNoteDataInfo shapeDataInfo;
    private String docUniqueId;
    private String parentLibraryId;
    private Rect viewportSize;
    private List<PageInfo> visiblePages = new ArrayList<PageInfo>();
    private boolean debugPathBenchmark = false;
    private boolean pauseInputProcessor = true;
    private boolean resumeInputProcessor = false;
    private volatile boolean render = true;

    public ReaderBaseNoteRequest() {
        setAbortPendingTasks(true);
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public void setDocUniqueId(final String id) {
        docUniqueId = id;
    }

    public final String getDocUniqueId() {
        return docUniqueId;
    }

    public String getParentLibraryId() {
        return parentLibraryId;
    }

    public void setParentLibraryId(String parentLibraryId) {
        this.parentLibraryId = parentLibraryId;
    }

    public void setViewportSize(final Rect size) {
        viewportSize = size;
    }

    public final Rect getViewportSize() {
        return viewportSize;
    }

    public void setVisiblePages(final List<PageInfo> pages) {
        visiblePages.clear();
        visiblePages.addAll(pages);
    }

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public void beforeExecute(final NoteManager noteManager) {
        noteManager.getRequestManager().acquireWakeLock(getContext());
        benchmarkStart();
        invokeStartCallback(noteManager.getRequestManager());
    }

    private void invokeStartCallback(final RequestManager requestManager) {
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(ReaderBaseNoteRequest.this);
            }
        };
        if (isRunInBackground()) {
            requestManager.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public void execute(final NoteManager noteManager) throws Exception {
    }

    public void afterExecute(final NoteManager parent) {
        if (getException() != null) {
            getException().printStackTrace();
        }
        benchmarkEnd();
        final Runnable runnable = postExecuteRunnable(parent);
        if (isRunInBackground()) {
            parent.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    private Runnable postExecuteRunnable(final NoteManager parent) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (isRender()) {
                        synchronized (parent) {
                            parent.copyBitmap();
                        }
                    }
                    parent.enableScreenPost(true);
                    if (getCallback() != null) {
                        getCallback().done(ReaderBaseNoteRequest.this, getException());
                    }
                } catch (Exception e) {

                } finally {
                    parent.getRequestManager().releaseWakeLock();
                }
            }
        };
        return runnable;
    }

    public final ReaderNoteDataInfo getShapeDataInfo() {
        if (shapeDataInfo == null) {
            shapeDataInfo = new ReaderNoteDataInfo();
        }
        return shapeDataInfo;
    }

    public boolean renderVisiblePages(final NoteManager parent) {
        synchronized (parent) {
            boolean contentRendered = false;
            Bitmap bitmap = parent.updateRenderBitmap(getViewportSize());
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = preparePaint(parent);

            drawBackground(canvas, paint, parent.getNoteDocument().getBackground());
            final Matrix renderMatrix = new Matrix();
            final RenderContext renderContext = parent.getRenderContext();
            renderContext.prepareRenderingBuffer(bitmap);

            for (PageInfo page : getVisiblePages()) {
                updateMatrix(renderMatrix, page);
                renderContext.update(bitmap, canvas, paint, renderMatrix);
                final ReaderNotePage notePage = parent.getNoteDocument().loadPage(getContext(), page.getName(), 0);
                if (notePage != null) {
                    notePage.render(renderContext, null);
                    contentRendered = true;
                }
            }
            renderContext.flushRenderingBuffer(bitmap);
            contentRendered |= drawRandomTestPath(canvas, paint);
            return contentRendered;
        }
    }

    private void updateMatrix(final Matrix matrix, final PageInfo pageInfo) {
        matrix.reset();
        matrix.postScale(pageInfo.getActualScale(), pageInfo.getActualScale());
        matrix.postTranslate(pageInfo.getDisplayRect().left, pageInfo.getDisplayRect().top);
    }

    private Paint preparePaint(final NoteManager parent) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(parent.getNoteDocument().getStrokeWidth());
        return paint;
    }

    private void drawBackground(final Canvas canvas, final Paint paint,int bgType) {
        int bgResID = 0;
        switch (bgType) {
            case NoteBackgroundType.EMPTY:
                return;
            case NoteBackgroundType.LINE:
                bgResID = com.onyx.android.sdk.scribble.R.drawable.scribble_back_ground_line;
                break;
            case NoteBackgroundType.GRID:
                bgResID = com.onyx.android.sdk.scribble.R.drawable.scribble_back_ground_grid;
                break;
        }
        drawBackgroundResource(canvas, paint, bgResID);
    }

    private void drawBackgroundResource(Canvas canvas, Paint paint, int resID) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), resID);
        Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        Rect dest = new Rect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
        canvas.drawBitmap(bitmap, src, dest, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private boolean isRenderRandomTestPath() {
        return debugPathBenchmark && BuildConfig.DEBUG;
    }

    private boolean drawRandomTestPath(final Canvas canvas, final Paint paint) {
        if (!isRenderRandomTestPath()) {
            return false;
        }
        Path path = new Path();
        int width = getViewportSize().width();
        int height = getViewportSize().height();
        int max = TestUtils.randInt(0, 1000);
        path.moveTo(TestUtils.randInt(0, width), TestUtils.randInt(0, height));
        for(int i = 0; i < max; ++i) {
            float xx = TestUtils.randInt(0, width);
            float yy = TestUtils.randInt(0, height);
            float xx2 = TestUtils.randInt(0, width);
            float yy2 = TestUtils.randInt(0, height);
            path.quadTo((xx + xx2) / 2, (yy + yy2) / 2, xx2, yy2);
            if (isAbort()) {
                return false;
            }
        }
        long ts = System.currentTimeMillis();
        canvas.drawPath(path, paint);
        return true;
    }

    public void currentPageAsVisiblePage(final NoteManager noteManager) {
        final ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(), "", 0);
        getVisiblePages().clear();
        PageInfo pageInfo = new PageInfo(notePage.getPageUniqueId(), getViewportSize().width(), getViewportSize().height());
        pageInfo.updateDisplayRect(new RectF(0, 0, getViewportSize().width(), getViewportSize().height()));
        getVisiblePages().add(pageInfo);
    }

    public void renderCurrentPage(final NoteManager helper) {
        if (!isRender()) {
            return;
        }
        currentPageAsVisiblePage(helper);
        renderVisiblePages(helper);
    }

    public void updateShapeDataInfo(final NoteManager parent) {
        final ReaderNoteDataInfo shapeDataInfo = getShapeDataInfo();
        parent.updateShapeDataInfo(getContext(), shapeDataInfo);
    }

    public void ensureDocumentOpened(final NoteManager parent) {
        if (!parent.getNoteDocument().isOpen()) {
            parent.getNoteDocument().open(getContext(),
                    getDocUniqueId(),
                    getParentLibraryId());
        }
    }

    public void syncDrawingArgs(final NoteDrawingArgs args) {
        getShapeDataInfo().getDrawingArgs().syncFrom(args);
    }

    public final NoteDrawingArgs getDrawingArgs() {
        return getShapeDataInfo().getDrawingArgs();
    }


}
