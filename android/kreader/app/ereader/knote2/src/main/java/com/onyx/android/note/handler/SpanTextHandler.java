package com.onyx.android.note.handler;

import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.onyx.android.note.NoteDataBundle;
import com.onyx.android.note.action.PageSpanShapesAction;
import com.onyx.android.note.action.RenderToBitmapAction;
import com.onyx.android.note.action.SpannableAction;
import com.onyx.android.note.event.BuildSpanTextShapeEvent;
import com.onyx.android.note.event.SpanViewEnableEvent;
import com.onyx.android.note.event.SpanViewEvent;
import com.onyx.android.note.utils.DrawUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.PageSpanShapesRequest;
import com.onyx.android.sdk.note.request.SpannableRequest;
import com.onyx.android.sdk.note.widget.LinedEditText;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.os.Looper.getMainLooper;

/**
 * Created by lxm on 2018/2/5.
 */

public class SpanTextHandler extends BaseHandler {

    private List<Shape> dirtyStash = new ArrayList<>();

    private static String SPACE_TEXT = " ";
    private static final int SPAN_TIME_OUT = 1000;
    private static final int SPACE_WIDTH = 40;

    private Map<String, List<Shape>> pageSpanShapesMap;
    private Runnable spanRunnable;
    private long lastUpTime = -1;
    private Handler handler;
    private boolean buildingSpan = false;
    private EditText spanView;

    public SpanTextHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
        handler = new Handler(getMainLooper());
    }

    @Override
    public void onActivate() {
        super.onActivate();
        getNoteManager().post(new SpanViewEnableEvent(true));
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        getNoteManager().post(new SpanViewEnableEvent(false));
        spanView = null;
    }

    private void start() {
        loadPageSpanShapes();
    }

    private void loadPageSpanShapes() {
        new PageSpanShapesAction(getNoteManager(), getNoteManager().getNoteDocument().getCurrentPageUniqueId()).execute(new RxCallback<PageSpanShapesRequest>() {
            @Override
            public void onNext(@NonNull PageSpanShapesRequest request) {
                pageSpanShapesMap = request.getPageSpanShapesMap();
                spannableRequest(pageSpanShapesMap, null);
            }
        });
    }

    @Override
    public void onRawDrawingPointsReceived(TouchPointList pointList) {
        super.onRawDrawingPointsReceived(pointList);
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawingArgs();
        Shape shape = DrawUtils.createShape(drawingArgs, ShapeFactory.LayoutType.LINE.ordinal());
        shape.addPoints(pointList);
        dirtyStash.add(shape);
        buildSpan();
    }

    private List<Shape> detachStash() {
        final List<Shape> temp = new ArrayList<>();
        temp.addAll(dirtyStash);
        dirtyStash = new ArrayList<>();
        return temp;
    }

    private void buildSpan() {
        long curTime = System.currentTimeMillis();
        if (lastUpTime != -1 && (curTime - lastUpTime <= SPAN_TIME_OUT) && (spanRunnable != null)) {
            removeSpanRunnable();
        }
        lastUpTime = curTime;
        spanRunnable = buildSpanRunnable();
        handler.postDelayed(spanRunnable, SPAN_TIME_OUT);
    }

    private void removeSpanRunnable() {
        if (handler != null && spanRunnable != null) {
            handler.removeCallbacks(spanRunnable);
        }
    }

    private Runnable buildSpanRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                buildSpanImpl();
            }
        };
    }

    private void buildSpanImpl() {
        final List<Shape> newAddShapeList = detachStash();
        String groupId = ShapeUtils.generateUniqueId();
        for (Shape shape : newAddShapeList) {
            shape.setGroupId(groupId);
        }
        spannableRequest(pageSpanShapesMap, newAddShapeList);
    }

    private void spannableRequest(final Map<String, List<Shape>> pageSpanTextShapeMap, final List<Shape> newAddShapeList) {
        new SpannableAction(getNoteManager(), pageSpanTextShapeMap, newAddShapeList).execute(new RxCallback<SpannableRequest>() {
            @Override
            public void onNext(@NonNull SpannableRequest request) {
                if (newAddShapeList != null && newAddShapeList.size() > 0) {
                    pageSpanTextShapeMap.put(newAddShapeList.get(0).getGroupId(), newAddShapeList);
                }
                onFinishedSpan(request.getSpannableStringBuilder(), newAddShapeList, request.getLastShapeSpan());
            }
        });
    }

    private void onFinishedSpan(SpannableStringBuilder builder, final List<Shape> spanShapeList, final ShapeSpan lastShapeSpan) {
        if (builder == null) {
            setBuildingSpan(false);
            return;
        }
        spanView.setText(builder);
        spanView.setSelection(builder.length());
        spanView.requestFocus();
        if (lastShapeSpan != null) {
            lastShapeSpan.setCallback(new ShapeSpan.Callback() {
                @Override
                public void onFinishDrawShapes(List<Shape> shapes) {
                    afterDrawLineLayoutShapes(spanShapeList);
                }
            });
        }else {
            afterDrawLineLayoutShapes(spanShapeList);
        }
    }

    private void afterDrawLineLayoutShapes(final List<Shape> lineLayoutShapes) {
        if (checkShapesOutOfRange(lineLayoutShapes)) {
            lineLayoutShapes.clear();
            showOutOfRangeTips();
            setBuildingSpan(false);
            return;
        }

        new RenderToBitmapAction(getNoteManager()).setShapes(lineLayoutShapes).execute(new RxCallback() {
            @Override
            public void onNext(@NonNull Object o) {
                setBuildingSpan(false);
            }
        });
    }

    private boolean checkShapesOutOfRange(List<Shape> shapes) {
        if (shapes == null || shapes.size() == 0) {
            return false;
        }
        for (Shape shape : shapes) {
            TouchPointList pointList = shape.getPoints();
            if (!checkTouchPointList(pointList)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkTouchPointList(final TouchPointList touchPointList) {
        if (touchPointList == null || touchPointList.size() == 0) {
            return false;
        }
        List<TouchPoint> touchPoints = touchPointList.getPoints();
        for (TouchPoint touchPoint : touchPoints) {
            if (!checkTouchPoint(touchPoint)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkTouchPoint(final TouchPoint touchPoint) {
        Rect rect = new Rect();
        spanView.getGlobalVisibleRect(rect);
        return rect.contains((int) touchPoint.x, (int) touchPoint.y);
    }

    private void showOutOfRangeTips() {
        Toast.makeText(getNoteManager().getAppContext(), "", Toast.LENGTH_SHORT).show();
    }

    private void setBuildingSpan(boolean buildingSpan) {
        this.buildingSpan = buildingSpan;
    }

    @Subscribe
    public void onSpanViewEvent(SpanViewEvent event) {
        spanView = event.spanView;
        spanView.post(new Runnable() {
            @Override
            public void run() {
                updateLineLayoutArgs();
                start();
            }
        });
    }

    @Subscribe
    public void onBuildSpanTextShape(BuildSpanTextShapeEvent event) {
        buildTextShape(event.text, (int) spanView.getPaint().measureText(event.text));
    }

    private void buildTextShape(String text, int width) {
        Shape spaceShape = createTextShape(text);
        addShapePoints(spaceShape, width, getSpanTextFontHeight());

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spannableRequest(pageSpanShapesMap, newAddShapeList);
    }

    private Shape createTextShape(String text) {
        Shape shape = ShapeFactory.createShape(ShapeFactory.SHAPE_TEXT);
        NoteDrawingArgs drawingArgs = NoteDataBundle.getInstance().getDrawingArgs();
        shape.setStrokeWidth(drawingArgs.getStrokeWidth());
        shape.setColor(drawingArgs.getStrokeColor());
        shape.setLayoutType(ShapeFactory.LayoutType.LINE.ordinal());
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

    private int getSpanTextFontHeight() {
        float bottom = spanView.getPaint().getFontMetrics().bottom;
        float top = spanView.getPaint().getFontMetrics().top;
        return (int) Math.ceil(bottom - top - 2 * ShapeSpan.SHAPE_SPAN_MARGIN);
    }

    private void updateLineLayoutArgs() {
        int height = spanView.getHeight();
        int lineHeight = spanView.getLineHeight();
        int lineCount = spanView.getLineCount();
        int count = height / lineHeight;
        if (lineCount <= count) {
            lineCount = count;
        }
        Rect r = new Rect();
        spanView.getLineBounds(0, r);
        int baseLine = r.bottom;
        LineLayoutArgs args = LineLayoutArgs.create(baseLine, lineCount, lineHeight);
        args.backgroundType = NoteBackgroundType.LINE;
        getNoteManager().getRenderContext().setLineLayoutArgs(args);
    }
}
