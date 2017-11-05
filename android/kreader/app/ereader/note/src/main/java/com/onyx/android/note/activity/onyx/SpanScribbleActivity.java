package com.onyx.android.note.activity.onyx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.android.note.actions.scribble.DocumentDiscardAction;
import com.onyx.android.note.actions.scribble.DocumentSaveAction;
import com.onyx.android.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.RedoAction;
import com.onyx.android.note.actions.scribble.UndoAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.data.PenType;
import com.onyx.android.note.data.ScribbleMenuCategory;
import com.onyx.android.note.data.ScribbleSubMenuID;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.note.view.ScribbleSubMenu;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by solskjaer49 on 16/8/12 18:09.
 */

public class SpanScribbleActivity extends BaseScribbleActivity {
    static final String TAG = ScribbleActivity.class.getCanonicalName();
    private TextView titleTextView;
    private EditText spanTextView;
    private GAdapter adapter;
    private ScribbleSubMenu scribbleSubMenu = null;
    private static final int SPAN_TIME_OUT = 1000;
    Runnable spanRunnable;
    long lastUpTime = -1;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.initWithAppConfig(this);
        setContentView(R.layout.onyx_activity_span_scribble);
        initSupportActionBarWithCustomBackFunction();
        initToolbarButtons();
        handler = new Handler(getMainLooper());
    }

    private void initToolbarButtons() {
        titleTextView = (TextView) findViewById(R.id.textView_main_title);
        spanTextView = (EditText)findViewById(R.id.span_text_view);
        ImageView addPageBtn = (ImageView) findViewById(R.id.button_add_page);
        ImageView deletePageBtn = (ImageView) findViewById(R.id.button_delete_page);
        ImageView prevPageBtn = (ImageView) findViewById(R.id.button_previous_page);
        ImageView nextPageBtn = (ImageView) findViewById(R.id.button_next_page);
        ImageView undoBtn = (ImageView) findViewById(R.id.button_undo);
        ImageView redoBtn = (ImageView) findViewById(R.id.button_redo);
        ImageView saveBtn = (ImageView) findViewById(R.id.button_save);
        ImageView settingBtn = (ImageView) findViewById(R.id.button_settings);
        ImageView exportBtn = (ImageView) findViewById(R.id.button_export);
        pageIndicator = (Button) findViewById(R.id.button_page_progress);
        ContentView functionContentView = (ContentView) findViewById(R.id.function_content_view);
        functionContentView.setShowPageInfoArea(false);
        functionContentView.setSubLayoutParameter(R.layout.onyx_main_function_item, getItemViewDataMap());
        functionContentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                final GObject temp = view.getData();
                syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        getScribbleSubMenu().show(ScribbleMenuCategory.translate(GAdapterUtil.getUniqueIdAsIntegerType(temp)), true);
                    }
                });
            }
        });
        functionContentView.setupContent(1, 4, getFunctionAdapter(), 0);
        addPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewPage();
            }
        });
        deletePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeletePage();
            }
        });
        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevPage();
            }
        });
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextPage();
            }
        });
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUndo();
            }
        });
        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRedo();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave(false);
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetting();
            }
        });
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExport();
            }
        });
    }

    private void onExport() {
        buildSpanImpl();
    }

    private void onSetting() {

    }

    private void onSave(final boolean finishAfterSave) {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                saveDocument(finishAfterSave);
            }
        });
    }

    private void onRedo() {
        syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final RedoAction<SpanScribbleActivity> action = new RedoAction<>();
                action.execute(SpanScribbleActivity.this);
            }
        });
    }

    private void onUndo() {
        syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final UndoAction<SpanScribbleActivity> action = new UndoAction<>();
                action.execute(SpanScribbleActivity.this);
            }
        });
    }

    private ScribbleSubMenu getScribbleSubMenu() {
        if (scribbleSubMenu == null) {
            scribbleSubMenu = new ScribbleSubMenu(this, shapeDataInfo, (RelativeLayout) this.findViewById(R.id.onyx_activity_scribble),
                    new ScribbleSubMenu.MenuCallback() {
                        @Override
                        public void onItemSelect(@ScribbleSubMenuID.ScribbleSubMenuIDDef int item) {
                            invokeSubMenuItem(item);
                        }

                        @Override
                        public void onCancel() {
                            syncWithCallback(true, true, null);
                        }

                        @Override
                        public void onLayoutStateChanged() {

                        }
                    }, R.id.divider, true
            );
        }
        return scribbleSubMenu;
    }

    private void invokeSubMenuItem(@ScribbleSubMenuID.ScribbleSubMenuIDDef int item) {
        switch (item) {
            //TODO:stroke width need confirm.
            case ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT:
                onStrokeWidthChanged(3.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_LIGHT:
                onStrokeWidthChanged(5.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_NORMAL:
                onStrokeWidthChanged(7.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_BOLD:
                onStrokeWidthChanged(9.0f, null);
                break;
            case ScribbleSubMenuID.THICKNESS_ULTRA_BOLD:
                onStrokeWidthChanged(11.0f, null);
                break;
            case ScribbleSubMenuID.ERASE_PARTIALLY:
                onEraseClicked(true);
                break;
            case ScribbleSubMenuID.ERASE_TOTALLY:
                onEraseClicked(false);
                break;
            case ScribbleSubMenuID.NORMAL_PEN_STYLE:
                onNoteShapeChanged(true, true, ShapeFactory.SHAPE_PENCIL_SCRIBBLE, null);
                break;
            case ScribbleSubMenuID.BRUSH_PEN_STYLE:
                onNoteShapeChanged(true, true, ShapeFactory.SHAPE_BRUSH_SCRIBBLE, null);
                break;
            case ScribbleSubMenuID.LINE_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_LINE, null);
                break;
            case ScribbleSubMenuID.CIRCLE_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_CIRCLE, null);
                break;
            case ScribbleSubMenuID.RECT_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_RECTANGLE, null);
                break;
            case ScribbleSubMenuID.TRIANGLE_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_TRIANGLE, null);
                break;
            case ScribbleSubMenuID.BG_EMPTY:
                setBackgroundType(NoteBackgroundType.EMPTY);
                onBackgroundChanged();
                break;
            case ScribbleSubMenuID.BG_LINE:
                setBackgroundType(NoteBackgroundType.LINE);
                onBackgroundChanged();
                break;
            case ScribbleSubMenuID.BG_GRID:
                setBackgroundType(NoteBackgroundType.GRID);
                onBackgroundChanged();
                break;
        }
    }

    private void onBackgroundChanged() {
        final NoteBackgroundChangeAction<SpanScribbleActivity> changeBGAction =
                new NoteBackgroundChangeAction<>(getBackgroundType(), !getNoteViewHelper().inUserErasing());
        changeBGAction.execute(SpanScribbleActivity.this, null);
    }

    private HashMap<String, Integer> getItemViewDataMap() {
        HashMap<String, Integer> mapping = new HashMap<>();
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.item_img);
        return mapping;
    }

    private GAdapter getFunctionAdapter() {
        if (adapter == null) {
            adapter = new GAdapter();
            adapter.addObject(createFunctionItem(R.drawable.ic_width, ScribbleMenuCategory.PEN_WIDTH));
            adapter.addObject(createFunctionItem(R.drawable.ic_shape, ScribbleMenuCategory.PEN_STYLE));
            adapter.addObject(createFunctionItem(R.drawable.ic_eraser, ScribbleMenuCategory.ERASER));
            adapter.addObject(createFunctionItem(R.drawable.ic_template, ScribbleMenuCategory.BG));
        }
        return adapter;
    }

    @Override
    protected void handleActivityIntent(final Intent intent) {
        super.handleActivityIntent(intent);
        if (StringUtils.isNotBlank(noteTitle)) {
            titleTextView.setText(noteTitle);
        }
    }

    @Override
    protected void cleanUpAllPopMenu() {
        if (getScribbleSubMenu().isShow()) {
            getScribbleSubMenu().dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        getNoteViewHelper().pauseDrawing();
        onSave(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void saveDocument(boolean finishAfterSave) {
        if (isNewDocument()) {
            saveNewNoteDocument(finishAfterSave);
        } else {
            saveExistingNoteDocument(finishAfterSave);
        }
    }

    private boolean isNewDocument() {
        return (Utils.ACTION_CREATE.equals(activityAction) || StringUtils.isNullOrEmpty(activityAction)) &&
                StringUtils.isNullOrEmpty(noteTitle);
    }

    private void saveNewNoteDocument(final boolean finishAfterSave) {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.save_note));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, noteTitle);
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, true);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(final String input) {
                final CheckNoteNameLegalityAction<SpanScribbleActivity> action = new
                        CheckNoteNameLegalityAction<>(input, parentID, NoteModel.TYPE_DOCUMENT, true, true);
                action.execute(SpanScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            saveDocumentWithTitle(input, finishAfterSave);
                        } else {
                            showNoteNameIllegal();
                        }
                    }
                });
                return false;
            }

            @Override
            public void onCancelAction() {
                dialogNoteNameInput.dismiss();
                syncWithCallback(true, true, null);
            }

            @Override
            public void onDiscardAction() {
                dialogNoteNameInput.dismiss();
                final DocumentDiscardAction<SpanScribbleActivity> discardAction = new DocumentDiscardAction<>(null);
                discardAction.execute(SpanScribbleActivity.this);
            }
        });
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogNoteNameInput.show(getFragmentManager());
            }
        });
    }

    private void saveDocumentWithTitle(final String title, final boolean finishAfterSave) {
        noteTitle = title;
        final DocumentSaveAction<SpanScribbleActivity> saveAction = new
                DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, finishAfterSave);
        saveAction.execute(SpanScribbleActivity.this, null);
    }

    private void saveExistingNoteDocument(final boolean finishAfterSave) {
        final DocumentSaveAction<SpanScribbleActivity> saveAction = new
                DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, finishAfterSave);
        saveAction.execute(SpanScribbleActivity.this, null);
    }

    private void onNoteShapeChanged(boolean render, boolean resume, int type, BaseCallback callback) {
        setCurrentShapeType(type);
        syncWithCallback(render, resume, callback);
    }

    private void onStrokeWidthChanged(float width, BaseCallback callback) {
        if (shapeDataInfo.isInUserErasing()) {
            setCurrentShapeType(PenType.PENCIL);
        }
        setStrokeWidth(width);
        syncWithCallback(true, true, callback);
    }

    private void onEraseClicked(boolean isPartialErase) {
        if (isPartialErase) {
            setCurrentShapeType(ShapeFactory.SHAPE_ERASER);
            syncWithCallback(true, false, null);
        } else {
            boolean resume = shouldResume();
            ClearAllFreeShapesAction<SpanScribbleActivity> action = new ClearAllFreeShapesAction<>(resume);
            action.execute(this, null);
        }
    }

    @Override
    protected void updateDataInfo(final BaseNoteRequest request) {
        super.updateDataInfo(request);
        getScribbleSubMenu().setCurShapeDataInfo(shapeDataInfo);
    }

    private GObject createFunctionItem(final int functionIconRes,
                                       @ScribbleMenuCategory.ScribbleMenuCategoryDef int menuCategory) {
        GObject object = GAdapterUtil.createTableItem(0, 0, functionIconRes, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, menuCategory);
        return object;
    }

    @Override
    protected NoteViewHelper.InputCallback inputCallback() {
        return new NoteViewHelper.InputCallback() {
            @Override
            public void onBeginRawData() {
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                onNewTouchPointListReceived(shape, pointList);
                long curTime = System.currentTimeMillis();
                triggerSpan();
            }

            @Override
            public void onBeginErasing() {
                SpanScribbleActivity.this.onBeginErasing();
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
                SpanScribbleActivity.this.onErasing(touchPoint);
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                onFinishErasing(pointList);
            }

            @Override
            public void onBeginShapeSelect() {
                
            }

            @Override
            public void onShapeSelecting(MotionEvent motionEvent) {

            }

            @Override
            public void onShapeSelectTouchPointListReceived(TouchPointList pointList) {

            }

            public void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    drawPage();
                }
            }

            public void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last) {
                if (last && !shape.supportDFB()) {
                    drawPage();
                }
            }

            public void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    drawPage();
                }
                triggerSpan();
            }
        };
    }

    private void triggerSpan() {
        long curTime = System.currentTimeMillis();
        if (lastUpTime != -1 && (curTime - lastUpTime <= SPAN_TIME_OUT) && (spanRunnable != null)) {
            handler.removeCallbacks(spanRunnable);
        }
        lastUpTime = curTime;
        spanRunnable = buildSpanRunnable();
        handler.postDelayed(spanRunnable, SPAN_TIME_OUT);
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
        List<Shape> list = getNoteViewHelper().detachStash();
//        final SpannableRequest spannableRequest = new SpannableRequest(list);
//        getNoteViewHelper().submit(this, spannableRequest, new BaseCallback() {
//            @Override
//            public void done(BaseRequest request, Throwable e) {
//                final SpannableStringBuilder builder = spannableRequest.getSpannableStringBuilder();
//                spanTextView.setText(builder);
//                spanTextView.setSelection(builder.length());
//            }
//        });
    }
}
