package com.onyx.edu.note.handler;

import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.SparseArray;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BuildLineBreakShapeEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BuildTextShapeEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.DeleteSpanEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.LoadSpanPageShapesEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.SpanFinishedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.SpanTextShowOutOfRangeEvent;
import com.onyx.android.sdk.scribble.asyncrequest.note.NotePageShapesRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeRemoveByGroupIdRequest;
import com.onyx.android.sdk.scribble.asyncrequest.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.scribble.utils.SpanUtils;
import com.onyx.android.sdk.scribble.view.LinedEditText;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.note.actions.scribble.DocumentAddNewPageAction;
import com.onyx.edu.note.actions.scribble.DocumentDeletePageAction;
import com.onyx.edu.note.actions.scribble.DocumentSaveAction;
import com.onyx.edu.note.actions.scribble.GotoNextPageAction;
import com.onyx.edu.note.actions.scribble.GotoPrevPageAction;
import com.onyx.edu.note.actions.scribble.NoteLineLayoutBackgroundChangeAction;
import com.onyx.edu.note.actions.scribble.RedoAction;
import com.onyx.edu.note.actions.scribble.UndoAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.edu.note.data.ScribbleSubMenuID;
import com.onyx.edu.note.data.ScribbleToolBarMenuID;
import com.onyx.edu.note.scribble.event.ChangeScribbleModeEvent;
import com.onyx.edu.note.scribble.event.RequestInfoUpdateEvent;
import com.onyx.edu.note.scribble.event.ShowInputKeyBoardEvent;
import com.onyx.edu.note.scribble.event.ShowSubMenuEvent;
import com.onyx.edu.note.scribble.event.SpanLineBreakerEvent;

import org.apache.commons.collections4.MapUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_EMPTY;
import static com.onyx.edu.note.data.ScribbleSubMenuID.Background.BG_LINE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.CIRCLE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.LINE_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.RECT_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE;
import static com.onyx.edu.note.data.ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE;

/**
 * Created by solskjaer49 on 2017/5/27 12:33.
 */

public class SpanTextHandler extends BaseHandler {
    private static final String TAG = SpanTextHandler.class.getSimpleName();

    // use ascII code to define WHITESPACE.
    private static final String SPACE_TEXT = Character.toString((char) 32);
    private static final int SPAN_TIME_OUT = 1000;
    private static final int SPACE_WIDTH = 40;
    private Handler spanTextHandler;
    private Map<String, List<Shape>> subPageSpanTextShapeMap;
    private Runnable spanRunnable;
    private long lastUpTime = -1;

    private class SpanBaseCallBack extends BaseCallback {
        public SpanBaseCallBack(boolean reloadPageShape) {
            this.reloadPageShape = reloadPageShape;
        }

        private boolean reloadPageShape = false;

        @Override
        public void done(BaseRequest request, Throwable e) {
            noteManager.post(new RequestInfoUpdateEvent(noteManager.getShapeDataInfo(), request, e));
            if (reloadPageShape) {
                loadPageShapes();
            }
        }
    }

    public SpanTextHandler(NoteManager manager) {
        super(manager);
        spanTextHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onActivate() {
        super.onActivate();
        noteManager.registerEventBus(this);
        openSpanTextFunc();
    }

    @Override
    public void onDeactivate() {
        noteManager.unregisterEventBus(this);
        exitSpanTextFunc();
    }

    @Override
    public void buildFunctionBarMenuFunctionList() {
        functionBarMenuIDList = new ArrayList<>();
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.PEN_STYLE);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.BG);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.DELETE);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.SPACE);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.ENTER);
        functionBarMenuIDList.add(ScribbleFunctionBarMenuID.KEYBOARD);
    }

    @Override
    protected void buildToolBarMenuFunctionList() {
        toolBarMenuIDList = new ArrayList<>();
        toolBarMenuIDList.add(ScribbleToolBarMenuID.SWITCH_TO_NORMAL_SCRIBBLE_MODE);
        toolBarMenuIDList.add(ScribbleToolBarMenuID.UNDO);
        toolBarMenuIDList.add(ScribbleToolBarMenuID.SAVE);
        toolBarMenuIDList.add(ScribbleToolBarMenuID.REDO);
    }

    @Override
    protected void buildFunctionBarMenuSubMenuIDListSparseArray() {
        functionBarSubMenuIDMap = new SparseArray<>();
        functionBarSubMenuIDMap.put(ScribbleFunctionBarMenuID.PEN_STYLE, buildSubMenuPenStyleIDList());
        functionBarSubMenuIDMap.put(ScribbleFunctionBarMenuID.BG, buildSubMenuBGIDList());
    }

    @Override
    public void handleFunctionBarMenuFunction(int functionBarMenuID) {
        Log.e(TAG, "handleFunctionBarMenuFunction: " + functionBarMenuID);
        switch (functionBarMenuID) {
            case ScribbleFunctionBarMenuID.DELETE:
                deleteSpan(true);
                break;
            case ScribbleFunctionBarMenuID.SPACE:
                buildSpaceShape();
                break;
            case ScribbleFunctionBarMenuID.ENTER:
                noteManager.post(new SpanLineBreakerEvent());
                break;
            case ScribbleFunctionBarMenuID.KEYBOARD:
                noteManager.post(new ShowInputKeyBoardEvent());
                break;
            case ScribbleFunctionBarMenuID.ADD_PAGE:
                addPage();
                break;
            case ScribbleFunctionBarMenuID.DELETE_PAGE:
                deletePage();
                break;
            case ScribbleFunctionBarMenuID.NEXT_PAGE:
                nextPage();
                break;
            case ScribbleFunctionBarMenuID.PREV_PAGE:
                prevPage();
                break;
            default:
                noteManager.post(new ShowSubMenuEvent(functionBarMenuID));
                break;
        }
    }

    @Override
    public void handleSubMenuFunction(int subMenuID) {
        Log.e(TAG, "handleSubMenuFunction: " + subMenuID);
        if (ScribbleSubMenuID.isBackgroundGroup(subMenuID)) {
            onBackgroundChanged(subMenuID);
        } else if (ScribbleSubMenuID.isPenStyleGroup(subMenuID)) {
            onShapeChanged(subMenuID);
        }
    }

    @Override
    public void handleToolBarMenuFunction(String uniqueID, String title, int toolBarMenuID) {
        switch (toolBarMenuID) {
            case ScribbleToolBarMenuID.SWITCH_TO_NORMAL_SCRIBBLE_MODE:
                noteManager.post(new ChangeScribbleModeEvent(ScribbleMode.MODE_NORMAL_SCRIBBLE));
                break;
            case ScribbleToolBarMenuID.SAVE:
                saveDocument(uniqueID, title, false, null);
                break;
            case ScribbleToolBarMenuID.UNDO:
                unDo();
                break;
            case ScribbleToolBarMenuID.REDO:
                reDo();
                break;
            case ScribbleToolBarMenuID.SETTING:
                break;
        }
    }

    @Override
    public void saveDocument(String uniqueID, String title, boolean closeAfterSave, BaseCallback callback) {
        DocumentSaveAction documentSaveAction = new DocumentSaveAction(uniqueID,
                title, closeAfterSave);
        documentSaveAction.execute(noteManager, callback);
    }

    @Override
    public void prevPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoPrevPageAction prevPageAction = new GotoPrevPageAction();
                prevPageAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    @Override
    public void nextPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GotoNextPageAction nextPageAction = new GotoNextPageAction();
                nextPageAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    @Override
    public void addPage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentAddNewPageAction addNewPageAction = new DocumentAddNewPageAction();
                addNewPageAction.execute(noteManager, new SpanBaseCallBack(false));
            }
        });
    }

    @Override
    public void deletePage() {
        noteManager.syncWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DocumentDeletePageAction deletePageAction = new DocumentDeletePageAction();
                deletePageAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private List<Integer> buildSubMenuPenStyleIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(NORMAL_PEN_STYLE);
        resultList.add(BRUSH_PEN_STYLE);
        resultList.add(LINE_STYLE);
        resultList.add(TRIANGLE_STYLE);
        resultList.add(CIRCLE_STYLE);
        resultList.add(RECT_STYLE);
        resultList.add(TRIANGLE_45_STYLE);
        resultList.add(TRIANGLE_60_STYLE);
        resultList.add(TRIANGLE_90_STYLE);
        return resultList;
    }

    private  List<Integer> buildSubMenuBGIDList() {
        List<Integer> resultList = new ArrayList<>();
        resultList.add(BG_EMPTY);
        resultList.add(BG_LINE);
        return resultList;
    }

    private void reDo() {
        noteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                RedoAction reDoAction = new RedoAction();
                reDoAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private void unDo() {
        noteManager.syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UndoAction unDoAction = new UndoAction();
                unDoAction.execute(noteManager, new SpanBaseCallBack(true));
            }
        });
    }

    private void onBackgroundChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int bgType = ScribbleSubMenuID.bgFromMenuID(subMenuID);
        NoteLineLayoutBackgroundChangeAction changeBGAction = new NoteLineLayoutBackgroundChangeAction(bgType, true);
        changeBGAction.execute(noteManager, new SpanBaseCallBack(false));
    }

    private void onShapeChanged(@ScribbleSubMenuID.ScribbleSubMenuIDDef int subMenuID) {
        int shapeType = ScribbleSubMenuID.shapeTypeFromMenuID(subMenuID);
        noteManager.getShapeDataInfo().setCurrentShapeType(shapeType);
        noteManager.sync(true, ShapeFactory.createShape(shapeType).supportDFB());
    }

    @Override
    public void onRawTouchPointListReceived() {
        buildSpan();
    }

    @Override
    public void onDrawingTouchUp() {
        buildSpan();
    }

    @Subscribe
    public void buildLineBreakShapeEvent(BuildLineBreakShapeEvent event) {
        buildLineBreakShape(event.getSpanTextView());
    }

    @Subscribe
    public void buildTextShapeEvent(BuildTextShapeEvent event) {
        buildTextShape(event.getText(), event.getSpanTextView());
    }

    @Subscribe
    public void onLoadSpanPageShapesEvent(LoadSpanPageShapesEvent event) {
        loadPageShapes();
    }

    @Subscribe
    public void onDeleteSpanEvent(DeleteSpanEvent event) {
        deleteSpan(event.isResume());
    }

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent event) {
        Debug.e(getClass(), "onBeginRawDataEvent");
        removeSpanRunnable();
    }

    private void deleteSpan(boolean resume) {
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

    private void openSpanTextFunc() {
        if (subPageSpanTextShapeMap == null) {
            loadPageShapes();
        }
    }

    private void loadPageShapes() {
        NotePageShapesRequest notePageShapesRequest = new NotePageShapesRequest(noteManager.getDocumentHelper().getCurrentPageUniqueId());
        noteManager.submitRequest(notePageShapesRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                List<Shape> subPageAllShapeList = ((NotePageShapesRequest) request).getPageShapes();
                subPageSpanTextShapeMap = ShapeFactory.getSubPageSpanShapeList(subPageAllShapeList);
                spanShape(subPageSpanTextShapeMap, null);
            }
        });
    }

    private void buildSpan() {
        long curTime = System.currentTimeMillis();
        if (lastUpTime != -1 && (curTime - lastUpTime <= SPAN_TIME_OUT) && (spanRunnable != null)) {
            removeSpanRunnable();
        }
        lastUpTime = curTime;
        spanRunnable = buildSpanRunnable();
        spanTextHandler.postDelayed(spanRunnable, SPAN_TIME_OUT);
    }

    private void removeSpanRunnable() {
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
        spanShape(subPageSpanTextShapeMap, newAddShapeList);
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
                noteManager.post(new SpanFinishedEvent(builder, newAddShapeList, req.getLastShapeSpan()));
            }
        });
    }

    private void exitSpanTextFunc() {
        if (subPageSpanTextShapeMap != null) {
            subPageSpanTextShapeMap.clear();
            subPageSpanTextShapeMap = null;
        }
        if (spanRunnable != null) {
            spanTextHandler.removeCallbacks(spanRunnable);
        }
    }

    private String getLastGroupId() {
        String groupId = null;
        if (MapUtils.isEmpty(subPageSpanTextShapeMap)) {
            return null;
        }
        for (String s : subPageSpanTextShapeMap.keySet()) {
            groupId = s;
        }
        return groupId;
    }

    private void buildTextShape(String text, LinedEditText spanTextView) {
        int width = (int) spanTextView.getPaint().measureText(text);
        buildTextShape(text, width, noteManager.getLineLayoutArgs().getSpanTextFontHeight());
    }

    private void buildTextShape(String text, int width, int height) {
        Shape spaceShape = createTextShape(text);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spanShape(subPageSpanTextShapeMap, newAddShapeList);
    }

    private void buildSpaceShape(final int width, int height) {
        Shape spaceShape = createTextShape(SPACE_TEXT);
        addShapePoints(spaceShape, width, height);

        List<Shape> newAddShapeList = new ArrayList<>();
        newAddShapeList.add(spaceShape);
        spanShape(subPageSpanTextShapeMap, newAddShapeList);
    }

    private void buildSpaceShape() {
        buildSpaceShape(SPACE_WIDTH, noteManager.getLineLayoutArgs().getSpanTextFontHeight());
    }

    private void buildLineBreakShape(LinedEditText spanTextView) {
        float spaceWidth = (int) spanTextView.getPaint().measureText(SPACE_TEXT);
        int pos = spanTextView.getSelectionStart();
        Layout layout = spanTextView.getLayout();
        int line = layout.getLineForOffset(pos);
        if (line == (noteManager.getLineLayoutArgs().getLineCount() - 1)) {
            noteManager.post(new SpanTextShowOutOfRangeEvent());
            noteManager.sync(true, true);
            return;
        }
        int width = spanTextView.getMeasuredWidth();
        float x = layout.getPrimaryHorizontal(pos) - spaceWidth;
        x = x >= width ? 0 : x;
        buildSpaceShape((int) Math.ceil(spanTextView.getMeasuredWidth() - x) - 2 * ShapeSpan.SHAPE_SPAN_MARGIN,
                noteManager.getLineLayoutArgs().getSpanTextFontHeight());
    }

    private Shape createTextShape(String text) {
        Shape shape = ShapeFactory.createShape(ShapeFactory.SHAPE_TEXT);
        shape.setStrokeWidth(noteManager.getShapeDataInfo().getStrokeWidth());
        shape.setColor(noteManager.getShapeDataInfo().getStrokeColor());
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
