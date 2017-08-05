package com.onyx.edu.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginShapeSelectEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchDownEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchMoveEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchUpEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EraseTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ShapeSelectingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.note.NotePageShapesRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.SelectShapeByPointListRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeSelectionRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.utils.NoteViewUtil;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.note.actions.scribble.DocumentFlushAction;
import com.onyx.edu.note.actions.scribble.DrawPageAction;
import com.onyx.edu.note.actions.scribble.NotePageShapeAction;
import com.onyx.edu.note.actions.scribble.RemoveByGroupIdAction;
import com.onyx.edu.note.actions.scribble.RemoveByPointListAction;
import com.onyx.edu.note.actions.scribble.SelectShapeByPointListAction;
import com.onyx.edu.note.actions.scribble.SpannableAction;
import com.onyx.edu.note.data.ScribbleMode;
import com.onyx.edu.note.scribble.event.RawDataReceivedEvent;
import com.onyx.edu.note.scribble.event.SpanFinishedEvent;
import com.onyx.edu.note.scribble.event.SpanTextShowOutOfRangeEvent;
import com.onyx.edu.note.ui.view.LinedEditText;

import org.apache.commons.collections4.MapUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by solskjaer49 on 2017/2/11 18:44.
 */

public class NoteManager {
    private static final String TAG = NoteManager.class.getSimpleName();


    private AsyncNoteViewHelper noteViewHelper;
    private static NoteManager instance;
    private ShapeDataInfo shapeDataInfo = new ShapeDataInfo();
    private Context appContext;
    private NoteViewHelper.InputCallback mInputCallback;

    private TouchPoint mErasePoint = null;

    //TODO:Span Function Relative
    // use ascII code to define WHITESPACE.
    private static final String SPACE_TEXT = Character.toString((char) 32);
    private static final int SPAN_TIME_OUT = 1000;
    private static final int SPACE_WIDTH = 40;
    private Handler mSpanTextHandler;
    private Map<String, List<Shape>> mSubPageSpanTextShapeMap;
    private Runnable mSpanRunnable;
    private long lastUpTime = -1;
    private int mSpanTextFontHeight = 0;

    private @ScribbleMode.ScribbleModeDef
    int mCurrentScribbleMode = ScribbleMode.MODE_NORMAL_SCRIBBLE;


    private TouchPoint mShapeSelectStartPoint = null;
    private TouchPoint mShapeSelectPoint = null;

    public int getCurrentScribbleMode() {
        return mCurrentScribbleMode;
    }

    public void setCurrentScribbleMode(int currentScribbleMode) {
        mCurrentScribbleMode = currentScribbleMode;
        setLineLayoutMode(mCurrentScribbleMode == ScribbleMode.MODE_SPAN_SCRIBBLE);
    }

    private NoteManager(Context context) {
        appContext = context;
        if (noteViewHelper == null) {
            noteViewHelper = new AsyncNoteViewHelper();
        }
        mSpanTextHandler = new Handler(Looper.getMainLooper());
    }

    static public NoteManager sharedInstance(Context context) {
        if (instance == null) {
            instance = new NoteManager(context);
        }
        return instance;
    }

    private Runnable generateRunnable(final AsyncBaseNoteRequest request) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    request.beforeExecute(noteViewHelper);
                    request.execute(noteViewHelper);
                } catch (Throwable tr) {
                    request.setException(tr);
                } finally {
                    request.postExecute(noteViewHelper);
                    getRequestManager().dumpWakelocks();
                    getRequestManager().removeRequest(request);
                }
            }
        };
    }

    private void beforeSubmit(AsyncBaseNoteRequest request) {
        final Rect rect = noteViewHelper.getViewportSize();
        if (rect != null) {
            request.setViewportSize(rect);
        }
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
        if (isLineLayoutMode()) {
            stash.clear();
        }
        final DocumentFlushAction action = new DocumentFlushAction(stash,
                render,
                resume,
                shapeDataInfo.getDrawingArgs());
        action.execute(this, callback);
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
        String groupId = getLastGroupId();
        if (StringUtils.isNullOrEmpty(groupId)) {
            sync(false, resume);
            return;
        }
        RemoveByGroupIdAction removeByGroupIdAction = new
                RemoveByGroupIdAction(groupId, resume);
        removeByGroupIdAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loadPageShapes();
            }
        });
    }

    public void updateLineLayoutArgs(LinedEditText spanTextView) {
        int height = spanTextView.getHeight();
        int lineHeight = spanTextView.getLineHeight();
        int lineCount = spanTextView.getLineCount();
        int count = height / lineHeight;
        if (lineCount <= count) {
            lineCount = count;
        }
        Rect r = new Rect();
        spanTextView.getLineBounds(0, r);
        int baseLine = r.bottom;
        LineLayoutArgs args = LineLayoutArgs.create(baseLine, lineCount, lineHeight);
        noteViewHelper.setLineLayoutArgs(args);
        mSpanTextFontHeight = calculateSpanTextFontHeight(spanTextView);
    }

    private int calculateSpanTextFontHeight(LinedEditText spanTextView) {
        float bottom = spanTextView.getPaint().getFontMetrics().bottom;
        float top = spanTextView.getPaint().getFontMetrics().top;
        return (int) Math.ceil(bottom - top - 2 * ShapeSpan.SHAPE_SPAN_MARGIN);
    }

    public void updateLineLayoutCursor(LinedEditText spanTextView) {
        int pos = spanTextView.getSelectionStart();
        Layout layout = spanTextView.getLayout();
        int line = layout.getLineForOffset(pos);
        int x = (int) layout.getPrimaryHorizontal(pos);
        LineLayoutArgs args = noteViewHelper.getLineLayoutArgs();
        int top = args.getLineTop(line);
        int bottom = args.getLineBottom(line);
        noteViewHelper.updateCursorShape(x, top + 1, x, bottom);
    }

    public boolean checkShapesOutOfRange(List<Shape> shapes) {
        if (shapes == null || shapes.size() == 0) {
            return false;
        }
        for (Shape shape : shapes) {
            TouchPointList pointList = shape.getPoints();
            if (!noteViewHelper.checkTouchPointList(pointList)) {
                return true;
            }
        }
        return false;
    }

    public void openSpanTextFunc() {
        if (mSubPageSpanTextShapeMap == null) {
            loadPageShapes();
        }
    }

    public void loadPageShapes() {
        new NotePageShapeAction().execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Shape> subPageAllShapeList = ((NotePageShapesRequest) request).getPageShapes();
                mSubPageSpanTextShapeMap = ShapeFactory.getSubPageSpanShapeList(subPageAllShapeList);
                spanShape(mSubPageSpanTextShapeMap, null);
            }
        });
    }

    public void buildSpan() {
        long curTime = System.currentTimeMillis();
        if (lastUpTime != -1 && (curTime - lastUpTime <= SPAN_TIME_OUT) && (mSpanRunnable != null)) {
            removeSpanRunnable();
        }
        lastUpTime = curTime;
        mSpanRunnable = buildSpanRunnable();
        mSpanTextHandler.postDelayed(mSpanRunnable, SPAN_TIME_OUT);
    }

    public void removeSpanRunnable() {
        if (mSpanTextHandler != null && mSpanRunnable != null) {
            mSpanTextHandler.removeCallbacks(mSpanRunnable);
        }
    }

    private Runnable buildSpanRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                buildSpanImpl();
            }
        };
    }

    private void buildSpanImpl() {
        if (isDrawing()) {
            return;
        }
        final List<Shape> newAddShapeList = detachStash();
        String groupId = ShapeUtils.generateUniqueId();
        for (Shape shape : newAddShapeList) {
            shape.setGroupId(groupId);
        }
        spanShape(mSubPageSpanTextShapeMap, newAddShapeList);
    }

    private void spanShape(final Map<String, List<Shape>> subPageSpanTextShapeMap, final List<Shape> newAddShapeList) {
        new SpannableAction(subPageSpanTextShapeMap, newAddShapeList).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SpannableRequest req = (SpannableRequest) request;
                final SpannableStringBuilder builder = req.getSpannableStringBuilder();
                if (newAddShapeList != null && newAddShapeList.size() > 0) {
                    subPageSpanTextShapeMap.put(newAddShapeList.get(0).getGroupId(), newAddShapeList);
                }
                EventBus.getDefault().post(new SpanFinishedEvent(builder, newAddShapeList, req.getLastShapeSpan()));
            }
        });
    }

    public void exitSpanTextFunc() {
        if (mSubPageSpanTextShapeMap != null) {
            mSubPageSpanTextShapeMap.clear();
            mSubPageSpanTextShapeMap = null;
        }
        if (mSpanRunnable != null) {
            mSpanTextHandler.removeCallbacks(mSpanRunnable);
        }
    }

    private String getLastGroupId() {
        String groupId = null;
        if (MapUtils.isEmpty(mSubPageSpanTextShapeMap)) {
            return null;
        }
        for (String s : mSubPageSpanTextShapeMap.keySet()) {
            groupId = s;
        }
        return groupId;
    }

    public void buildTextShape(String text, LinedEditText spanTextView) {
        int width = (int) spanTextView.getPaint().measureText(text);
        buildTextShape(text, width, mSpanTextFontHeight);
    }

    private void buildTextShape(String text, int width, int height) {
        Shape spaceShape = createTextShape(text);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spanShape(mSubPageSpanTextShapeMap, newAddShapeList);
    }

    public void buildSpaceShape(final int width, int height) {
        Shape spaceShape = createTextShape(SPACE_TEXT);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spanShape(mSubPageSpanTextShapeMap, newAddShapeList);
    }

    public void buildSpaceShape() {
        buildSpaceShape(SPACE_WIDTH, mSpanTextFontHeight);
    }

    public void buildLineBreakShape(LinedEditText spanTextView) {
        float spaceWidth = (int) spanTextView.getPaint().measureText(SPACE_TEXT);
        int pos = spanTextView.getSelectionStart();
        Layout layout = spanTextView.getLayout();
        int line = layout.getLineForOffset(pos);
        if (line == (noteViewHelper.getLineLayoutArgs().getLineCount() - 1)) {
            EventBus.getDefault().post(new SpanTextShowOutOfRangeEvent());
            sync(true, true);
            return;
        }
        int width = spanTextView.getMeasuredWidth();
        float x = layout.getPrimaryHorizontal(pos) - spaceWidth;
        x = x >= width ? 0 : x;
        buildSpaceShape((int) Math.ceil(spanTextView.getMeasuredWidth() - x) - 2 * ShapeSpan.SHAPE_SPAN_MARGIN,
                mSpanTextFontHeight);
    }

    private Shape createTextShape(String text) {
        Shape shape = ShapeFactory.createShape(ShapeFactory.SHAPE_TEXT);
        shape.setStrokeWidth(getNoteDocument().getStrokeWidth());
        shape.setColor(getNoteDocument().getStrokeColor());
        shape.setLayoutType(ShapeFactory.POSITION_LINE_LAYOUT);
        shape.setGroupId(ShapeUtils.generateUniqueId());
        shape.getShapeExtraAttributes().setTextContent(text);
        return shape;
    }

    private void addShapePoints(final Shape shape, final int width, final int height) {
        TouchPointList touchPointList = new TouchPointList();
        TouchPoint downPoint = new TouchPoint();
        downPoint.offset(0, 0);
        TouchPoint currentPoint = new TouchPoint();
        currentPoint.offset(width, height);
        touchPointList.add(downPoint);
        touchPointList.add(currentPoint);
        shape.addPoints(touchPointList);
    }

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent event) {
        Log.e(TAG, "onBeginRawDataEvent");
        removeSpanRunnable();
    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent event) {
        Log.e(TAG, "onRawTouchPointListReceivedEvent");
        if (isLineLayoutMode()) {
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
        onErasing(event.getMotionEvent());
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
        if (isLineLayoutMode()) {
            buildSpan();
        }
    }

    @Subscribe
    public void onBeginShapeSelectEvent(BeginShapeSelectEvent event) {
        Log.e(TAG, "onBeginShapeSelectEvent: ");
        onBeginShapeSelecting(event.getMotionEvent());
    }

    @Subscribe
    public void onShapeSelectingEvent(ShapeSelectingEvent event) {
        Log.e(TAG, "onShapeSelectingEvent: ");
        onShapeSelecting(event.getMotionEvent());
    }

    @Subscribe
    public void onShapeSelectTouchPointListReceived(ShapeSelectTouchPointListReceivedEvent event) {
        onFinishShapeSelecting(event.getTouchPointList());
    }

    private void drawErasingIndicator(final SurfaceView view, final Paint paint) {
        if (mErasePoint == null || mErasePoint.getX() <= 0 || mErasePoint.getY() <= 0) {
            return;
        }

        float x = mErasePoint.getX();
        float y = mErasePoint.getY();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        Canvas canvas = view.getHolder().lockCanvas();
        canvas.drawCircle(x, y, shapeDataInfo.getEraserRadius(), paint);
        view.getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawShapeSelectIndicator(final SurfaceView view, final Paint paint) {
        if (mShapeSelectStartPoint == null || mShapeSelectStartPoint.getX() <= 0 || mShapeSelectStartPoint.getY() <= 0) {
            return;
        }

        RectF selectRect = new RectF(mShapeSelectStartPoint.getX(), mShapeSelectStartPoint.getY(),
                mShapeSelectPoint.getX(), mShapeSelectPoint.getY());
        Log.d(TAG, "selectRect:" + selectRect);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        paint.setPathEffect(noteViewHelper.selectedDashPathEffect);
        Canvas canvas = view.getHolder().lockCanvas();
        canvas.drawRect(selectRect, paint);
//        canvas.drawPoint(mShapeSelectStartPoint.getX(), mShapeSelectStartPoint.getY(),paint);
//        canvas.drawPoint(mShapeSelectPoint.getX(), mShapeSelectPoint.getY(),paint);
        view.getHolder().unlockCanvasAndPost(canvas);
    }

    //TODO:avoid direct obtain note view helper,because we plan to remove this class.

    public void reset(View view) {
        noteViewHelper.reset(view);
    }

    public SurfaceView getView() {
        return noteViewHelper.getView();
    }

    public void setView(Context context, SurfaceView surfaceView) {
        EventBus.getDefault().register(this);
        noteViewHelper.setView(context, surfaceView);
    }

    protected void onBeginErasing() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mErasePoint = new TouchPoint();
            }
        });
    }

    protected void onErasing(final MotionEvent touchPoint) {
        if (mErasePoint == null) {
            return;
        }
        mErasePoint.x = touchPoint.getX();
        mErasePoint.y = touchPoint.getY();
        //TODO:temp disable indicator.
//        drawErasingIndicator(getView(), new Paint());
    }

    protected void onFinishErasing(TouchPointList pointList) {
        mErasePoint = null;
        new RemoveByPointListAction(pointList).execute(this, null);
    }

    protected void onBeginShapeSelecting(final MotionEvent event) {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mShapeSelectStartPoint = new TouchPoint(event);
                mShapeSelectPoint = new TouchPoint(event);
                Log.e(TAG, "mShapeSelectStartPoint.getX():" + mShapeSelectStartPoint.getX());
                Log.e(TAG, "mShapeSelectStartPoint.getY():" + mShapeSelectStartPoint.getY());
            }
        });
    }

    protected void onShapeSelecting(final MotionEvent touchPoint) {
        if (mShapeSelectStartPoint == null) {
            return;
        }
        mShapeSelectPoint.x = touchPoint.getX();
        mShapeSelectPoint.y = touchPoint.getY();
        final ShapeSelectionRequest shapeSelectionRequest = new ShapeSelectionRequest(mShapeSelectStartPoint, mShapeSelectPoint);
        submitRequest(shapeSelectionRequest, null);
    }

    protected void onFinishShapeSelecting(TouchPointList pointList) {
        mShapeSelectStartPoint = null;
        mShapeSelectPoint = null;
        new SelectShapeByPointListAction(pointList).execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SelectShapeByPointListRequest req = (SelectShapeByPointListRequest) request;
                for (Shape shape : req.getSelectResultList()) {
                    Log.e(TAG, "shape:" + shape);
                }
            }
        });
    }

    private void drawCurrentPage() {
        new DrawPageAction().execute(this, null);
    }

    public void quit() {
        EventBus.getDefault().unregister(this);
        noteViewHelper.quit();
    }

    public void setLineLayoutMode(boolean isLineLayoutMode) {
        noteViewHelper.setLineLayoutMode(isLineLayoutMode);
    }

    public boolean isLineLayoutMode() {
        return noteViewHelper.isLineLayoutMode();
    }

    public List<Shape> detachStash() {
        return noteViewHelper.detachStash();
    }

    public void clearSurfaceView(SurfaceView surfaceView) {
        NoteViewUtil.clearSurfaceView(surfaceView);
    }

    public void setDrawing(boolean drawing) {
        noteViewHelper.setDrawing(drawing);
    }

    public boolean isDrawing() {
        return noteViewHelper.isDrawing();
    }

    public boolean inUserErasing() {
        return noteViewHelper.inUserErasing();
    }

    public NoteDocument getNoteDocument() {
        return noteViewHelper.getNoteDocument();
    }

    public void pauseDrawing() {
        noteViewHelper.pauseDrawing();
    }

    public void resumeDrawing() {
        noteViewHelper.resumeDrawing();
    }

    public Bitmap getRenderBitmap() {
        return noteViewHelper.getRenderBitmap();
    }

    public List<Shape> getDirtyShape() {
        return noteViewHelper.getDirtyStash();
    }

    public void clearPageUndoRedo(Context context) {
        noteViewHelper.clearPageUndoRedo(context);
    }

    public RequestManager getRequestManager() {
        return noteViewHelper.getRequestManager();
    }
}
