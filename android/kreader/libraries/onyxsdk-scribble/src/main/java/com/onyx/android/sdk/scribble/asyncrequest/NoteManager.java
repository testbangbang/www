package com.onyx.android.sdk.scribble.asyncrequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointsReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.TouchErasePointsReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.UpdateLineLayoutArgsEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.UpdateLineLayoutCursorEvent;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeRemoveByPointListRequest;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.data.SpanLayoutData;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.MappingConfig;
import com.onyx.android.sdk.scribble.utils.NoteViewUtil;
import com.onyx.android.sdk.scribble.view.LinedEditText;
import com.onyx.android.sdk.utils.Debug;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/2/11 18:44.
 */

public class NoteManager {

    private static final String TAG = NoteManager.class.getSimpleName();

    private RendererHelper rendererHelper;
    private DocumentHelper documentHelper;
    private ViewHelper viewHelper;
    private TouchHelper touchHelper;

    private ShapeDataInfo shapeDataInfo = new ShapeDataInfo();
    private SpanLayoutData spanLayoutData = new SpanLayoutData();
    private Context appContext;
    private RequestManager requestManager = new RequestManager();
    private EventBus eventBus = new EventBus();
    private List<Shape> dirtyStash = new ArrayList<>();
    private boolean drawing = false;
    private @ScribbleMode.ScribbleModeDef
    int currentLayoutMode = ScribbleMode.MODE_NORMAL_SCRIBBLE;

    public int getCurrentScribbleMode() {
        return currentLayoutMode;
    }

    public void setCurrentScribbleMode(int currentScribbleMode) {
        currentLayoutMode = currentScribbleMode;
    }

    public void resetScribbleMode() {
        setCurrentScribbleMode(ScribbleMode.MODE_NORMAL_SCRIBBLE);
    }

    public NoteManager(Context context) {
        appContext = context.getApplicationContext();
        ConfigManager.init(appContext);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void post(Object event) {
        getEventBus().post(event);
    }

    public void registerEventBus(Object subscriber) {
        getEventBus().register(subscriber);
    }

    public void unregisterEventBus(Object subscriber) {
        getEventBus().unregister(subscriber);
    }

    public DeviceConfig getDeviceConfig() {
        return ConfigManager.getInstance().getDeviceConfig();
    }

    public MappingConfig getMappingConfig() {
        return ConfigManager.getInstance().getMappingConfig();
    }

    public Context getAppContext() {
        return appContext;
    }

    public RendererHelper getRendererHelper() {
        if (rendererHelper == null) {
            rendererHelper = new RendererHelper();
        }
        return rendererHelper;
    }

    public DocumentHelper getDocumentHelper() {
        if (documentHelper == null) {
            documentHelper = new DocumentHelper();
        }
        return documentHelper;
    }

    public ViewHelper getViewHelper() {
        if (viewHelper == null) {
            viewHelper = new ViewHelper(eventBus);
        }
        return viewHelper;
    }

    public TouchHelper getTouchHelper() {
        if (touchHelper == null) {
            touchHelper = new TouchHelper(eventBus);
        }
        return touchHelper;
    }

    private Runnable generateRunnable(final AsyncBaseNoteRequest request) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(NoteManager.this);
                    request.execute(NoteManager.this);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.postExecute(NoteManager.this);
                    getRequestManager().dumpWakelocks();
                    getRequestManager().removeRequest(request);
                }
            }
        };
    }

    private void beforeSubmit(AsyncBaseNoteRequest request) {
    }

    public boolean submitRequest(final AsyncBaseNoteRequest request, final BaseCallback callback) {
        beforeSubmit(request);
        return getRequestManager().submitRequest(appContext,
                request, generateRunnable(request), callback);
    }

    public boolean submitRequestWithIdentifier(final AsyncBaseNoteRequest request,
                                               final String identifier,
                                               final BaseCallback callback) {
        beforeSubmit(request);
        request.setIdentifier(identifier);
        return getRequestManager().submitRequestToMultiThreadPool(appContext, identifier,
                request, generateRunnable(request), callback);
    }

    public void sync(boolean render,
                     boolean resume) {
        syncWithCallback(render, resume, null);
    }

    public void syncWithCallback(boolean render,
                                 boolean resume,
                                 final BaseCallback callback) {
        final List<Shape> stash = detachStash();
        if (inSpanLayoutMode()) {
            stash.clear();
        }
        PageFlushRequest flushRequest = new PageFlushRequest(stash, render, resume, shapeDataInfo.getDrawingArgs());
        submitRequest(flushRequest, callback);
    }

    public ShapeDataInfo getShapeDataInfo() {
        return shapeDataInfo;
    }

    public void setShapeDataInfo(ShapeDataInfo shapeDataInfo) {
        this.shapeDataInfo = shapeDataInfo;
    }

    public void setStrokeWidth(float strokeWidth, BaseCallback callback) {
        if (shapeDataInfo.isInUserErasing()) {
            shapeDataInfo.setCurrentShapeType(ShapeFactory.SHAPE_PENCIL_SCRIBBLE);
        }
        shapeDataInfo.setStrokeWidth(strokeWidth);
        syncWithCallback(true, true, callback);
    }

    public LineLayoutArgs getLineLayoutArgs() {
        return spanLayoutData.getLineLayoutArgs();
    }

    public boolean checkShapesOutOfRange(List<Shape> shapes) {
        return getTouchHelper().checkShapesOutOfRange(shapes);
    }

    private void updateLineLayoutArgs(LinedEditText spanTextView) {
        spanLayoutData.updateLineLayoutArgs(spanTextView);
    }

    @Subscribe
    public void updateLineLayoutCursorEvent(UpdateLineLayoutCursorEvent event) {
        if (event == null) {
            return;
        }
        spanLayoutData.updateLineLayoutCursor(event.getSpanTextView());
    }

    @Subscribe
    public void updateLineLayoutArgsEvent(UpdateLineLayoutArgsEvent event) {
        if (event == null) {
            return;
        }
        updateLineLayoutArgs(event.getSpanTextView());
    }

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent event) {
        Debug.e(getClass(), "onBeginRawDataEvent");
    }

    @Subscribe
    public void onBeginErasingEvent(BeginErasingEvent event) {
        Debug.i(getClass(), "onBeginErasingEvent");
        onBeginErasing();
    }

    @Subscribe
    public void onErasingEvent(ErasingEvent event) {
        onErasing(event.getTouchPoint(), event.isShowIndicator());
    }

    @Subscribe
    public void onTouchErasePointsReceivedEvent(TouchErasePointsReceivedEvent event) {
        Debug.i(getClass(), "onTouchErasePointsReceivedEvent: ");
        onFinishErasing(event.getTouchPointList());
    }

    @Subscribe
    public void onRawErasePointsReceivedEvent(RawErasePointsReceivedEvent event) {
        Debug.i(getClass(), "onRawErasePointsReceivedEvent: ");
        onFinishErasing(event.getTouchPointList());
    }

    public SurfaceView getHostView() {
        return getViewHelper().getHostView();
    }

    public void setView(SurfaceView surfaceView) {
        getViewHelper().setHostView(surfaceView);
        getTouchHelper().setup(surfaceView);
        registerEventBus(this);
    }

    protected void onBeginErasing() {
        syncWithCallback(true, false, null);
    }

    protected void onErasing(final TouchPoint touchPoint, final boolean showIndicator) {
        if (showIndicator) {
            getRendererHelper().drawErasingIndicator(getHostView(), new Paint(), touchPoint, shapeDataInfo.getEraserRadius());
        }
    }

    protected void onFinishErasing(TouchPointList pointList) {
        if (pointList == null) {
            return;
        }
        ShapeRemoveByPointListRequest changeRequest = new ShapeRemoveByPointListRequest(pointList);
        submitRequest(changeRequest, null);
    }

    private void drawCurrentPage() {
        AsyncBaseNoteRequest request = new AsyncBaseNoteRequest();
        submitRequest(request, null);
    }

    public void quit() {
        unregisterEventBus(this);
        getTouchHelper().quit();
        getViewHelper().quit();
        resetScribbleMode();
    }

    public boolean inSpanLayoutMode() {
        return currentLayoutMode == ScribbleMode.MODE_SPAN_SCRIBBLE;
    }

    public Shape getSpanCursorShape() {
        return spanLayoutData.getCursorShape();
    }

    public List<Shape> detachStash() {
        final List<Shape> temp = new ArrayList<>();
        temp.addAll(dirtyStash);
        dirtyStash = new ArrayList<>();
        return temp;
    }

    public void onNewShape(Shape shape) {
        dirtyStash.add(shape);
    }

    public void clearSurfaceView(SurfaceView surfaceView) {
        NoteViewUtil.clearSurfaceView(surfaceView);
    }

    public void setDrawing(boolean drawing) {
        this.drawing = drawing;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public boolean inUserErasing() {
        return getDocumentHelper().inUserErasing();
    }

    public NoteDocument getNoteDocument() {
        return getDocumentHelper().getNoteDocument();
    }

    public void pauseRawDrawing() {
        getTouchHelper().pauseRawDrawing();
    }

    public void resumeRawDrawing() {
        getDocumentHelper().setPenState(NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING);
        getTouchHelper().resumeRawDrawing();
        updateInUserErasingState();
    }

    public Bitmap getRenderBitmap() {
        return getRendererHelper().getRenderBitmap();
    }

    public List<Shape> getDirtyStash() {
        return dirtyStash;
    }

    public void clearPageUndoRedo(Context context) {
        getDocumentHelper().clearPageUndoRedo(context);
    }

    public void clearShapeSelectRecord(){
        for (int i = 0; i < getNoteDocument().getPageCount(); i++) {
            NotePage page = getNoteDocument().getPageByIndex(i);
            if (page != null) {
                page.clearShapeSelectRecord();
            }
        }
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public void enableScreenPost(boolean enable) {
        if (getHostView() != null) {
            EpdController.enablePost(getHostView(), enable ? 1 : 0);
        }
    }

    public void renderToSurfaceView() {
        getRendererHelper().renderToSurfaceView(getDirtyStash(), getHostView());
    }

    public Rect getViewportSize() {
        return getViewHelper().getViewportSize();
    }

    public RectF getViewportSizeF() {
        return getViewHelper().getViewportSizeF();
    }

    public void updateShapeDataInfo(final Context context, final ShapeDataInfo shapeDataInfo) {
        getDocumentHelper().updateShapeDataInfo(context, shapeDataInfo);
    }

    public void renderCurrentPageInBitmap(final AsyncBaseNoteRequest request) {
        rendererHelper.renderCurrentPageInBitmap(this, request);
    }

    public void renderSelectionRectangle(final TouchPoint start, final TouchPoint end) {
        rendererHelper.renderSelectionRectangle(this, start, end);
    }

    public int getCurrentShapeType() {
        return getNoteDocument().getNoteDrawingArgs().getCurrentShapeType();
    }

    public boolean useDFBForCurrentState() {
        return ShapeFactory.isDFBShape(getCurrentShapeType()) && !inUserErasing();
    }

    public void updateDrawingArgs(final NoteDrawingArgs drawingArgs) {
        getDocumentHelper().updateDrawingArgs(drawingArgs);
        updateRenderByFrameworkState();
        updateInUserErasingState();
    }

    public void openDocument(final Context context, final String documentUniqueId, final String parentUniqueId) {
        getDocumentHelper().openDocument(this, context, documentUniqueId, parentUniqueId);
        getRendererHelper().init();
    }

    public void createDocument(final Context context, final String documentUniqueId, final String parentUniqueId) {
        getDocumentHelper().createDocument(this, context, documentUniqueId, parentUniqueId);
        getRendererHelper().init();
    }

    public void setStrokeColor(int color) {
        getDocumentHelper().setStrokeColor(color);
    }

    public void setBackground(int type) {
        getDocumentHelper().setBackground(NoteBackgroundType.FILE);
    }

    public void setBackgroundFilePath(final String path) {
        getDocumentHelper().setBackgroundFilePath(path);
    }

    public void setLineLayoutBackground(int type) {
        getDocumentHelper().setLineLayoutBackground(type);
    }

    public void setCurrentShapeType(int type) {
        getDocumentHelper().setCurrentShapeType(type);
        updateRenderByFrameworkState();
        updateInUserErasingState();
    }

    public void save(final Context context, final String title , boolean closeAfterSave) {
        getDocumentHelper().save(context, title, closeAfterSave);
    }

    public void undo(final Context context) {
        getNoteDocument().getCurrentPage(context).undo(inSpanLayoutMode());
    }

    public void redo(final Context context) {
        getNoteDocument().getCurrentPage(context).redo(inSpanLayoutMode());
    }

    private void updateInUserErasingState() {
        getTouchHelper().setInUserErasing(inUserErasing());
    }

    private void updateRenderByFrameworkState() {
        boolean renderByFramework = ShapeFactory.isDFBShape(getDocumentHelper().getCurrentShapeType());
        getTouchHelper().setRenderByFramework(renderByFramework);
    }
}
