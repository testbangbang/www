package com.onyx.android.note.activity.onyx;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.scribble.ClearPageAction;
import com.onyx.android.note.actions.scribble.DocumentDiscardAction;
import com.onyx.android.note.actions.scribble.DocumentFlushAction;
import com.onyx.android.note.actions.scribble.DocumentSaveAction;
import com.onyx.android.note.actions.scribble.ExportNoteAction;
import com.onyx.android.note.actions.scribble.GotoTargetPageAction;
import com.onyx.android.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.NoteLineLayoutBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.PenColorChangeAction;
import com.onyx.android.note.actions.scribble.RedoAction;
import com.onyx.android.note.actions.scribble.RemoveByGroupIdAction;
import com.onyx.android.note.actions.scribble.UndoAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.data.PenType;
import com.onyx.android.note.data.ScribbleMenuCategory;
import com.onyx.android.note.data.ScribbleSubMenuID;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.handler.SpanTextHandler;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.note.view.LinedEditText;
import com.onyx.android.note.view.ScribbleSubMenu;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.android.sdk.ui.dialog.DialogSetValue;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.HashMap;
import java.util.List;


/**
 * when any button clicked, flush at first and render page, after that always switch to drawing state.
 */
public class ScribbleActivity extends BaseScribbleActivity {
    static final String TAG = ScribbleActivity.class.getCanonicalName();
    private TextView titleTextView;
    private ScribbleSubMenu scribbleSubMenu = null;
    private ImageView switchBtn;
    private ContentView functionContentView;
    private RelativeLayout workView;
    private LinedEditText spanTextView;
    private SpanTextHandler spanTextHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.setFullScreenOnCreate(this, true);
        setContentView(R.layout.onyx_activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        initToolbarButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this, true);
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
        ImageView exportBtn = (ImageView) findViewById(R.id.button_export);
        ImageView settingBtn = (ImageView) findViewById(R.id.button_setting);
        workView = (RelativeLayout) findViewById(R.id.work_view);
        spanTextView = (LinedEditText) findViewById(R.id.span_text_view);
        switchBtn = (ImageView) findViewById(R.id.button_switch);
        exportBtn.setVisibility(NoteAppConfig.sharedInstance(this).isEnableExport() ? View.VISIBLE : View.GONE);
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
                        int category = ScribbleMenuCategory.translate(GAdapterUtil.getUniqueIdAsIntegerType(temp));
                        if (digestionSpanMenu(category)) {
                            return;
                        }
                        getScribbleSubMenu().show(category, isLineLayoutMode());
                    }
                });
            }
        });

        addPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSpanTextMode();
                onAddNewPage();
            }
        });
        deletePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSpanTextMode();
                onDeletePage();
            }
        });
        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSpanTextMode();
                onPrevPage();
            }
        });
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSpanTextMode();
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
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExportMenu();
            }
        });
        pageIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null) {
                            showGotoPageDialog();
                        }
                    }
                });
            }
        });

        getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    syncWithCallback(true, true, null);
                }
            }
        });

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLineLayoutMode();
                switchScribbleMode(isLineLayoutMode());
                syncWithCallback(true, true, null);
            }
        });

        switchScribbleMode(isLineLayoutMode());
        initSpanTextView();
    }

    private void initSpanTextView() {
        spanTextView.setCursorVisible(true);
        final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.note_view);
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                spanTextView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, surfaceView.getMeasuredHeight() * 2 / 3));
                spanTextView.setFocusable(true);
            }
        });

        spanTextHandler = new SpanTextHandler(this, new SpanTextHandler.Callback() {
            @Override
            public void OnFinishedSpan(SpannableStringBuilder builder, final List<Shape> spanShapeList) {
                if (builder == null) {
                    return;
                }
                spanTextView.setText(builder);
                spanTextView.setSelection(builder.length());
                spanTextView.requestFocus();
                spanTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        final DocumentFlushAction<BaseScribbleActivity> action = new DocumentFlushAction<>(spanShapeList,
                                true,
                                true,
                                shapeDataInfo.getDrawingArgs());
                        action.execute(ScribbleActivity.this, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                spanTextView.invalidate();
                            }
                        });
                    }
                });
            }
        });

        spanTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DEL:
                        onDelete();
                        return true;
                }
                return false;
            }
        });

        spanTextView.setInputConnectionListener(new LinedEditText.InputConnectionListener() {
            @Override
            public void commitText(CharSequence text, int newCursorPosition) {
                int width = (int) spanTextView.getPaint().measureText(text.toString());
                spanTextHandler.buildTextShape(text.toString(), width, getSpanTextFontHeight());
            }
        });

    }

    private boolean digestionSpanMenu(final @ScribbleMenuCategory.ScribbleMenuCategoryDef
                                          int category) {
        switch (category) {
            case ScribbleMenuCategory.DELETE:
                onDelete();
                return true;
            case ScribbleMenuCategory.SPACE:
                onSpace();
                return true;
            case ScribbleMenuCategory.ENTER:
                onEnter();
                return true;
            case ScribbleMenuCategory.KEYBOARD:
                onKeyboard();
                return true;
        }
        return false;
    }

    private void closeSpanTextMode() {
        setLineLayoutMode(false);
        switchScribbleMode(isLineLayoutMode());
        spanTextHandler.clear();
        spanTextView.setText("");
    }

    private void switchScribbleMode(boolean isLineLayoutMode) {
        cleanUpAllPopMenu();
        if (isLineLayoutMode) {
            spanTextHandler.openSpanTextFunc();
        }
        updateMenuView(isLineLayoutMode);
        updateWorkView(isLineLayoutMode);
    }

    private void updateMenuView(boolean isLineLayoutMode) {
        switchBtn.setImageResource(isLineLayoutMode ? R.drawable.ic_vector : R.drawable.ic_note);
        functionContentView.setupContent(1, getResources().getInteger(R.integer.onyx_scribble_main_function_cols), getFunctionAdapter(isLineLayoutMode), 0, true);
    }

    private void updateWorkView(boolean isLineLayoutMode) {
        spanTextView.setVisibility(isLineLayoutMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.onyx_scribble_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_current_page:
                onExport(true);
                break;
            case R.id.export_all_pages:
                onExport(false);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGotoPageDialog() {
        final int originalVisualPageIndex = currentVisualPageIndex;
        final DialogSetValue dlg = new DialogSetValue();
        Bundle args = new Bundle();
        args.putString(DialogSetValue.ARGS_DIALOG_TITLE, getString(R.string.go_to_page));
        args.putString(DialogSetValue.ARGS_VALUE_TITLE, getString(R.string.current_page));
        args.putInt(DialogSetValue.ARGS_CURRENT_VALUE, originalVisualPageIndex);
        args.putInt(DialogSetValue.ARGS_MAX_VALUE, totalPageCount);
        args.putInt(DialogSetValue.ARGS_MIN_VALUE, 1);
        dlg.setArguments(args);
        dlg.setCallback(new DialogSetValue.DialogCallback() {
            @Override
            public void valueChange(int newValue) {
                int logicalIndex = newValue - 1;
                GotoTargetPageAction<ScribbleActivity> action = new GotoTargetPageAction<>(logicalIndex);
                action.execute(ScribbleActivity.this);
            }

            @Override
            public void done(boolean isValueChange, int newValue) {
                GotoTargetPageAction<ScribbleActivity> action =
                        new GotoTargetPageAction<>(isValueChange ? newValue : originalVisualPageIndex -1);
                action.execute(ScribbleActivity.this);
                syncWithCallback(true, true, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null) {
                            dlg.dismiss();
                        }
                    }
                });
            }
        });
        dlg.show(getFragmentManager());
    }

    private void onExport(boolean exportCurPage) {
        new ExportNoteAction<>(this,
                shapeDataInfo.getDocumentUniqueId(),
                shapeDataInfo.getPageNameList().getPageNameList(),
                noteTitle,
                exportCurPage,
                shapeDataInfo.getCurrentPageIndex()).execute(ScribbleActivity.this, null);
    }

    private void showExportMenu() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getSupportActionBar().openOptionsMenu();
            }
        });
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

    private void onDelete() {
        RemoveByGroupIdAction<BaseScribbleActivity> removeByPointListAction = new
                RemoveByGroupIdAction<>(spanTextHandler.getLastGroupId());
        removeByPointListAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                spanTextHandler.loadPageShapes();
            }
        });
    }

    private void onSpace() {
        spanTextHandler.buildSpaceShape(SpanTextHandler.SPACE_WIDTH, getSpanTextFontHeight());
    }

    private int getSpanTextFontHeight() {
        float bottom = spanTextView.getPaint().getFontMetrics().bottom;
        float top = spanTextView.getPaint().getFontMetrics().top;
        int height = (int) Math.ceil(bottom - top - 2 * ShapeSpan.SHAPE_SPAN_MARGIN);
        return height;
    }

    private void onEnter() {
        int pos = spanTextView.getSelectionStart();
        Layout layout = spanTextView.getLayout();
        float x = layout.getPrimaryHorizontal(pos);

        spanTextHandler.buildSpaceShape((int) Math.ceil(spanTextView.getMeasuredWidth() - x), getSpanTextFontHeight());
    }

    private void onKeyboard() {
        spanTextView.setFocusable(true);
        spanTextView.setFocusableInTouchMode(true);
        spanTextView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)spanTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(spanTextView, 0);
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
            case ScribbleSubMenuID.THICKNESS_ULTRA_LIGHT:
            case ScribbleSubMenuID.THICKNESS_LIGHT:
            case ScribbleSubMenuID.THICKNESS_NORMAL:
            case ScribbleSubMenuID.THICKNESS_BOLD:
            case ScribbleSubMenuID.THICKNESS_ULTRA_BOLD:
                float value = ScribbleSubMenuID.strokeWidthFromMenuId(item);
                onStrokeWidthChanged(value, null);
                break;
            case ScribbleSubMenuID.THICKNESS_CUSTOM_BOLD:
                showCustomLineWidthDialog();
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
                onBackgroundChanged(NoteBackgroundType.EMPTY);
                break;
            case ScribbleSubMenuID.BG_LINE:
                onBackgroundChanged(NoteBackgroundType.LINE);
                break;
            case ScribbleSubMenuID.BG_GRID:
                onBackgroundChanged(NoteBackgroundType.GRID);
                break;
            case ScribbleSubMenuID.BG_MUSIC:
                onBackgroundChanged(NoteBackgroundType.MUSIC);
                break;
            case ScribbleSubMenuID.BG_MATS:
                onBackgroundChanged(NoteBackgroundType.MATS);
                break;
            case ScribbleSubMenuID.BG_ENGLISH:
                onBackgroundChanged(NoteBackgroundType.ENGLISH);
                break;
            case ScribbleSubMenuID.PEN_COLOR_BLACK:
                setStrokeColor(Color.BLACK);
                onPenColoChanged();
                break;
            case ScribbleSubMenuID.PEN_COLOR_BLUE:
                setStrokeColor(Color.BLUE);
                onPenColoChanged();
                break;
            case ScribbleSubMenuID.PEN_COLOR_GREEN:
                setStrokeColor(Color.GREEN);
                onPenColoChanged();
                break;
            case ScribbleSubMenuID.PEN_COLOR_YELLOW:
                setStrokeColor(Color.YELLOW);
                onPenColoChanged();
                break;
            case ScribbleSubMenuID.PEN_COLOR_RED:
                setStrokeColor(Color.RED);
                onPenColoChanged();
                break;
            case ScribbleSubMenuID.PEN_COLOR_MAGENTA:
                setStrokeColor(Color.MAGENTA);
                onPenColoChanged();
                break;
        }
    }

    private void showCustomLineWidthDialog() {
        DialogCustomLineWidth customLineWidth = new DialogCustomLineWidth(this,
                (int) shapeDataInfo.getStrokeWidth(),
                20, Color.BLACK, new DialogCustomLineWidth.Callback() {
            @Override
            public void done(int lineWidth) {
                onStrokeWidthChanged(lineWidth, null);
            }
        });
        customLineWidth.show();
    }

    private void onBackgroundChanged(int type) {
        if (isLineLayoutMode()) {
            shapeDataInfo.setLineLayoutBackground(type);
            spanTextView.setShowLineBackground(type == NoteBackgroundType.LINE);
            final NoteLineLayoutBackgroundChangeAction<ScribbleActivity> changeBGAction =
                    new NoteLineLayoutBackgroundChangeAction<>(type, true);
            changeBGAction.execute(ScribbleActivity.this, null);
            return;
        }

        setBackgroundType(type);
        final NoteBackgroundChangeAction<ScribbleActivity> changeBGAction =
                new NoteBackgroundChangeAction<>(getBackgroundType(), !getNoteViewHelper().inUserErasing());
        changeBGAction.execute(ScribbleActivity.this, null);
    }

    private void onPenColoChanged(){
        final PenColorChangeAction<ScribbleActivity> penColorChangeAction =
                new PenColorChangeAction<>(getCurrentShapeColor(), !getNoteViewHelper().inUserErasing());
        penColorChangeAction.execute(ScribbleActivity.this, null);
    }

    private HashMap<String, Integer> getItemViewDataMap() {
        HashMap<String, Integer> mapping = new HashMap<>();
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.item_img);
        return mapping;
    }

    private GAdapter getFunctionAdapter(boolean isLineLayoutMode) {
        GAdapter adapter = new GAdapter();
        adapter.addObject(createFunctionItem(R.drawable.ic_shape, ScribbleMenuCategory.PEN_STYLE));
        adapter.addObject(createFunctionItem(R.drawable.ic_template, ScribbleMenuCategory.BG));
        if (!isLineLayoutMode) {
            adapter.addObject(createFunctionItem(R.drawable.ic_eraser, ScribbleMenuCategory.ERASER));
            adapter.addObject(createFunctionItem(R.drawable.ic_width, ScribbleMenuCategory.PEN_WIDTH));
        }else {
            adapter.addObject(createFunctionItem(R.drawable.ic_delet_big, ScribbleMenuCategory.DELETE));
            adapter.addObject(createFunctionItem(R.drawable.ic_space, ScribbleMenuCategory.SPACE));
            adapter.addObject(createFunctionItem(R.drawable.ic_enter, ScribbleMenuCategory.ENTER));
            adapter.addObject(createFunctionItem(R.drawable.ic_keyboard, ScribbleMenuCategory.KEYBOARD));
        }
        if (getNoteViewHelper().supportColor(this)){
            adapter.addObject(createFunctionItem(R.drawable.ic_color, ScribbleMenuCategory.COLOR));
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
                final CheckNoteNameLegalityAction<ScribbleActivity> action =
                        new CheckNoteNameLegalityAction<>(input, parentID, NoteModel.TYPE_DOCUMENT, false, false);
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
            ClearPageAction<ScribbleActivity> action = new ClearPageAction<>();
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
    protected void triggerLineLayoutMode(boolean isLineLayoutMode) {
        if (!isLineLayoutMode) {
            return;
        }
        spanTextHandler.buildSpan();
    }
}
