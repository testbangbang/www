package com.onyx.kreader.note.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
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

    private volatile ReaderNoteDataInfo noteDataInfo;
    private String docUniqueId;
    private String parentLibraryId;
    private Rect viewportSize;
    private List<PageInfo> visiblePages = new ArrayList<PageInfo>();
    private boolean hasSideNote = false;
    private boolean debugPathBenchmark = false;
    private volatile boolean pauseRawInputProcessor = true;
    private volatile boolean resumeRawInputProcessor = false;
    private volatile boolean render = true;
    private volatile boolean renderToScreen = true;
    private volatile boolean transfer = true;
    private volatile boolean resetNoteDataInfo = true;
    private volatile boolean applyGCIntervalUpdate = false;
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
        loadNoteDataInfo(parent);

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
                    if (isRenderToScreen()) {
                        parent.enableScreenPost(true);
                    }
                    synchronized (parent) {
                        updateShapeDataInfo(parent);
                        if (isRender() && isTransfer() && !isAbort()) {
                            parent.copyBitmap();
                        }
                    }
                    BaseCallback.invoke(getCallback(), ReaderBaseNoteRequest.this, getException());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    updateRawInputProcessor(parent);
                    parent.getRequestManager().releaseWakeLock();
                }
            }
        };
        return runnable;
    }

    private void updateRawInputProcessor(NoteManager noteManager) {
        if (isPauseRawInputProcessor()) {
            noteManager.pauseRawEventProcessor();
        }
        if (isResumeRawInputProcessor() && noteManager.isDFBForCurrentShape()) {
            noteManager.resumeRawEventProcessor(getContext());
        }
    }

    public final ReaderNoteDataInfo getNoteDataInfo() {
        if (noteDataInfo == null) {
            noteDataInfo = new ReaderNoteDataInfo();
        }
        return noteDataInfo;
    }

    public boolean renderVisiblePages(final NoteManager parent) {
        boolean rendered = false;
        ReaderBitmapImpl bitmap = new ReaderBitmapImpl(viewportSize.width(), viewportSize.height(), Bitmap.Config.ARGB_8888);
        bitmap.getBitmap().eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bitmap.getBitmap());
        Paint paint = preparePaint(parent);

        drawBackground(canvas, paint, parent.getNoteDocument().getBackground());
        final Matrix renderMatrix = new Matrix();
        final RenderContext renderContext = parent.getRenderContext();
        renderContext.prepareRenderingBuffer(bitmap.getBitmap());

        for (PageInfo page : getVisiblePages()) {
            if (isAbort()) {
                break;
            }
            updateMatrix(renderMatrix, page);
            renderContext.update(bitmap.getBitmap(), canvas, paint, renderMatrix);
            final ReaderNotePage notePage = parent.getNoteDocument().loadPage(getContext(), page.getRange(), page.getSubPage());
            if (notePage != null) {
                notePage.render(renderContext, new ReaderNotePage.RenderCallback() {
                    @Override
                    public boolean isRenderAbort() {
                        return isAbort();
                    }
                });
                rendered = true;
            }
        }
        renderContext.flushRenderingBuffer(bitmap.getBitmap());

        synchronized (parent) {
            parent.setRenderBitmap(bitmap);
        }

        return rendered;
    }

    private void loadNotePages(final NoteManager parent) {
        synchronized (parent) {
            for (PageInfo page : getVisiblePages()) {
                int pageCount = parent.getNoteDocument().getSubPageCount(page.getRange());
                for (int i = 0; i < pageCount; i++) {
                    final ReaderNotePage notePage = parent.getNoteDocument().loadPage(getContext(), page.getRange(), i);
                    if (notePage != null) {
                        if (i > page.getSubPage() && notePage.hasShapes()) {
                            hasSideNote = true;
                            return;
                        }
                    }
                }
            }
        }
    }

    private void loadNoteDataInfo(final NoteManager parent) {
        loadNotePages(parent);
        getNoteDataInfo().setHasSideNote(hasSideNote);
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

    private boolean isRenderToScreen() {
        return renderToScreen;
    }

    public void setRenderToScreen(boolean toScreen) {
        renderToScreen = toScreen;
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
            restoreNoteDrawingArgs(parent);
        }
    }

    private void initWithDeviceConfig(final NoteManager parent) {
        NoteModel.setDefaultEraserRadius(DeviceConfig.sharedInstance(getContext()).getEraserRadius());
        parent.getNoteDocument().setEraserRadius(NoteModel.getDefaultEraserRadius());
    }

    private void restoreNoteDrawingArgs(NoteManager noteManager) {
        noteManager.restoreStrokeWidth();
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

    public boolean isApplyGCIntervalUpdate() {
        return applyGCIntervalUpdate;
    }

    public void setApplyGCIntervalUpdate(boolean applyGCIntervalUpdate) {
        this.applyGCIntervalUpdate = applyGCIntervalUpdate;
    }

    public int getAssociatedUniqueId() {
        return associatedUniqueId;
    }

    public void setAssociatedUniqueId(int associatedUniqueId) {
        this.associatedUniqueId = associatedUniqueId;
    }
}
