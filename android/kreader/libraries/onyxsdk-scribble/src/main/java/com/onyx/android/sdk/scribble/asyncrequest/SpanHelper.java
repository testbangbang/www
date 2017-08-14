package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.view.View;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.event.SpanFinishedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.SpanTextShowOutOfRangeEvent;
import com.onyx.android.sdk.scribble.asyncrequest.note.NotePageShapesRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeRemoveByGroupIdRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.scribble.view.LinedEditText;
import com.onyx.android.sdk.utils.StringUtils;

import org.apache.commons.collections4.MapUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lxm on 2017/8/14.
 */

public class SpanHelper {

    // use ascII code to define WHITESPACE.
    private static final String SPACE_TEXT = Character.toString((char) 32);
    private static final int SPAN_TIME_OUT = 1000;
    private static final int SPACE_WIDTH = 40;
    private Handler spanTextHandler;
    private Map<String, List<Shape>> mSubPageSpanTextShapeMap;
    private Runnable spanRunnable;
    private long lastUpTime = -1;
    private int mSpanTextFontHeight = 0;

    private LineLayoutArgs lineLayoutArgs;
    private boolean isLineLayoutMode = false;
    private Shape cursorShape = null;
    private NoteManager noteManager;

    public SpanHelper(NoteManager noteManager) {
        this.noteManager = noteManager;
        spanTextHandler = new Handler(Looper.getMainLooper());
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
        setLineLayoutArgs(args);
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
        LineLayoutArgs args = getLineLayoutArgs();
        int top = args.getLineTop(line);
        int bottom = args.getLineBottom(line);
        updateCursorShape(x, top + 1, x, bottom);
    }

    public void updateCursorShape(final int left, final int top, final int right, final int bottom) {
        TouchPointList touchPointList = new TouchPointList();
        TouchPoint downPoint = new TouchPoint();
        downPoint.offset(left, top);
        TouchPoint currentPoint = new TouchPoint();
        currentPoint.offset(right, bottom);
        touchPointList.add(downPoint);
        touchPointList.add(currentPoint);
        getCursorShape().addPoints(touchPointList);
    }

    public Shape getCursorShape() {
        if (cursorShape == null) {
            cursorShape = createNewShape(true, ShapeFactory.SHAPE_LINE);
        }
        return cursorShape;
    }

    private Shape createNewShape(boolean isSpanTextMode, int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(noteManager.getDocumentHelper().getStrokeWidth());
        shape.setColor(noteManager.getDocumentHelper().getStrokeColor());
        shape.setLayoutType(isSpanTextMode ? ShapeFactory.POSITION_LINE_LAYOUT : ShapeFactory.POSITION_FREE);
        return shape;
    }


    public boolean checkShapesOutOfRange(List<Shape> shapes) {
        if (shapes == null || shapes.size() == 0) {
            return false;
        }
        for (Shape shape : shapes) {
            TouchPointList pointList = shape.getPoints();
            if (!noteManager.getNoteViewHelper().checkTouchPointList(pointList)) {
                return true;
            }
        }
        return false;
    }

    public void deleteSpan(boolean resume) {
        String groupId = getLastGroupId();
        if (StringUtils.isNullOrEmpty(groupId)) {
            noteManager.sync(false, resume);
            return;
        }
        ShapeRemoveByGroupIdRequest changeRequest = new ShapeRemoveByGroupIdRequest(groupId, resume);
        noteManager.submitRequest(changeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loadPageShapes();
            }
        });
    }

    public void openSpanTextFunc() {
        if (mSubPageSpanTextShapeMap == null) {
            loadPageShapes();
        }
    }

    public void loadPageShapes() {
        NotePageShapesRequest notePageShapesRequest = new NotePageShapesRequest(noteManager.getDocumentHelper().getCurrentPageUniqueId());
        noteManager.submitRequest(notePageShapesRequest, new BaseCallback() {
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
        if (lastUpTime != -1 && (curTime - lastUpTime <= SPAN_TIME_OUT) && (spanRunnable != null)) {
            removeSpanRunnable();
        }
        lastUpTime = curTime;
        spanRunnable = buildSpanRunnable();
        spanTextHandler.postDelayed(spanRunnable, SPAN_TIME_OUT);
    }

    public void removeSpanRunnable() {
        if (spanTextHandler != null && spanRunnable != null) {
            spanTextHandler.removeCallbacks(spanRunnable);
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
        if (noteManager.isDrawing()) {
            return;
        }
        final List<Shape> newAddShapeList = noteManager.detachStash();
        String groupId = ShapeUtils.generateUniqueId();
        for (Shape shape : newAddShapeList) {
            shape.setGroupId(groupId);
        }
        spanShape(mSubPageSpanTextShapeMap, newAddShapeList);
    }

    private void spanShape(final Map<String, List<Shape>> subPageSpanTextShapeMap, final List<Shape> newAddShapeList) {
        SpannableRequest spannableRequest = new SpannableRequest(subPageSpanTextShapeMap, newAddShapeList);
        noteManager.submitRequest(spannableRequest, new BaseCallback() {
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
        if (spanRunnable != null) {
            spanTextHandler.removeCallbacks(spanRunnable);
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
        if (line == (getLineLayoutArgs().getLineCount() - 1)) {
            EventBus.getDefault().post(new SpanTextShowOutOfRangeEvent());
            noteManager.sync(true, true);
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
        shape.setStrokeWidth(noteManager.getDocumentHelper().getStrokeWidth());
        shape.setColor(noteManager.getDocumentHelper().getStrokeColor());
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

    public void setLineLayoutMode(boolean lineLayoutMode) {
        isLineLayoutMode = lineLayoutMode;
    }

    public boolean isLineLayoutMode() {
        return isLineLayoutMode;
    }

    public void drawLineLayoutBackground(final RenderContext renderContext, View view) {
        if (!isLineLayoutMode()) {
            return;
        }
        if (noteManager.getDocumentHelper().getLineLayoutBackground() == NoteBackgroundType.EMPTY) {
            return;
        }
        LineLayoutArgs args = getLineLayoutArgs();
        if (args == null) {
            return;
        }

        Rect viewRect = new Rect();
        view.getLocalVisibleRect(viewRect);
        int count = args.getLineCount();
        int lineHeight = args.getLineHeight();
        int baseline = args.getBaseLine();
        Paint paint = new Paint();
        for (int i = 0; i < count; i++) {
            renderContext.canvas.drawLine(viewRect.left, baseline + 1, viewRect.right, baseline + 1, paint);
            baseline += lineHeight;
        }
    }

    public void setLineLayoutArgs(LineLayoutArgs lineLayoutArgs) {
        this.lineLayoutArgs = lineLayoutArgs;
    }

    public LineLayoutArgs getLineLayoutArgs() {
        return lineLayoutArgs;
    }

}
