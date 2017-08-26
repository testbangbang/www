package com.onyx.android.sdk.scribble.asyncrequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceView;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchDownEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchMoveEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchUpEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EraseTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawDataReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeRemoveByPointListRequest;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.MappingConfig;
import com.onyx.android.sdk.scribble.utils.NoteViewUtil;
import com.onyx.android.sdk.scribble.view.LinedEditText;

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
    private SpanHelper spanHelper;
    private ViewHelper viewHelper;
    private TouchHelper touchHelper;
    private PenManager penManager;

    private ShapeDataInfo shapeDataInfo = new ShapeDataInfo();
    private Context appContext;
    private RequestManager requestManager = new RequestManager();
    private DeviceConfig deviceConfig;
    private MappingConfig mappingConfig;
    private List<Shape> dirtyStash = new ArrayList<>();
    private boolean drawing = false;
    private @ScribbleMode.ScribbleModeDef
    int mCurrentScribbleMode = ScribbleMode.MODE_NORMAL_SCRIBBLE;

    public int getCurrentScribbleMode() {
        return mCurrentScribbleMode;
    }

    public void setCurrentScribbleMode(int currentScribbleMode) {
        mCurrentScribbleMode = currentScribbleMode;
    }

    public void resetScribbleMode() {
        setCurrentScribbleMode(ScribbleMode.MODE_NORMAL_SCRIBBLE);
    }

    public NoteManager(Context context) {
        appContext = context.getApplicationContext();
        initRawResource(appContext);
    }

    private void initRawResource(final Context context) {
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
        mappingConfig = MappingConfig.sharedInstance(context, "note");
    }

    public DeviceConfig getDeviceConfig() {
        return deviceConfig;
    }

    public MappingConfig getMappingConfig() {
        return mappingConfig;
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
            documentHelper = new DocumentHelper(this);
        }
        return documentHelper;
    }

    public SpanHelper getSpanHelper() {
        if (spanHelper == null) {
            spanHelper = new SpanHelper(this);
        }
        return spanHelper;
    }

    public ViewHelper getViewHelper() {
        if (viewHelper == null) {
            viewHelper = new ViewHelper(this);
        }
        return viewHelper;
    }

    public TouchHelper getTouchHelper() {
        if (touchHelper == null) {
            touchHelper = new TouchHelper(this);
        }
        return touchHelper;
    }

    public PenManager getPenManager() {
        if (penManager == null) {
            penManager = new PenManager();
        }
        return penManager;
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
        if (inSpanScribbleMode()) {
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

    public void deleteSpan(boolean resume) {
        getSpanHelper().deleteSpan(resume);
    }

    public void updateLineLayoutArgs(LinedEditText spanTextView) {
        getSpanHelper().updateLineLayoutArgs(spanTextView);
    }

    public void updateLineLayoutCursor(LinedEditText spanTextView) {
        getSpanHelper().updateLineLayoutCursor(spanTextView);
    }

    public boolean checkShapesOutOfRange(List<Shape> shapes) {
        return getSpanHelper().checkShapesOutOfRange(shapes);
    }

    public void openSpanTextFunc() {
        getSpanHelper().openSpanTextFunc();
    }

    public void loadPageShapes() {
        getSpanHelper().loadPageShapes();
    }

    private void buildSpan() {
        getSpanHelper().buildSpan();
    }

    private void removeSpanRunnable() {
        getSpanHelper().removeSpanRunnable();
    }

    public void exitSpanTextFunc() {
        getSpanHelper().exitSpanTextFunc();
    }

    public void buildTextShape(String text, LinedEditText spanTextView) {
        getSpanHelper().buildTextShape(text, spanTextView);
    }

    public void buildSpaceShape(final int width, int height) {
        getSpanHelper().buildSpaceShape(width, height);
    }

    public void buildSpaceShape() {
        getSpanHelper().buildSpaceShape();
    }

    public void buildLineBreakShape(LinedEditText spanTextView) {
        getSpanHelper().buildLineBreakShape(spanTextView);
    }

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent event) {
        Log.e(TAG, "onBeginRawDataEvent");
        removeSpanRunnable();
    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent event) {
        Log.e(TAG, "onRawTouchPointListReceivedEvent");
        if (inSpanScribbleMode()) {
            buildSpan();
        }
        EventBus.getDefault().post(new RawDataReceivedEvent());
    }

    @Subscribe
    public void onBeginErasingEvent(BeginErasingEvent event) {
        Log.e(TAG, "onBeginErasingEvent");
        onBeginErasing();
    }

    @Subscribe
    public void onErasingEvent(ErasingEvent event) {
        Log.e(TAG, "onErasingEvent: ");
        onErasing(event.getTouchPoint(), event.isShowIndicator());
    }

    @Subscribe
    public void onEraseTouchPointListReceivedEvent(EraseTouchPointListReceivedEvent event) {
        Log.e(TAG, "onEraseTouchPointListReceivedEvent: ");
        onFinishErasing(event.getTouchPointList());
    }

    @Subscribe
    public void onDrawingTouchDownEvent(DrawingTouchDownEvent event) {
        Log.e(TAG, "onDrawingTouchDownEvent: ");
        if (!event.getShape().supportDFB()) {
            drawCurrentPage();
        }
    }

    @Subscribe
    public void onDrawingTouchMoveEvent(DrawingTouchMoveEvent event) {
        Log.e(TAG, "onDrawingTouchMoveEvent: ");
        if (event.isLast() && !event.getShape().supportDFB()) {
            drawCurrentPage();
        }
    }

    @Subscribe
    public void onDrawingTouchUpEvent(DrawingTouchUpEvent event) {
        Log.e(TAG, "onDrawingTouchUpEvent: ");
        if (!event.getShape().supportDFB()) {
            drawCurrentPage();
        }
        if (inSpanScribbleMode()) {
            buildSpan();
        }
    }

    public void reset() {
        getViewHelper().resetView();
    }

    public SurfaceView getHostView() {
        return getViewHelper().getHostView();
    }

    public void setView(SurfaceView surfaceView) {
        getPenManager().setHostView(surfaceView);
        getViewHelper().setHostView(surfaceView);
        getTouchHelper().onTouch(surfaceView);
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        getTouchHelper().quit();
        getViewHelper().quit();
        resetScribbleMode();
    }

    public boolean inSpanScribbleMode() {
        return mCurrentScribbleMode == ScribbleMode.MODE_SPAN_SCRIBBLE;
    }

    public void drawSpanLayoutBackground(final RenderContext renderContext) {
        getSpanHelper().drawLineLayoutBackground(renderContext, getHostView());
    }

    public Shape getSpanCursorShape() {
        return getSpanHelper().getCursorShape();
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
        getTouchHelper().resumeRawDrawing();
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
        getPenManager().enableScreenPost(enable);
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
    }

    public void openDocument(final Context context, final String documentUniqueId, final String parentUniqueId) {
        getDocumentHelper().getNoteDocument().open(context, documentUniqueId, parentUniqueId);
        getRendererHelper().init();
    }

    public void createDocument(final Context context, final String documentUniqueId, final String parentUniqueId) {
        getNoteDocument().create(context, documentUniqueId, parentUniqueId);
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
    }

    public void save(final Context context, final String title , boolean closeAfterSave) {
        getDocumentHelper().save(context, title, closeAfterSave);
    }

    public void undo(final Context context) {
        getNoteDocument().getCurrentPage(context).undo(inSpanScribbleMode());
    }

    public void redo(final Context context) {
        getNoteDocument().getCurrentPage(context).redo(inSpanScribbleMode());
    }
}
