package com.onyx.kreader.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.asyncrequest.ConfigManager;
import com.onyx.android.sdk.scribble.api.TouchHelper;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.BaseShape;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.InkUtils;
import com.onyx.android.sdk.scribble.utils.MappingConfig;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.note.data.ReaderNoteDocument;
import com.onyx.kreader.note.data.ReaderNotePage;
import com.onyx.kreader.note.request.ReaderBaseNoteRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhuzeng on 9/2/16.
 */
public class NoteManager {

    private static final String TAG = NoteManager.class.getSimpleName();
    private RequestManager requestManager = new RequestManager(Thread.NORM_PRIORITY);
    private TouchHelper touchHelper;
    private ReaderNoteDocument noteDocument = new ReaderNoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private ReaderBitmapImpl viewBitmapWrapper = new ReaderBitmapImpl();
    private volatile SurfaceView surfaceView;
    private volatile Rect visibleDrawRect;

    private ShapeEventHandler shapeEventHandler;

    private RenderContext renderContext = new RenderContext();

    private List<Shape> shapeStash = new ArrayList<>();
    private DeviceConfig noteConfig;
    private MappingConfig mappingConfig;
    private boolean sideNoting = false;
    private List<PageInfo> visiblePages = new ArrayList<>();
    private EventBus eventBus;
    private ReaderNoteDataInfo noteDataInfo = new ReaderNoteDataInfo();
    private RectF visibleDrawRectF;
    private volatile boolean enableRawEventProcessor = false;
    private AtomicBoolean noteDirty = new AtomicBoolean(false);
    private volatile boolean enableShortcutDrawing = false;
    private volatile boolean enableShortcutErasing = false;
    private boolean useWakeLock = true;
    private WakeLockHolder wakeLockHolder = new WakeLockHolder(false);

    public NoteManager(final Context context, final EventBus bus) {
        eventBus = bus;
        shapeEventHandler = new ShapeEventHandler(this);
        shapeEventHandler.registerEventBus();
        ConfigManager.init(context.getApplicationContext());
        BaseShape.setUseRawInput(ConfigManager.getInstance().getDeviceConfig().useRawInput());
        getTouchHelper().setUseRawInput(ConfigManager.getInstance().getDeviceConfig().useRawInput());
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final ReaderNoteDocument getNoteDocument() {
        return noteDocument;
    }

    public void startRawEventProcessor() {
        getTouchHelper().createRawDrawing();
    }

    public void enableRawEventProcessor(boolean enable) {
        enableRawEventProcessor = enable;
        if (!enable) {
            releaseWakeLock();
        }
    }

    public void stopRawEventProcessor() {
        releaseWakeLock();
        getTouchHelper().destroyRawDrawing();
    }

    public void pauseRawEventProcessor() {
        releaseWakeLock();
        getTouchHelper().pauseRawDrawing();
    }

    public void resumeRawEventProcessor(final Context context) {
        if (enableRawEventProcessor) {
            acquireWakeLock(context);
        }
        getTouchHelper().resumeRawDrawing();
    }

    private void acquireWakeLock(final Context context) {
        if (useWakeLock) {
            wakeLockHolder.acquireWakeLock(context, WakeLockHolder.FULL_FLAGS, TAG, 40 * 60 * 1000);
        }
    }

    private void releaseWakeLock() {
        wakeLockHolder.releaseWakeLock();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public SurfaceView getHostView() {
        return surfaceView;
    }

    private void initNoteArgs(final Context context) {
        noteConfig = DeviceConfig.sharedInstance(context, "note");
        mappingConfig = MappingConfig.sharedInstance(context, "note");
        enableShortcutDrawing = (noteConfig.isShortcutDrawingEnabled() && noteConfig.supportBigPen());
        enableShortcutErasing = (noteConfig.isShortcutErasingEnabled() && noteConfig.supportBigPen());
        NoteModel.setDefaultEraserRadius(noteConfig.getEraserRadius());
        NoteModel.setDefaultStrokeColor(noteConfig.getDefaultStrokeColor());
        InkUtils.setPressureEntries(mappingConfig.getPressureList());
    }

    public void updateHostView(final Context context, final SurfaceView sv, final Rect visibleDrawRect, final List<RectF> excludeRect, int orientation) {
        if (noteConfig == null || mappingConfig == null) {
            initNoteArgs(context);
        }
        surfaceView = sv;
        this.visibleDrawRect = new Rect(visibleDrawRect);
        getTouchHelper().setup(surfaceView);

        List<Rect> limitRegions = getLimitRegionOfVisiblePages();
        getTouchHelper().setLimitRect(limitRegions, RectUtils.toRectList(excludeRect));
    }

    public final TouchHelper getTouchHelper() {
        if (touchHelper == null) {
            touchHelper = new TouchHelper(getEventBus());
        }
        return touchHelper;
    }

    public final DeviceConfig getNoteConfig() {
        return noteConfig;
    }

    public void updateShapeDataInfo(final Context context, final ReaderNoteDataInfo shapeDataInfo) {
        shapeDataInfo.updateDrawingArgs(getNoteDocument().getNoteDrawingArgs());
//        shapeDataInfo.setCanRedoShape(getNoteDocument().getCurrentPage(context).canRedo());
//        shapeDataInfo.setCanUndoShape(getNoteDocument().getCurrentPage(context).canUndo());
        shapeDataInfo.setDocumentUniqueId(getNoteDocument().getDocumentUniqueId());
    }

    public void enableScreenPost(boolean enable) {
        if (surfaceView != null) {
            EpdController.enablePost(surfaceView, enable ? 1 : 0);
        }
    }

    public Bitmap updateRenderBitmap(final Rect viewportSize) {
        renderBitmapWrapper.update(viewportSize.width(), viewportSize.height(), Bitmap.Config.ARGB_8888);
        return renderBitmapWrapper.getBitmap();
    }

    public Bitmap getRenderBitmap() {
        return renderBitmapWrapper.getBitmap();
    }

    // copy from render bitmap to surfaceView bitmap.
    public void copyBitmap() {
        if (renderBitmapWrapper == null) {
            return;
        }
        final Bitmap bitmap = renderBitmapWrapper.getBitmap();
        if (bitmap == null) {
            return;
        }
        viewBitmapWrapper.copyFrom(bitmap);
    }

    public Bitmap getViewBitmap() {
        if (viewBitmapWrapper == null) {
            return null;
        }
        return viewBitmapWrapper.getBitmap();
    }

    public void submit(final Context context, final ReaderBaseNoteRequest request, final BaseCallback callback) {
        beforeSubmit(context, Integer.MAX_VALUE, request, callback);
        getRequestManager().submitRequest(context, request, generateRunnable(request), callback);
    }

    public void submitWithUniqueId(final Context context, int uniqueId, final ReaderBaseNoteRequest request, final BaseCallback callback) {
        beforeSubmit(context, uniqueId, request, callback);
        getRequestManager().submitRequest(context, request, generateRunnable(request), callback);
    }


    public void submitRequestWithIdentifier(final Context context,
                                            final String identifier,
                                            final ReaderBaseNoteRequest request,
                                            final BaseCallback callback) {
        beforeSubmit(context, Integer.MAX_VALUE, request, callback);
        getRequestManager().submitRequest(context, identifier, request, generateRunnable(request), callback);
    }

    private void beforeSubmit(final Context context, int uniqueId, final ReaderBaseNoteRequest request, final BaseCallback callback) {
        final Rect rect = getViewportSize();
        if (rect != null) {
            request.setViewportSize(rect);
        }
        request.setAssociatedUniqueId(uniqueId);
        if (request.isResetNoteDataInfo()) {
            resetNoteDataInfo();
        }
    }

    private final Runnable generateRunnable(final ReaderBaseNoteRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(NoteManager.this);
                    request.execute(NoteManager.this);
                } catch (java.lang.Exception exception) {
                    Debug.d(exception.toString());
                    request.setException(exception);
                } finally {
                    request.afterExecute(NoteManager.this);
                    getRequestManager().dumpWakelocks();
                    getRequestManager().removeRequest(request);
                }
            }
        };
        return runnable;
    }

    private Rect getViewportSize() {
        if (surfaceView != null) {
            return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        }
        return null;
    }

    public void onNewStash(final Shape shape) {
        if (shape != null) {
            // TODO work around of null shape, deep fix in future
            shapeStash.add(shape);
        }
    }

    public final NoteDrawingArgs getNoteDrawingArgs() {
        return noteDocument.getNoteDrawingArgs();
    }

    public void setCurrentShapeType(int type) {
        getNoteDrawingArgs().setCurrentShapeType(type);
    }

    public void setCurrentShapeColor(int color) {
        getNoteDrawingArgs().setStrokeColor(color);
    }

    public void restoreCurrentShapeType() {
        getNoteDrawingArgs().restoreCurrentShapeType();
    }

    public void setCurrentStrokeWidth(float w) {
        getNoteDrawingArgs().strokeWidth = w;
        getTouchHelper().setStrokeWidth(w);
    }

    public void restoreStrokeWidth() {
        if (noteDocument != null && noteDocument.isOpen()) {
            setCurrentStrokeWidth(noteDocument.getStrokeWidth());
        }
    }

    public Shape getCurrentShape() {
        return shapeEventHandler.getCurrentShape();
    }

    public boolean isDFBForCurrentShape() {
        return ShapeFactory.isDFBShape(getNoteDrawingArgs().getCurrentShapeType());
    }

    public boolean isEraser() {
        return getNoteDrawingArgs().getCurrentShapeType() == ShapeFactory.SHAPE_ERASER;
    }

    public boolean isInSelection() {
        return getNoteDrawingArgs().getCurrentShapeType() == ShapeFactory.SHAPE_SELECTOR;
    }

    public void resetSelection() {
        if (isInSelection()) {
            getNoteDrawingArgs().resetCurrentShapeType();
        }
    }

    public boolean isSideNoting() {
        return sideNoting;
    }

    public void setSideNoting(boolean sideNoting) {
        this.sideNoting = sideNoting;
    }

    public boolean isSidePage(PageInfo pageInfo) {
        return pageInfo.getSubPage() > 0;
    }

    public void setVisiblePages(final List<PageInfo> list) {
        visiblePages.clear();
        visiblePages.addAll(list);

        getTouchHelper().setLimitRect(getLimitRegionOfVisiblePages());
    }

    public final RenderContext getRenderContext() {
        return renderContext;
    }

    public final List<Shape> detachShapeStash() {
        final List<Shape> list = shapeStash;
        shapeStash = new ArrayList<>();
        return list;
    }

    public final List<Shape> getShapeStash() {
        return shapeStash;
    }

    public boolean hasShapeStash() {
        return shapeStash.size() > 0;
    }

    public void undo(final Context context, final String pageName, int subPage) {
        final ReaderNotePage readerNotePage = getNoteDocument().loadPage(context, pageName, subPage);
        if (readerNotePage != null) {
            readerNotePage.undo();
        }
    }

    public void redo(final Context context, final String pageName, int subPage) {
        final ReaderNotePage readerNotePage = getNoteDocument().loadPage(context, pageName, subPage);
        if (readerNotePage != null) {
            readerNotePage.redo();
        }
    }

    public void saveNoteDataInfo(final ReaderBaseNoteRequest request) {
        noteDataInfo = request.getNoteDataInfo();
        noteDataInfo.setRequestFinished(true);
    }

    public final ReaderNoteDataInfo getNoteDataInfo() {
        return noteDataInfo;
    }

    public void resetNoteDataInfo() {
        noteDataInfo.setRequestFinished(false);
    }

    public void ensureContentRendered() {
        if (getNoteDataInfo().isRequestFinished()) {
            getNoteDataInfo().setContentRendered(true);
        }
    }

    public boolean isNoteDirty() {
        return noteDirty.get();
    }

    public void setNoteDirty(boolean dirty) {
        noteDirty.set(dirty);
    }

    public void setEnableShortcutDrawing(boolean enableShortcutDrawing) {
        if (noteConfig != null && noteConfig.isShortcutDrawingEnabled() && noteConfig.supportBigPen()) {
            this.enableShortcutDrawing = enableShortcutDrawing;
        }
    }

    public void setEnableShortcutErasing(boolean enableShortcutErasing) {
        if (noteConfig != null && noteConfig.isShortcutDrawingEnabled() && noteConfig.supportBigPen()) {
            this.enableShortcutErasing = enableShortcutErasing;
        }
    }

    public TouchPoint getEraserPoint() {
        return shapeEventHandler.getEraserPoint();
    }

    private List<Rect> getLimitRegionOfVisiblePages() {
        List<Rect> list = new ArrayList<>();
        for (PageInfo page : visiblePages) {
            if (sideNoting && !isSidePage(page)) {
                continue;
            }
            Rect r = RectUtils.toRect(page.getDisplayRect());
            if (visibleDrawRect != null) {
                r.intersect(visibleDrawRect);
            }
            list.add(r);
        }
        return list;
    }

}
