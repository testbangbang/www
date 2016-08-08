package com.onyx.android.note.activity.onyx;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.scribble.ClearPageAction;
import com.onyx.android.note.actions.scribble.DocumentAddNewPageAction;
import com.onyx.android.note.actions.scribble.DocumentCreateAction;
import com.onyx.android.note.actions.scribble.DocumentDeletePageAction;
import com.onyx.android.note.actions.scribble.DocumentDiscardAction;
import com.onyx.android.note.actions.scribble.DocumentEditAction;
import com.onyx.android.note.actions.scribble.DocumentFlushAction;
import com.onyx.android.note.actions.scribble.DocumentSaveAction;
import com.onyx.android.note.actions.scribble.GotoNextPageAction;
import com.onyx.android.note.actions.scribble.GotoPrevPageAction;
import com.onyx.android.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.RedoAction;
import com.onyx.android.note.actions.scribble.RemoveByPointListAction;
import com.onyx.android.note.actions.scribble.UndoAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.data.PenType;
import com.onyx.android.note.data.ScribbleMenuCategory;
import com.onyx.android.note.data.ScribbleSubMenuID;
import com.onyx.android.note.dialog.BackGroundTypePopupMenu;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.dialog.PenColorPopupMenu;
import com.onyx.android.note.dialog.PenWidthPopupMenu;
import com.onyx.android.note.receiver.DeviceReceiver;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.note.view.ScribbleSubMenu;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.request.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.onyx.android.sdk.scribble.shape.ShapeFactory.SHAPE_PENCIL_SCRIBBLE;


/**
 * when any button clicked, flush at first and render page, after that always switch to drawing state.
 */
public class ScribbleActivity extends BaseScribbleActivity {
    static final String TAG = ScribbleActivity.class.getCanonicalName();
    static final String TAG_NOTE_TITLE = "note_title";

    private SurfaceView surfaceView;
    private String activityAction;
    private String noteTitle;
    private TextView titleTextView;
    private ContentView functionContentView;
    private GAdapter adapter;
    private PenWidthPopupMenu penWidthPopupMenu;
    private BackGroundTypePopupMenu bgTypePopupMenu;
    PenColorPopupMenu penColorPopupMenu;
    private Button pageIndicator;
    private ScribbleSubMenu scribbleSubMenu = null;
    private TouchPoint erasePoint = null;
    private ShapeDataInfo shapeDataInfo = new ShapeDataInfo();
    private DeviceReceiver deviceReceiver = new DeviceReceiver();
    private SurfaceHolder.Callback surfaceCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.initWithAppConfig(this);
        setContentView(R.layout.onyx_activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        initToolbarButtons();
        registerDeviceReceiver();
    }

    public NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getNoteViewHelper();
    }

    private void registerDeviceReceiver() {
        deviceReceiver.setSystemUIChangeListener(new DeviceReceiver.SystemUIChangeListener() {
            @Override
            public void onSystemUIChanged(String type, boolean open) {
                if (open) {
                    onSystemUIOpened();
                } else {
                    onSystemUIClosed();
                }
            }

            @Override
            public void onHomeClicked() {
                getNoteViewHelper().enableScreenPost(true);
                finish();
            }
        });
        deviceReceiver.registerReceiver(this);
    }

    private void unregisterDeviceReceiver() {
        deviceReceiver.unregisterReceiver(this);
    }

    private void onSystemUIOpened() {
        syncWithCallback(true, false, null);
    }

    private void onSystemUIClosed() {
        syncWithCallback(true, !shapeDataInfo.isInUserErasing(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSurfaceView();
    }

    private void initToolbarButtons() {
        titleTextView = (TextView) findViewById(R.id.textView_main_title);
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
        functionContentView = (ContentView) findViewById(R.id.function_content_view);
        functionContentView.setShowPageInfoArea(false);
        functionContentView.setSubLayoutParameter(R.layout.onyx_main_function_item, getItemViewDataMap());
        functionContentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                final GObject temp = view.getData();
                syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        getScribbleSubMenu().show(ScribbleMenuCategory.translate(GAdapterUtil.getUniqueIdAsIntegerType(temp)));
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
        testSpan();
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
                final RedoAction<ScribbleActivity> action = new RedoAction<>();
                action.execute(ScribbleActivity.this);
            }
        });
    }

    private void onUndo() {
        syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final UndoAction<ScribbleActivity> action = new UndoAction<>();
                action.execute(ScribbleActivity.this);
            }
        });
    }

    private void onDeletePage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentDeletePageAction<ScribbleActivity> action = new DocumentDeletePageAction<>();
                action.execute(ScribbleActivity.this, null);
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
        final NoteBackgroundChangeAction<ScribbleActivity> changeBGAction = new NoteBackgroundChangeAction<>(getBackgroundType());
        changeBGAction.execute(ScribbleActivity.this, null);
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

    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.note_view);
        surfaceView.getHolder().addCallback(surfaceCallback());
    }

    private SurfaceHolder.Callback surfaceCallback() {
        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    clearSurfaceView();
                    getNoteViewHelper().setView(ScribbleActivity.this, surfaceView, inputCallback());
                    handleActivityIntent(getIntent());
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    surfaceHolder.removeCallback(surfaceCallback);
                    surfaceCallback = null;
                }
            };
        }
        return surfaceCallback;
    }

    private void handleActivityIntent(final Intent intent) {
        if (!intent.hasExtra(Utils.ACTION_TYPE)) {
            handleDocumentCreate(ShapeUtils.generateUniqueId(), null);
            return;
        }
        activityAction = intent.getStringExtra(Utils.ACTION_TYPE);
        noteTitle = intent.getStringExtra(TAG_NOTE_TITLE);
        titleTextView.setText(noteTitle);
        if (Utils.ACTION_CREATE.equals(activityAction)) {
            handleDocumentCreate(intent.getStringExtra(Utils.DOCUMENT_ID),
                    intent.getStringExtra(Utils.PARENT_LIBRARY_ID));
        } else if (Utils.ACTION_EDIT.equals(activityAction)) {
            handleDocumentEdit(intent.getStringExtra(Utils.DOCUMENT_ID),
                    intent.getStringExtra(Utils.PARENT_LIBRARY_ID));
        }
    }

    private NoteViewHelper.InputCallback inputCallback() {
        return new NoteViewHelper.InputCallback() {
            @Override
            public void onBeginRawData() {

            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                ScribbleActivity.this.onNewTouchPointListReceived(shape, pointList);
            }

            @Override
            public void onBeginErasing() {
                ScribbleActivity.this.onBeginErasing();
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
                ScribbleActivity.this.onErasing(touchPoint);
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                ScribbleActivity.this.onFinishErasing(pointList);
            }

            public void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape) {
                ScribbleActivity.this.drawPage();
            }

            public void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last) {
                if (last) {
                    ScribbleActivity.this.drawPage();
                }
            }

            public void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape) {
                ScribbleActivity.this.drawPage();
            }

        };
    }

    private void onNewTouchPointListReceived(final Shape shape, TouchPointList pointList) {
        //final AddShapeInBackgroundAction<ScribbleActivity> action = new AddShapeInBackgroundAction<>(shape);
        //action.execute(this, null);
    }

    private void onBeginErasing() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                erasePoint = new TouchPoint();
            }
        });
    }

    private void onErasing(final MotionEvent touchPoint) {
        if (erasePoint == null) {
            return;
        }
        erasePoint.x = touchPoint.getX();
        erasePoint.y = touchPoint.getY();
        drawPage();
    }

    private void onFinishErasing(TouchPointList pointList) {
        erasePoint = null;
        drawPage();
        RemoveByPointListAction<ScribbleActivity> removeByPointListAction = new RemoveByPointListAction<>(pointList);
        removeByPointListAction.execute(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        syncWithCallback(true, false, null);
    }

    @Override
    protected void onDestroy() {
        cleanUpAllPopMenu();
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getNoteViewHelper().quit();
            }
        });
        unregisterDeviceReceiver();
        super.onDestroy();
    }

    private void cleanUpAllPopMenu() {
        if (penWidthPopupMenu != null && penWidthPopupMenu.isShowing()) {
            penWidthPopupMenu.dismiss();
        }
        if (bgTypePopupMenu != null && bgTypePopupMenu.isShowing()) {
            bgTypePopupMenu.dismiss();
        }
        if (penColorPopupMenu != null && penColorPopupMenu.isShowing()) {
            penColorPopupMenu.dismiss();
        }
        penWidthPopupMenu = null;
        bgTypePopupMenu = null;
    }

    public void onBackPressed() {
        getNoteViewHelper().pauseDrawing();
        onSave(true);
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
                final CheckNoteNameLegalityAction<ScribbleActivity> action = new CheckNoteNameLegalityAction<ScribbleActivity>(input);
                action.execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            saveDocumentWithTitle(input, finishAfterSave);
                        } else {
                            showNoteNameIllegal();
                        }
                    }
                });
                return true;
            }

            @Override
            public void onCancelAction() {
                dialogNoteNameInput.dismiss();
                syncWithCallback(true, true, null);
            }

            @Override
            public void onDiscardAction() {
                dialogNoteNameInput.dismiss();
                final DocumentDiscardAction<ScribbleActivity> discardAction = new DocumentDiscardAction<>(null);
                discardAction.execute(ScribbleActivity.this);
            }
        });
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogNoteNameInput.show(getFragmentManager());
            }
        });
    }

    private void showNoteNameIllegal() {
        final OnyxAlertDialog illegalDialog = new OnyxAlertDialog();
        illegalDialog.setParams(new OnyxAlertDialog.Params().setTittleString(getString(R.string.noti))
                .setCustomLayoutResID(R.layout.mx_custom_alert_dialog)
                .setAlertMsgString(getString(R.string.note_name_already_exist))
                .setEnableNegativeButton(false).setCanceledOnTouchOutside(false)
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        illegalDialog.dismiss();
                        syncWithCallback(true, true, null);
                    }
                }));
        illegalDialog.show(getFragmentManager(), "illegalDialog");
    }

    private void saveDocumentWithTitle(final String title, final boolean finishAfterSave) {
        noteTitle = title;
        final DocumentSaveAction<ScribbleActivity> saveAction = new
                DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, finishAfterSave);
        saveAction.execute(ScribbleActivity.this, null);
    }

    private void saveExistingNoteDocument(final boolean finishAfterSave) {
        final DocumentSaveAction<ScribbleActivity> saveAction = new
                DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, finishAfterSave);
        saveAction.execute(ScribbleActivity.this, null);
    }

    private void setCurrentShapeType(int type) {
        shapeDataInfo.setCurrentShapeType(type);
    }

    private void setBackgroundType(int type) {
        shapeDataInfo.setBackground(type);
    }

    private int getBackgroundType() {
        return shapeDataInfo.getBackground();
    }

    private void setStrokeWidth(float width) {
        shapeDataInfo.setStrokeWidth(width);
    }

    private void setStrokeColor(int color) {
        shapeDataInfo.setStrokeColor(color);
    }

    private void onPencilClicked() {
        setCurrentShapeType(SHAPE_PENCIL_SCRIBBLE);
        setStrokeWidth(3.0f);
        syncWithCallback(true, true, null);
    }

    private void undo() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final UndoAction<ScribbleActivity> undoAction = new UndoAction<>();
                undoAction.execute(ScribbleActivity.this);

            }
        });
    }

    private void redo() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final RedoAction<ScribbleActivity> redoAction = new RedoAction<>();
                redoAction.execute(ScribbleActivity.this);
            }
        });
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

    private void onColorChange(final int currentPenColor) {
        setStrokeColor(currentPenColor);
        syncWithCallback(true, true, null);
    }

    private void onEraseClicked(boolean isPartialErase) {
        if (isPartialErase) {
            setCurrentShapeType(ShapeFactory.SHAPE_ERASER);
            syncWithCallback(true, false, null);
        } else {
            ClearPageAction<ScribbleActivity> action = new ClearPageAction<>();
            action.execute(this, null);
        }
    }

    private void handleDocumentCreate(final String uniqueId, final String parentId) {
        final DocumentCreateAction<ScribbleActivity> action = new DocumentCreateAction<>(uniqueId, parentId);
        action.execute(this);
    }

    private void handleDocumentEdit(final String uniqueId, final String parentId) {
        final DocumentEditAction<ScribbleActivity> action = new DocumentEditAction<>(uniqueId, parentId);
        action.execute(this);
    }

    private void syncWithCallback(boolean render,
                                  boolean resume,
                                  final BaseCallback callback) {
        final List<Shape> stash = getNoteViewHelper().deatchStash();
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<>(stash,
                render,
                resume,
                shapeDataInfo.getDrawingArgs());
        action.execute(this, callback);
    }

    private void onAddNewPage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentAddNewPageAction<ScribbleActivity> action = new DocumentAddNewPageAction<>(-1);
                action.execute(ScribbleActivity.this);
            }
        });
    }

    private void onNextPage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final GotoNextPageAction<ScribbleActivity> action = new GotoNextPageAction<>();
                action.execute(ScribbleActivity.this);
            }
        });
    }

    private void onPrevPage() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final GotoPrevPageAction<ScribbleActivity> action = new GotoPrevPageAction<>();
                action.execute(ScribbleActivity.this);
            }
        });
    }

    private int getCurrentShapeType() {
        return shapeDataInfo.getCurrentShapeType();
    }

    private int getCurrentShapeColor() {
        return shapeDataInfo.getStrokeColor();
    }

    private float getCurrentStrokeWidth() {
        return shapeDataInfo.getStrokeWidth();
    }

    private void updateDataInfo(final BaseNoteRequest request) {
        shapeDataInfo = request.getShapeDataInfo();
        int currentPageIndex = shapeDataInfo.getCurrentPageIndex() + 1;
        int pageCount = shapeDataInfo.getPageCount();
        pageIndicator.setText(currentPageIndex + File.separator + pageCount);
        updatePenIndicator(shapeDataInfo);
        getScribbleSubMenu().setCurShapeDataInfo(shapeDataInfo);
    }

    private int indexOf(int shapeType) {
        int pen = PenType.shapeToPen(shapeType);
        final List<GObject> list = getFunctionAdapter().getList();
        for (int i = 0; i < list.size(); ++i) {
            int value = Integer.decode(GAdapterUtil.getUniqueId(list.get(i)));
            if (value == pen) {
                return i;
            }
        }
        return -1;
    }

    private void updatePenIndicator(final ShapeDataInfo shapeDataInfo) {
        int type = shapeDataInfo.getCurrentShapeType();
        int index = indexOf(type);
        if (index < 0) {
            return;
        }
        selectPenStyle(index);
    }

    private void selectPenStyle(int index) {
        final GObject object = functionContentView.getCurrentAdapter().get(index);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        functionContentView.getCurrentAdapter().setObject(index, object);
        functionContentView.unCheckOtherViews(index, true);
        functionContentView.updateCurrentPage();
    }

    private void clearSurfaceView() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        afterDraw(canvas);
    }

    private void cleanup(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    public void drawPage() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        drawContent(canvas, paint);
        drawStashShape(canvas, paint);
        drawErasingIndicator(canvas, paint);
        afterDraw(canvas);
    }

    private Canvas beforeDraw(final Rect rect) {
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        return canvas;
    }

    private void afterDraw(final Canvas canvas) {
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawContent(final Canvas canvas, final Paint paint) {
        Bitmap bitmap = getNoteViewHelper().getViewBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    private void drawStashShape(final Canvas canvas, final Paint paint) {
        final List<Shape> stash = getNoteViewHelper().getDirtyStash();
        for (Shape shape : stash) {
            shape.render(canvas, paint, null);
        }
    }

    private void drawErasingIndicator(final Canvas canvas, final Paint paint) {
        if (erasePoint == null || erasePoint.getX() <= 0 || erasePoint.getY() <= 0) {
            return;
        }

        float x = erasePoint.getX();
        float y = erasePoint.getY();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        canvas.drawCircle(x, y, shapeDataInfo.getEraserRadius(), paint);
    }

    public Rect getViewportSize() {
        return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
    }

    private GObject createFunctionItem(final int functionIconRes, @ScribbleMenuCategory.ScribbleMenuCategoryDef int menuCategory) {
        GObject object = GAdapterUtil.createTableItem(0, 0, functionIconRes, 0, null);
        object.putInt(GAdapterUtil.TAG_UNIQUE_ID, menuCategory);
        return object;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                onNextPage();
                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                onPrevPage();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void submitRequest(BaseNoteRequest request, BaseCallback callback) {
        getNoteViewHelper().submit(this, request, callback);
    }

    @Override
    public void submitRequestWithIdentifier(String identifier, BaseNoteRequest request, BaseCallback callback) {
        getNoteViewHelper().submitRequestWithIdentifier(this, identifier, request, callback);
    }

    @Override
    public void onRequestFinished(final BaseNoteRequest request, boolean updatePage) {
        updateDataInfo(request);
        if (request.isAbort()) {
            return;
        }
        if (updatePage) {
            drawPage();
        }
    }

    private void testSpan() {
        final List<Shape> stash = getNoteViewHelper().deatchStash();
        final SpannableRequest spannableRequest = new SpannableRequest(stash);
        getNoteViewHelper().submit(this, spannableRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final Dialog dlg = new Dialog(ScribbleActivity.this);
                dlg.setContentView(R.layout.span_text_view);
                dlg.setTitle("Message");
                TextView textView = (TextView)dlg.findViewById(R.id.text_view);
                textView.setText(spannableRequest.getSpannableStringBuilder());
                syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        dlg.show();
                    }
                });

            }
        });
    }
}
