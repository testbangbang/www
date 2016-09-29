package com.onyx.kreader.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.note.bridge.NoteEventProcessorBase;
import com.onyx.kreader.note.bridge.NoteEventProcessorManager;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.note.data.ReaderNoteDocument;
import com.onyx.kreader.note.data.ReaderNotePage;
import com.onyx.kreader.note.data.ReaderShapeFactory;
import com.onyx.kreader.note.request.ReaderBaseNoteRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ShapeDrawingEvent;
import com.onyx.kreader.ui.events.ShapeErasingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/2/16.
 */
public class NoteManager {

    private RequestManager requestManager = new RequestManager(Thread.NORM_PRIORITY);
    private NoteEventProcessorManager noteEventProcessorManager;
    private ReaderNoteDocument noteDocument = new ReaderNoteDocument();
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();
    private ReaderBitmapImpl viewBitmapWrapper = new ReaderBitmapImpl();
    private volatile View view;

    private volatile Shape currentShape = null;
    private volatile NoteDrawingArgs noteDrawingArgs = new NoteDrawingArgs();
    private RenderContext renderContext = new RenderContext();

    private List<Shape> shapeStash = new ArrayList<>();
    private DeviceConfig noteConfig;
    private List<PageInfo> visiblePages = new ArrayList<>();
    private ReaderDataHolder parent;
    private ReaderNoteDataInfo noteDataInfo;
    private RectF visibleDrawRectF;

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

    public void stopRawEventProcessor() {
        getNoteEventProcessorManager().stop();
    }

    public void pauseRawEventProcessor() {
        getNoteEventProcessorManager().pause();
    }

    public void resumeRawEventProcessor() {
        getNoteEventProcessorManager().resume();
    }

    public void updateHostView(final Context context, final View sv, Rect visibleDrawRect) {
        view = sv;
        noteConfig = DeviceConfig.sharedInstance(context, "note");
        getNoteEventProcessorManager().update(view, noteConfig, visibleDrawRect);
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
                if (!shape.supportDFB()) {
                    getParent().getEventBus().post(new ShapeDrawingEvent(shape));
                }
            }

            @Override
            public void onDrawingTouchMove(MotionEvent motionEvent, Shape shape, boolean last) {
                if (!shape.supportDFB() && last) {
                    getParent().getEventBus().post(new ShapeDrawingEvent(shape));
                }
            }

            @Override
            public void onDrawingTouchUp(MotionEvent motionEvent, Shape shape) {
                onNewStash(shape);
                if (!shape.supportDFB()) {
                    getParent().getEventBus().post(new ShapeDrawingEvent(shape));
                }
            }

            public void onErasingTouchDown(final MotionEvent motionEvent, final Shape shape) {
                getParent().getEventBus().post(new ShapeErasingEvent());
            }

            public void onErasingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last) {
                if (last) {
                    getParent().getEventBus().post(new ShapeErasingEvent());
                }
            }

            public void onErasingTouchUp(final MotionEvent motionEvent, final Shape shape) {
                getParent().getEventBus().post(new ShapeErasingEvent());
            }

            public void onDFBShapeFinished(final Shape shape) {
                onNewStash(shape);
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

    // copy from render bitmap to view bitmap.
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
        beforeSubmit(context, request, callback);
        getRequestManager().submitRequest(context, request, generateRunnable(request), callback);
    }

    public void submitRequestWithIdentifier(final Context context,
                                            final String identifier,
                                            final ReaderBaseNoteRequest request,
                                            final BaseCallback callback) {
        beforeSubmit(context, request, callback);
        getRequestManager().submitRequest(context, identifier, request, generateRunnable(request), callback);
    }

    private void beforeSubmit(final Context context, final ReaderBaseNoteRequest request, final BaseCallback callback) {
        final Rect rect = getViewportSize();
        if (rect != null) {
            request.setViewportSize(rect);
        }

        resetNoteDataInfo();
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
        shapeStash.add(shape);
    }

    public final NoteDrawingArgs getNoteDrawingArgs() {
        return noteDrawingArgs;
    }

    public void setCurrentShapeType(int type) {
        getNoteDrawingArgs().currentShapeType = type;
    }

    public void setCurrentStrokeWidth(float w) {
        getNoteDrawingArgs().strokeWidth = w;
    }

    public Shape createNewShape(final PageInfo pageInfo) {
        Shape shape = ShapeFactory.createShape(getNoteDrawingArgs().currentShapeType);
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
        return ShapeFactory.isDFBShape(getNoteDrawingArgs().currentShapeType);
    }

    public boolean isEraser() {
        return getNoteDrawingArgs().currentShapeType == ShapeFactory.SHAPE_ERASER;
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
    }

    public final ReaderNoteDataInfo getNoteDataInfo() {
        return noteDataInfo;
    }

    public void resetNoteDataInfo() {
        noteDataInfo = null;
    }

    public void ensureContentRendered() {
        if (getNoteDataInfo() != null) {
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
}
