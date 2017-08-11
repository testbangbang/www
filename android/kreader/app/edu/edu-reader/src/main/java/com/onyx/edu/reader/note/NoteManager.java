package com.onyx.edu.reader.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.reader.api.ReaderFormScribble;
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
import com.onyx.edu.reader.note.bridge.NoteEventProcessorBase;
import com.onyx.edu.reader.note.bridge.NoteEventProcessorManager;
import com.onyx.edu.reader.note.data.ReaderNoteDataInfo;
import com.onyx.edu.reader.note.data.ReaderNoteDocument;
import com.onyx.edu.reader.note.data.ReaderNotePage;
import com.onyx.edu.reader.note.data.ReaderShapeFactory;
import com.onyx.edu.reader.note.request.ReaderBaseNoteRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ShapeDrawingEvent;
import com.onyx.edu.reader.ui.events.ShortcutDrawingFinishedEvent;
import com.onyx.edu.reader.ui.events.ShapeErasingEvent;
import com.onyx.edu.reader.ui.events.ShortcutDrawingStartEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingFinishEvent;
import com.onyx.edu.reader.ui.events.ShortcutErasingStartEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhuzeng on 9/2/16.
 */
public class NoteManager {

    private static final String TAG = NoteManager.class.getSimpleName();
    private RequestManager requestManager = new RequestManager(Thread.NORM_PRIORITY);
    private NoteEventProcessorManager noteEventProcessorManager;
    private ReaderNoteDocument noteDocument = new ReaderNoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private ReaderBitmapImpl viewBitmapWrapper = new ReaderBitmapImpl();

    private ReaderBitmapImpl reviewBufferBitmap = new ReaderBitmapImpl();
    private ReaderBitmapImpl reviewBitmapWrapper = new ReaderBitmapImpl();

    private volatile View view;

    private volatile Shape currentShape = null;
    private volatile NoteDrawingArgs noteDrawingArgs = new NoteDrawingArgs();
    private RenderContext noteRenderContext = new RenderContext();
    private RenderContext reviewRenderContext = new RenderContext();

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

    public NoteManager(final ReaderDataHolder p) {
        parent = p;
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final ReaderNoteDocument getNoteDocument() {
        return noteDocument;
    }

    public void startRawEventProcessor() {
        getNoteEventProcessorManager().start();
    }

    public void enableRawEventProcessor(boolean enable) {
        enableRawEventProcessor = enable;
        if (!enable) {
            releaseWakeLock();
        }
    }

    public void stopRawEventProcessor() {
        releaseWakeLock();
        getNoteEventProcessorManager().stop();
    }

    public void pauseRawEventProcessor() {
        releaseWakeLock();
        getNoteEventProcessorManager().pause();
    }

    public void resumeRawEventProcessor(final Context context) {
        if (enableRawEventProcessor) {
            acquireWakeLock(context);
        }
        getNoteEventProcessorManager().resume();
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

    public void updateHostView(final Context context, final View sv, final Rect visibleDrawRect, final List<RectF> excludeRect, int orientation) {
        if (noteConfig == null || mappingConfig == null) {
            initNoteArgs(context);
        }
        view = sv;
        getNoteEventProcessorManager().update(view, noteConfig, mappingConfig, visibleDrawRect, excludeRect, orientation);
        setVisibleDrawRectF(new RectF(visibleDrawRect.left, visibleDrawRect.top, visibleDrawRect.right, visibleDrawRect.bottom));
    }

    public final NoteEventProcessorManager getNoteEventProcessorManager() {
        if (noteEventProcessorManager == null) {
            noteEventProcessorManager = new NoteEventProcessorManager(this);
        }
        return noteEventProcessorManager;
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

    public final NoteEventProcessorBase.InputCallback getInputCallback() {
        NoteEventProcessorBase.InputCallback inputCallback = new NoteEventProcessorBase.InputCallback() {

            @Override
            public void onDrawingTouchDown(MotionEvent motionEvent, Shape shape) {
                if (shape != null && !shape.supportDFB()) {
                    getParent().getEventBus().post(new ShapeDrawingEvent(shape));
                }
            }

            @Override
            public void onDrawingTouchMove(MotionEvent motionEvent, Shape shape, boolean last) {
                if (shape != null && !shape.supportDFB() && last) {
                    getParent().getEventBus().post(new ShapeDrawingEvent(shape));
                }
            }

            @Override
            public void onDrawingTouchUp(MotionEvent motionEvent, Shape shape) {
                if (shape == null) {
                    return;
                }
                onNewStash(shape);
                if (!shape.supportDFB()) {
                    getParent().getEventBus().post(new ShapeDrawingEvent(shape));
                }
            }

            public void onErasingTouchDown(final MotionEvent motionEvent, final TouchPointList list) {
                getParent().getEventBus().post(new ShapeErasingEvent(true, false, list));
            }

            public void onErasingTouchMove(final MotionEvent motionEvent, final TouchPointList list, boolean last) {
                if (last) {
                    getParent().getEventBus().post(new ShapeErasingEvent(false, false, list));
                }
            }

            public void onErasingTouchUp(final MotionEvent motionEvent, final TouchPointList list) {
                getParent().getEventBus().post(new ShapeErasingEvent(false, true, list));
            }

            public void onRawErasingStart() {
                getParent().getEventBus().post(new ShortcutErasingStartEvent());
            }

            public void onRawErasingFinished(final TouchPointList list) {
                getParent().getEventBus().post(new ShortcutErasingFinishEvent(list));
            }

            public void onDFBShapeStart(boolean shortcut) {
                if (shortcut) {
                    getParent().getEventBus().post(new ShortcutDrawingStartEvent());
                }
            }

            public void onDFBShapeFinished(final Shape shape, boolean shortcut) {
                onNewStash(shape);
                if (shortcut) {
                    getParent().getEventBus().post(new ShortcutDrawingFinishedEvent());
                }
            }

            public boolean enableShortcutDrawing() {
                if (getNoteDataInfo() == null) {
                    return enableShortcutDrawing;
                }
                return enableShortcutDrawing && !isInSelection();
            }

            public boolean enableShortcutErasing() {
                return enableShortcutErasing;
            }

            public boolean enableRawEventProcessor() {
                return enableRawEventProcessor;
            }

            public void enableTouchInput(boolean enable) {
                getParent().getHandlerManager().setEnableTouch(enable);
            }

        };
        return inputCallback;
    }

    public void enableScreenPost(boolean enable) {
        if (view != null) {
            EpdController.enablePost(view, enable ? 1 : 0);
        }
    }

    public Bitmap updateRenderBitmap(final Rect viewportSize) {
        renderBitmapWrapper.update(viewportSize.width(), viewportSize.height(), Bitmap.Config.ARGB_8888);
        return renderBitmapWrapper.getBitmap();
    }

    public Bitmap createReviewBufferBitmap(final Rect viewportSize) {
        reviewBufferBitmap.update(viewportSize.width() / 2, viewportSize.height() / 2 , Bitmap.Config.ARGB_8888);
        return reviewBufferBitmap.getBitmap();
    }

    public void updateReviewBufferBitmap(final Bitmap src) {
        reviewBufferBitmap.attach(src);
    }

    public Bitmap getReviewBitmap() {
        if (reviewBitmapWrapper == null) {
            return null;
        }
        return reviewBitmapWrapper.getBitmap();
    }

    // copy from renderNoteShapes bitmap to view bitmap.
    public void copyNoteBitmap() {
        if (renderBitmapWrapper == null) {
            return;
        }
        final Bitmap bitmap = renderBitmapWrapper.getBitmap();
        if (bitmap == null) {
            return;
        }
        viewBitmapWrapper.copyFrom(bitmap);
    }

    public void copyReviewBitmap() {
        if (reviewBufferBitmap == null) {
            return;
        }
        final Bitmap bitmap = reviewBufferBitmap.getBitmap();
        if (bitmap == null) {
            return;
        }
        reviewBitmapWrapper.copyFrom(bitmap);
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
        if (view != null) {
            return new Rect(0, 0, view.getWidth(), view.getHeight());
        }
        return null;
    }

    private void onNewStash(final Shape shape) {
        parent.getHandlerManager().getActiveProvider().onShapeAdded(shape);
        shapeStash.add(shape);
    }

    public void addNewStashList(final List<Shape> shapes) {
        shapeStash.addAll(shapes);
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
    }

    public Shape createNewShape(final PageInfo pageInfo) {
        Shape shape = ShapeFactory.createShape(getNoteDrawingArgs().getCurrentShapeType());
        shape.setStrokeWidth(getNoteDrawingArgs().strokeWidth);
        shape.setColor(getNoteDrawingArgs().strokeColor);
        shape.setPageUniqueId(pageInfo.getName());
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

    public boolean inScribbleRect(TouchPoint point) {
        return noteEventProcessorManager.inScribbleRect(point);
    }

    public final RenderContext getNoteRenderContext() {
        return noteRenderContext;
    }

    public RenderContext getReviewRenderContext() {
        return reviewRenderContext;
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

    public Shape collectPoint(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen, boolean createShape, boolean up) {
        if (pageInfo == null) {
            return onShapeUp(pageInfo, normal, screen);
        }
        TouchPoint origin = new TouchPoint(normal);
        normal.normalize(pageInfo);
        if (getCurrentShape() == null) {
            if (createShape) {
                return onShapeDown(pageInfo, normal, screen, origin);
            }
            return null;
        }
        if (!up) {
            return onShapeMove(pageInfo, normal, screen);
        }
        return onShapeUp(pageInfo, normal, screen);
    }

    private Shape onShapeDown(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen, final TouchPoint origin) {
        Shape shape = ShapeFactory.createShape(getNoteDrawingArgs().getCurrentShapeType());
        onDownMessage(shape);
        shape.setStrokeWidth(getNoteDrawingArgs().strokeWidth / pageInfo.getActualScale());
        shape.setColor(getNoteDrawingArgs().strokeColor);
        shape.setPageUniqueId(pageInfo.getName());
        shape.ensureShapeUniqueId();
        shape.setDisplayStrokeWidth(getNoteDrawingArgs().strokeWidth);
        shape.setPageOriginWidth((int) pageInfo.getOriginWidth());
        shape.setPageOriginHeight((int) pageInfo.getOriginHeight());
        shape.onDown(normal, screen);
        detectionScribbleFormShape(pageInfo, shape, origin);
        currentShape = shape;
        return shape;
    }

    private void detectionScribbleFormShape(final PageInfo pageInfo, final Shape shape, final TouchPoint origin) {
        if (!parent.inFormProvider()) {
            return;
        }
        if (!parent.getHandlerManager().isEnableNoteWhenHaveScribbleForm()) {
            shape.setFormShape(true);
            return;
        }
        ReaderFormField field = getScribbleFormField(pageInfo, origin);
        if (field != null) {
            shape.setFormShape(true);
            shape.setFormId(field.getName());
            shape.setFormRect(field.getRect());
            shape.setFormType(ReaderShapeFactory.SHAPE_FORM_QA);
        }
    }

    private ReaderFormField getScribbleFormField(final PageInfo pageInfo, final TouchPoint origin) {
        if (!parent.getReaderUserDataInfo().hasFormFields(pageInfo)) {
            return null;
        }
        List<ReaderFormField> fields = parent.getReaderUserDataInfo().getFormFields(pageInfo);
        for (ReaderFormField field : fields) {
            if (field instanceof ReaderFormScribble) {
                RectF rect = field.getRect();
                if (rect.contains(origin.x, origin.y)) {
                    return field;
                }
            }
        }
        return null;
    }

    private Shape onShapeMove(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen) {
        if (getCurrentShape().inVisibleDrawRectF(visibleDrawRectF)) {
            getCurrentShape().onMove(normal, screen);
        }
        return getCurrentShape();
    }

    private Shape onShapeUp(final PageInfo pageInfo, final TouchPoint normal, final TouchPoint screen) {
        final Shape shape = getCurrentShape();
        if (shape == null) {
            return null;
        }
        TouchPoint normalPoint = shape.inVisibleDrawRectF(visibleDrawRectF) ? normal : shape.getCurrentPoint();
        TouchPoint screenPoint = shape.inVisibleDrawRectF(visibleDrawRectF) ? screen : shape.getCurrentScreenPoint();
        shape.onUp(normalPoint, screenPoint);
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
}
