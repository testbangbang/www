package com.onyx.kreader.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.asyncrequest.ConfigManager;
import com.onyx.android.sdk.scribble.asyncrequest.TouchHelper;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EndRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
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
import com.onyx.kreader.note.data.ReaderShapeFactory;
import com.onyx.kreader.note.request.ReaderBaseNoteRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ShapeAddedEvent;
import com.onyx.kreader.ui.events.ShapeDrawingEvent;
import com.onyx.kreader.ui.events.ShapeErasingEvent;
import com.onyx.kreader.ui.events.ShortcutDrawingFinishedEvent;
import com.onyx.kreader.ui.events.ShortcutDrawingStartEvent;
import com.onyx.kreader.ui.events.ShortcutErasingFinishEvent;
import com.onyx.kreader.ui.events.ShortcutErasingStartEvent;

import org.greenrobot.eventbus.Subscribe;

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

    private volatile Shape currentShape = null;
    private volatile NoteDrawingArgs noteDrawingArgs = new NoteDrawingArgs();
    private RenderContext renderContext = new RenderContext();

    private List<Shape> shapeStash = new ArrayList<>();
    private DeviceConfig noteConfig;
    private MappingConfig mappingConfig;
    private List<PageInfo> visiblePages = new ArrayList<>();
    private ReaderDataHolder parent;
    private ReaderNoteDataInfo noteDataInfo = new ReaderNoteDataInfo();
    private RectF visibleDrawRectF;
    private volatile boolean enableRawEventProcessor = false;
    private AtomicBoolean noteDirty = new AtomicBoolean(false);
    private volatile boolean enableShortcutDrawing = false;
    private volatile boolean enableShortcutErasing = false;
    private boolean useWakeLock = true;
    private WakeLockHolder wakeLockHolder = new WakeLockHolder(false);
    private TouchPoint eraserPoint;

    public NoteManager(final ReaderDataHolder p) {
        parent = p;
        p.getEventBus().register(this);
        ConfigManager.init(p.getContext().getApplicationContext());
        getTouchHelper().setUseRawInput(ConfigManager.getInstance().getDeviceConfig().useRawInput());
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final ReaderNoteDocument getNoteDocument() {
        return noteDocument;
    }

    public void startRawEventProcessor() {
        getTouchHelper().startRawDrawing();
    }

    public void enableRawEventProcessor(boolean enable) {
        enableRawEventProcessor = enable;
        if (!enable) {
            releaseWakeLock();
        }
    }

    public void stopRawEventProcessor() {
        releaseWakeLock();
        getTouchHelper().quitRawDrawing();
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
        getTouchHelper().setup(surfaceView);
        getTouchHelper().setCustomLimitRect(surfaceView, visibleDrawRect,
                RectUtils.toRectList(excludeRect));
    }

    public final TouchHelper getTouchHelper() {
        if (touchHelper == null) {
            touchHelper = new TouchHelper(parent.getEventBus());
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

    public ReaderDataHolder getParent() {
        return parent;
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

    private void onNewStash(final Shape shape) {
        shapeStash.add(shape);
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

    public Shape createNewShape(final PageInfo pageInfo) {
        ReaderNotePage page = getNoteDocument().ensurePageExist(null, pageInfo.getName(), pageInfo.getSubPage());
        Shape shape = ShapeFactory.createShape(getNoteDrawingArgs().getCurrentShapeType());
        shape.setStrokeWidth(getNoteDrawingArgs().strokeWidth);
        shape.setColor(getNoteDrawingArgs().strokeColor);
        shape.setPageUniqueId(pageInfo.getName());
        shape.setSubPageUniqueId(page.getSubPageUniqueId());
        shape.ensureShapeUniqueId();
        currentShape = shape;
        return shape;
    }

    public Shape getCurrentShape() {
        return currentShape;
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

    public void resetCurrentShape() {
        currentShape = null;
    }

    public void onDownMessage(final Shape currentShape) {
        if (ReaderShapeFactory.isDFBShape(currentShape.getType())) {
            enableScreenPost(false);
        } else {
            enableScreenPost(true);
        }
    }

    public void setVisiblePages(final List<PageInfo> list) {
        visiblePages.clear();
        visiblePages.addAll(list);
    }

    public final PageInfo hitTest(final float x, final float y) {
        for(PageInfo pageInfo : visiblePages) {
            if (pageInfo.getDisplayRect().contains(x, y)) {
                return pageInfo;
            }
        }
        return null;
    }

    public final RenderContext getRenderContext() {
        return renderContext;
    }

    public final List<Shape> detachShapeStash() {
        final List<Shape> list = shapeStash;
        shapeStash = new ArrayList<>();
        currentShape = null;
        return list;
    }

    public final List<Shape> getShapeStash() {
        return shapeStash;
    }

    public boolean hasShapeStash() {
        return shapeStash.size() > 0;
    }

    public void undo(final Context context, final String pageName) {
        final ReaderNotePage readerNotePage = getNoteDocument().loadPage(context, pageName, 0);
        if (readerNotePage != null) {
            readerNotePage.undo();
        }
    }

    public void redo(final Context context, final String pageName) {
        final ReaderNotePage readerNotePage = getNoteDocument().loadPage(context, pageName, 0);
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

    public Shape ensureNewShape(final TouchPoint normalizedPoint, final TouchPoint screen) {
        final PageInfo pageInfo = hitTest(normalizedPoint.x, normalizedPoint.y);
        if (pageInfo == null) {
            return null;
        }
        Shape shape = getCurrentShape();
        if (shape == null) {
            shape = createNewShape(pageInfo);
            onDownMessage(shape);
            shape.onDown(normalizedPoint, screen);
            return shape;
        }
        shape.onMove(normalizedPoint, screen);
        return shape;
    }

    public void setVisibleDrawRectF(RectF visibleDrawRectF) {
        this.visibleDrawRectF = visibleDrawRectF;
    }

    public boolean inVisibleDrawRectF(float x, float y){
        return visibleDrawRectF.contains(x, y);
    }

    public Shape collectPoint(final PageInfo pageInfo, final TouchPoint point, boolean createShape, boolean up) {
        float[] srcPoint = new float[2];
        float[] dstPoint = new float[2];

        srcPoint[0] = point.x;
        srcPoint[1] = point.y;
        EpdController.mapToEpd(surfaceView, srcPoint, dstPoint);
        final TouchPoint screen = new TouchPoint(dstPoint[0], dstPoint[1],
                point.getPressure(), point.getSize(),
                point.getTimestamp());


        if (pageInfo == null) {
            return onShapeUp(pageInfo, point, screen);
        }
        point.normalize(pageInfo);
        if (getCurrentShape() == null) {
            if (createShape) {
                return onShapeDown(pageInfo, point, screen);
            }
            return null;
        }
        if (!up) {
            return onShapeMove(pageInfo, point, screen);
        }
        return onShapeUp(pageInfo, point, screen);
    }

    private Shape onShapeDown(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen) {
        ReaderNotePage page = getNoteDocument().ensurePageExist(null, pageInfo.getName(), pageInfo.getSubPage());
        Shape shape = ShapeFactory.createShape(getNoteDrawingArgs().getCurrentShapeType());
        onDownMessage(shape);
        shape.setStrokeWidth(getNoteDrawingArgs().strokeWidth / pageInfo.getActualScale());
        shape.setColor(getNoteDrawingArgs().strokeColor);
        shape.setPageUniqueId(pageInfo.getName());
        shape.setSubPageUniqueId(page.getSubPageUniqueId());
        shape.ensureShapeUniqueId();
        shape.setDisplayStrokeWidth(getNoteDrawingArgs().strokeWidth);
        shape.onDown(normal, screen);
        currentShape = shape;
        return shape;
    }

    private Shape onShapeMove(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen) {
        getCurrentShape().onMove(normal, screen);
        return getCurrentShape();
    }

    private Shape onShapeUp(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen) {
        final Shape shape = getCurrentShape();
        if (shape == null) {
            return null;
        }
        shape.onUp(normal, screen);
        resetCurrentShape();
        return shape;
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

    private TouchPoint fromHistorical(final MotionEvent motionEvent, int i) {
        final TouchPoint normalized = new TouchPoint(motionEvent.getHistoricalX(i),
                motionEvent.getHistoricalY(i),
                motionEvent.getHistoricalPressure(i),
                motionEvent.getHistoricalSize(i),
                motionEvent.getHistoricalEventTime(i));
        return normalized;
    }

    private PageInfo lastPageInfo = null;

    public void onDrawingTouchDown(MotionEvent motionEvent) {
        final TouchPoint touchPoint = new TouchPoint(motionEvent);
        lastPageInfo = hitTest(touchPoint.getX(), touchPoint.getY());
        if (lastPageInfo == null) {
            return;
        }
        final Shape shape = collectPoint(lastPageInfo, touchPoint, true, false);
        getParent().getEventBus().post(new ShapeDrawingEvent(shape));
    }

    public void onDrawingTouchMove(MotionEvent motionEvent) {
        if (lastPageInfo == null) {
            return;
        }

        int n = motionEvent.getHistorySize();
        for (int i = 0; i < n; i++) {
            final TouchPoint touchPoint = fromHistorical(motionEvent, i);
            PageInfo pageInfo = hitTest(touchPoint.getX(), touchPoint.getY());
            if (pageInfo != lastPageInfo) {
                 continue;
            }
            collectPoint(lastPageInfo, touchPoint, true, false);
        }

        TouchPoint touchPoint = new TouchPoint(motionEvent);
        PageInfo pageInfo = hitTest(touchPoint.getX(), touchPoint.getY());
        if (pageInfo != lastPageInfo) {
            return;
        }
        final Shape shape = collectPoint(lastPageInfo, touchPoint, true, false);
        getParent().getEventBus().post(new ShapeDrawingEvent(shape));
    }

    public void onDrawingTouchUp(MotionEvent motionEvent) {
        if (lastPageInfo == null) {
            return;
        }
        finishCurrentShape();
    }

    public void onErasingTouchDown(final MotionEvent motionEvent) {
    }

    public void onErasingTouchMove(final MotionEvent motionEvent) {
        TouchPointList list = new TouchPointList();
        int n = motionEvent.getHistorySize();
        for (int i = 0; i < n; i++) {
            list.add(fromHistorical(motionEvent, i));
        }
        list.add(new TouchPoint(motionEvent));
        getParent().getEventBus().post(new ShapeErasingEvent(false, true, list));

        eraserPoint = list.get(list.size() - 1);
    }

    public void onErasingTouchUp(final MotionEvent motionEvent) {
        eraserPoint = null;
    }

    @Subscribe
    public void onErasingTouchEvent(ErasingTouchEvent e) {
        if (isDFBForCurrentShape()) {
            return;
        }
        switch (e.getMotionEvent().getAction()) {
            case MotionEvent.ACTION_DOWN:
                onErasingTouchDown(e.getMotionEvent());
                break;
            case MotionEvent.ACTION_MOVE:
                onErasingTouchMove(e.getMotionEvent());
                break;
            case MotionEvent.ACTION_UP:
                onErasingTouchUp(e.getMotionEvent());
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onDrawingTouchEvent(DrawingTouchEvent e) {
        if (isDFBForCurrentShape()) {
            return;
        }
        switch (e.getMotionEvent().getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEraser()) {
                    onErasingTouchDown(e.getMotionEvent());
                } else {
                    onDrawingTouchDown(e.getMotionEvent());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isEraser()) {
                    onErasingTouchMove(e.getMotionEvent());
                } else {
                    onDrawingTouchMove(e.getMotionEvent());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isEraser()) {
                    onErasingTouchUp(e.getMotionEvent());
                } else {
                    onDrawingTouchUp(e.getMotionEvent());
                }
                break;
            default:
                break;
        }
    }

    private boolean shortcutDrawing = false;

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent e) {
        Debug.e(getClass(), "onBeginRawDataEvent");
        if (e.isShortcutDrawing()) {
            shortcutDrawing = true;
            getParent().getEventBus().post(new ShortcutDrawingStartEvent());
        }
    }

    @Subscribe
    public void onEndRawDataEvent(EndRawDataEvent e) {
        Debug.e(getClass(), "onEndRawDataEvent");
//        onNewStash(e.shape);
    }

    private void finishCurrentShape() {
        onNewStash(currentShape);
        resetCurrentShape();
        if (shortcutDrawing) {
            getParent().getEventBus().post(new ShortcutDrawingFinishedEvent());
        } else {
            getParent().getEventBus().post(new ShapeAddedEvent());
        }
    }

    @Subscribe
    public void onRawTouchPointMoveReceivedEvent(RawTouchPointMoveReceivedEvent e) {
    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent e) {
        Debug.e(getClass(), "onRawTouchPointListReceivedEvent");
        PageInfo lastPageInfo = null;
        for (TouchPoint p : e.getTouchPointList().getPoints()) {
            PageInfo pageInfo = hitTest(p.getX(), p.getY());
            if (pageInfo == null || (lastPageInfo != null && pageInfo != lastPageInfo)) {
                if (currentShape != null) {
                    finishCurrentShape();
                }
                continue;
            }

            collectPoint(pageInfo, p, true, false);
        }
        if (currentShape != null) {
            finishCurrentShape();
        }
    }

    @Subscribe
    public void onRawErasingStartEvent(BeginRawErasingEvent e) {
        Debug.e(getClass(), "onRawErasingStartEvent");
        getParent().getEventBus().post(new ShortcutErasingStartEvent());
    }

    @Subscribe
    public void onRawErasingFinishEvent(RawErasePointListReceivedEvent e) {
        Debug.e(getClass(), "onRawErasingFinishEvent");
        getParent().getEventBus().post(new ShortcutErasingFinishEvent(e.getTouchPointList()));
    }

    @Subscribe
    public void onRawErasePointMoveReceivedEvent(RawErasePointMoveReceivedEvent e) {

    }

    @Subscribe
    public void onRawErasePointListReceivedEvent(RawErasePointListReceivedEvent e) {
        Debug.e(getClass(), "onRawErasePointListReceivedEvent");

    }

    public TouchPoint getEraserPoint() {
        return eraserPoint;
    }
}
