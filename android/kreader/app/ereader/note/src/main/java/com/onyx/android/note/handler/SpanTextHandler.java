package com.onyx.android.note.handler;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableStringBuilder;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.note.NotePageShapesRequest;
import com.onyx.android.sdk.scribble.request.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.os.Looper.getMainLooper;

/**
 * Created by ming on 2016/12/14.
 */

public class SpanTextHandler {

    public interface Callback{
        void OnFinishedSpan(SpannableStringBuilder builder, List<Shape> spanShapeList, ShapeSpan lastShapeSpan);
    }

    public static String SPACE_TEXT = " ";
    private static final int SPAN_TIME_OUT = 1000;
    public static final int SPACE_WIDTH = 40;
    private Map<String, List<Shape>> subPageSpanTextShapeMap;
    private Runnable spanRunnable;
    private long lastUpTime = -1;
    private Handler handler;
    private Context context;
    private Callback callback;

    public SpanTextHandler(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        handler = new Handler(getMainLooper());
    }

    public void openSpanTextFunc() {
        if (subPageSpanTextShapeMap == null) {
            loadPageShapes();
        }
    }

    public void loadPageShapes() {
        final NotePageShapesRequest notePageShapesRequest = new NotePageShapesRequest(getNoteViewHelper().getNoteDocument().getCurrentPageUniqueId());
        getNoteViewHelper().submit(context, notePageShapesRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Shape> subPageAllShapeList = notePageShapesRequest.getPageShapes();
                subPageSpanTextShapeMap = ShapeFactory.getSubPageSpanShapeList(subPageAllShapeList);
                spannableRequest(subPageSpanTextShapeMap, null);
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
        handler.postDelayed(spanRunnable, SPAN_TIME_OUT);
    }

    public void removeSpanRunnable() {
        if (handler != null && spanRunnable != null) {
            handler.removeCallbacks(spanRunnable);
        }
    }

    private NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getInstance().getNoteViewHelper();
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
        if (getNoteViewHelper().isDrawing()) {
            return;
        }
        final List<Shape> newAddShapeList = getNoteViewHelper().detachStash();
        String groupId = ShapeUtils.generateUniqueId();
        for (Shape shape : newAddShapeList) {
            shape.setGroupId(groupId);
        }
        spannableRequest(subPageSpanTextShapeMap, newAddShapeList);
    }

    private void spannableRequest(final Map<String, List<Shape>> subPageSpanTextShapeMap, final List<Shape> newAddShapeList) {
        final SpannableRequest spannableRequest = new SpannableRequest(subPageSpanTextShapeMap, newAddShapeList);
        getNoteViewHelper().submit(context, spannableRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final SpannableStringBuilder builder = spannableRequest.getSpannableStringBuilder();
                if (newAddShapeList != null && newAddShapeList.size() > 0) {
                    subPageSpanTextShapeMap.put(newAddShapeList.get(0).getGroupId(), newAddShapeList);
                }
                if (callback != null) {
                    callback.OnFinishedSpan(builder, newAddShapeList, spannableRequest.getLastShapeSpan());
                }
            }
        });
    }

    public void clear() {
        if (subPageSpanTextShapeMap != null) {
            subPageSpanTextShapeMap.clear();
            subPageSpanTextShapeMap = null;
        }
        if (spanRunnable != null) {
            handler.removeCallbacks(spanRunnable);
        }
    }

    public String getLastGroupId() {
        String groupId = null;
        if (MapUtils.isEmpty(subPageSpanTextShapeMap)) {
            return groupId;
        }
        Iterator<String> iterator = subPageSpanTextShapeMap.keySet().iterator();
        while (iterator.hasNext()) {
            groupId = iterator.next();
        }
        return groupId;
    }

    public void buildTextShape(String text, int width, int height) {
        Shape spaceShape = createTextShape(text);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spannableRequest(subPageSpanTextShapeMap, newAddShapeList);
    }

    public void buildSpaceShape(final int width, int height) {
        Shape spaceShape = createTextShape(SPACE_TEXT);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spannableRequest(subPageSpanTextShapeMap, newAddShapeList);
    }

    private Shape createTextShape(String text) {
        Shape shape = ShapeFactory.createShape(ShapeFactory.SHAPE_TEXT);
        shape.setStrokeWidth(getNoteViewHelper().getNoteDocument().getStrokeWidth());
        shape.setColor(getNoteViewHelper().getNoteDocument().getStrokeColor());
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

}
