package com.onyx.edu.note.handler;

import android.os.Handler;
import android.text.SpannableStringBuilder;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.note.NotePageShapesRequest;
import com.onyx.android.sdk.scribble.request.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.scribble.NotePageShapeAction;
import com.onyx.edu.note.actions.scribble.SpannableAction;
import com.onyx.edu.note.data.ScribbleMainMenuID;

import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.os.Looper.getMainLooper;

/**
 * Created by solskjaer49 on 2017/5/27 12:33.
 */

public class SpanTextHandler extends BaseHandler {
    private static final String TAG = SpanTextHandler.class.getSimpleName();

    public interface Callback {
        void OnFinishedSpan(SpannableStringBuilder builder, List<Shape> spanShapeList, ShapeSpan lastShapeSpan);
    }

    public static String SPACE_TEXT = " ";
    private static final int SPAN_TIME_OUT = 1000;
    public static final int SPACE_WIDTH = 40;
    private Map<String, List<Shape>> subPageSpanTextShapeMap;
    private Runnable spanRunnable;
    private long lastUpTime = -1;
    private Handler handler;
    private Callback callback;

    public SpanTextHandler(NoteManager manager, Callback callback) {
        super(manager);
        this.callback = callback;
        handler = new Handler(getMainLooper());
    }

    public void openSpanTextFunc() {
        if (subPageSpanTextShapeMap == null) {
            loadPageShapes();
        }
    }

    public void loadPageShapes() {
        new NotePageShapeAction().execute(mNoteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Shape> subPageAllShapeList = ((NotePageShapesRequest) request).getPageShapes();
                subPageSpanTextShapeMap = ShapeFactory.getSubPageSpanShapeList(subPageAllShapeList);
                spanShape(subPageSpanTextShapeMap, null);
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

    private Runnable buildSpanRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                buildSpanImpl();
            }
        };
    }

    private void buildSpanImpl() {
        if (mNoteManager.isDrawing()) {
            return;
        }
        final List<Shape> newAddShapeList = mNoteManager.detachStash();
        String groupId = ShapeUtils.generateUniqueId();
        for (Shape shape : newAddShapeList) {
            shape.setGroupId(groupId);
        }
        spanShape(subPageSpanTextShapeMap, newAddShapeList);
    }

    private void spanShape(final Map<String, List<Shape>> subPageSpanTextShapeMap, final List<Shape> newAddShapeList) {
        new SpannableAction().execute(mNoteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SpannableRequest req = (SpannableRequest) request;
                final SpannableStringBuilder builder = req.getSpannableStringBuilder();
                if (newAddShapeList != null && newAddShapeList.size() > 0) {
                    subPageSpanTextShapeMap.put(newAddShapeList.get(0).getGroupId(), newAddShapeList);
                }
                if (callback != null) {
                    callback.OnFinishedSpan(builder, newAddShapeList, req.getLastShapeSpan());
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
            return null;
        }
        for (String s : subPageSpanTextShapeMap.keySet()) {
            groupId = s;
        }
        return groupId;
    }

    public void buildTextShape(String text, int width, int height) {
        Shape spaceShape = createTextShape(text);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spanShape(subPageSpanTextShapeMap, newAddShapeList);
    }

    public void buildSpaceShape(final int width, int height) {
        Shape spaceShape = createTextShape(SPACE_TEXT);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spanShape(subPageSpanTextShapeMap, newAddShapeList);
    }

    private Shape createTextShape(String text) {
        Shape shape = ShapeFactory.createShape(ShapeFactory.SHAPE_TEXT);
        shape.setStrokeWidth(mNoteManager.getNoteDocument().getStrokeWidth());
        shape.setColor(mNoteManager.getNoteDocument().getStrokeColor());
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

    @Override
    public void onDeactivate() {
        clear();
    }

    @Override
    public void buildMainMenuFunctionList() {
        mMainMenuFunctionIDList = new ArrayList<>();
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.KEYBOARD);
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.ENTER);
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.DELETE);
        mMainMenuFunctionIDList.add(ScribbleMainMenuID.SPACE);
    }
}
