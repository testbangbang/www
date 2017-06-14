package com.onyx.edu.reader.note.request;

import android.graphics.*;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.edu.reader.BuildConfig;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNoteDataInfo;
import com.onyx.edu.reader.note.data.ReaderNotePage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/2/16.
 */
public class ReaderBaseNoteRequest extends BaseRequest {

    private volatile ReaderNoteDataInfo noteDataInfo;
    private String docUniqueId;
    private String parentLibraryId;
    private Rect viewportSize;
    private List<PageInfo> visiblePages = new ArrayList<PageInfo>();
    private boolean debugPathBenchmark = false;
    private volatile boolean pauseRawInputProcessor = true;
    private volatile boolean resumeRawInputProcessor = false;
    private volatile boolean render = true;
    private volatile boolean transfer = true;
    private volatile boolean resetNoteDataInfo = true;
    private volatile int associatedUniqueId;

    public ReaderBaseNoteRequest() {
        super();
        setAbortPendingTasks(false);
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
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

    public boolean isPauseRawInputProcessor() {
        return pauseRawInputProcessor;
    }

    public void setPauseRawInputProcessor(boolean pauseRawInputProcessor) {
        this.pauseRawInputProcessor = pauseRawInputProcessor;
    }

    public boolean isResumeRawInputProcessor() {
        return resumeRawInputProcessor;
    }

    public void setResumeRawInputProcessor(boolean resumeRawInputProcessor) {
        this.resumeRawInputProcessor = resumeRawInputProcessor;
    }

    public void setVisiblePages(final List<PageInfo> pages) {
        visiblePages.clear();
        visiblePages.addAll(pages);
    }

    public void setVisiblePage(final PageInfo pageInfo) {
        visiblePages.clear();
        visiblePages.add(pageInfo);
    }

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public void beforeExecute(final NoteManager noteManager) {
        noteManager.getRequestManager().acquireWakeLock(getContext(), getClass().getSimpleName());
        if (isPauseRawInputProcessor()) {
            Debug.d(getClass(), "raw status: pause");
            noteManager.pauseRawEventProcessor();
        }
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
                    parent.enableScreenPost(true);
                    synchronized (parent) {
                        updateShapeDataInfo(parent);
                        if (isRender() && isTransfer()) {
                            parent.copyBitmap();
                        }
                    }
                    BaseCallback.invoke(getCallback(), ReaderBaseNoteRequest.this, getException());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (isResumeRawInputProcessor() && parent.isDFBForCurrentShape()) {
                        Debug.d(getClass(), "raw status: resume");
                        parent.resumeRawEventProcessor(getContext());
                    }
                    parent.getRequestManager().releaseWakeLock();
                }
            }
        };
        return runnable;
    }

    public final ReaderNoteDataInfo getNoteDataInfo() {
        if (noteDataInfo == null) {
            noteDataInfo = new ReaderNoteDataInfo();
        }
        return noteDataInfo;
    }

    public boolean renderVisiblePages(final NoteManager parent) {
        synchronized (parent) {
            boolean rendered = false;
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
                    rendered = true;
                }
            }
            renderContext.flushRenderingBuffer(bitmap);
            return rendered;
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

    private void updateShapeDataInfo(final NoteManager parent) {
        parent.saveNoteDataInfo(ReaderBaseNoteRequest.this);
        final ReaderNoteDataInfo shapeDataInfo = getNoteDataInfo();
        parent.updateShapeDataInfo(getContext(), shapeDataInfo);
    }

    public void ensureDocumentOpened(final NoteManager parent) {
        final String src = getDocUniqueId();
        final String dst = parent.getNoteDocument().getDocumentUniqueId();
        if (src != null && dst != null && !src.equalsIgnoreCase(dst)) {
            parent.getNoteDocument().close(getContext());
        }
        if (!parent.getNoteDocument().isOpen()) {
            parent.getNoteDocument().open(getContext(),
                    getDocUniqueId(),
                    getParentLibraryId());
            initWithDeviceConfig(parent);
        }
    }

    private void initWithDeviceConfig(final NoteManager parent) {
        NoteModel.setDefaultEraserRadius(DeviceConfig.sharedInstance(getContext()).getEraserRadius());
        parent.getNoteDocument().setEraserRadius(NoteModel.getDefaultEraserRadius());
    }

    public void syncDrawingArgs(final NoteDrawingArgs args) {
        getNoteDataInfo().getDrawingArgs().copyFrom(args);
    }

    public final NoteDrawingArgs getDrawingArgs() {
        return getNoteDataInfo().getDrawingArgs();
    }

    public boolean isResetNoteDataInfo() {
        return resetNoteDataInfo;
    }

    public void setResetNoteDataInfo(boolean resetNoteDataInfo) {
        this.resetNoteDataInfo = resetNoteDataInfo;
    }

    public int getAssociatedUniqueId() {
        return associatedUniqueId;
    }

    public void setAssociatedUniqueId(int associatedUniqueId) {
        this.associatedUniqueId = associatedUniqueId;
    }
}
