package com.onyx.android.note.activity.onyx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.android.note.actions.scribble.DocumentDiscardAction;
import com.onyx.android.note.actions.scribble.DocumentFlushAction;
import com.onyx.android.note.actions.scribble.DocumentSaveAction;
import com.onyx.android.note.actions.scribble.ExportEditedPicAction;
import com.onyx.android.note.actions.scribble.ExportNoteAction;
import com.onyx.android.note.actions.scribble.GotoTargetPageAction;
import com.onyx.android.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.NoteLineLayoutBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.NoteSetBackgroundAsLocalFileAction;
import com.onyx.android.note.actions.scribble.PenColorChangeAction;
import com.onyx.android.note.actions.scribble.RedoAction;
import com.onyx.android.note.actions.scribble.RemoveByGroupIdAction;
import com.onyx.android.note.actions.scribble.UndoAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.note.data.ScribbleMenuCategory;
import com.onyx.android.note.data.ScribbleSubMenuID;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.handler.SpanTextHandler;
import com.onyx.android.note.receiver.DeviceReceiver;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.note.view.LinedEditText;
import com.onyx.android.note.view.ScribbleSubMenu;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.shape.SpannableRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.android.sdk.ui.dialog.DialogSetValue;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
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
    private FrameLayout workView;
    private RelativeLayout rootView;
    private LinedEditText spanTextView;
    private SpanTextHandler spanTextHandler;
    private boolean isKeyboardInput = false;
    private boolean buildingSpan = false;
    private boolean isPictureEditMode = false;
    private Uri editPictUri;
    private WakeLockHolder wakeLockHolder = new WakeLockHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.setFullScreenOnCreate(this, true);
        setContentView(R.layout.onyx_activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        checkPictureEditMode();
        initToolbarButtons();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        wakeLockHolder.acquireWakeLock(this, TAG);
        DeviceUtils.setFullScreenOnResume(this, true);
        if (AppCompatUtils.isColorDevice(this)){
            Device.currentDevice().postInvalidate(getWindow().getDecorView(), UpdateMode.GC);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hidePotentialShowSubMenu();
        wakeLockHolder.releaseWakeLock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPictureEditMode() {
        Intent editIntent = getIntent();
        if (Intent.ACTION_EDIT.equalsIgnoreCase(editIntent.getAction())) {
            isPictureEditMode = true;
            editPictUri = editIntent.getData();
        }
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
        workView = (FrameLayout) findViewById(R.id.work_view);
        rootView = (RelativeLayout) findViewById(R.id.onyx_activity_scribble);
        spanTextView = (LinedEditText) findViewById(R.id.span_text_view);
        switchBtn = (ImageView) findViewById(R.id.button_switch);
        settingBtn.setVisibility(NoteAppConfig.sharedInstance(this).useEduConfig() ? View.GONE : View.VISIBLE);
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
                        if (processDirectMenuItemClick(category)) {
                            return;
                        }
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
                hidePotentialShowSubMenu();
                onPrevPage();
            }
        });
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePotentialShowSubMenu();
                onNextPage();
            }
        });
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePotentialShowSubMenu();
                onUndo();
            }
        });
        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePotentialShowSubMenu();
                onRedo();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePotentialShowSubMenu();
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
                            hidePotentialShowSubMenu();
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
                syncWithCallback(true, true, null);
                toggleLineLayoutMode();
                switchScribbleMode(isLineLayoutMode());
            }
        });

        switchBtn.setVisibility(NoteAppConfig.sharedInstance(this).useLineLayout() ? View.VISIBLE : View.GONE);
        initSpanTextView();

        //avoid creating global variable, just clean up ui here instead of creating new method.
        if (isPictureEditMode){
            findViewById(R.id.page_count_control).setVisibility(View.GONE);
            findViewById(R.id.page_indicator).setVisibility(View.GONE);
            exportBtn.setVisibility(View.GONE);
            switchBtn.setVisibility(View.GONE);
            settingBtn.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
        }
        getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    getWindow().getDecorView().post(new Runnable() {
                        @Override
                        public void run() {
                            boolean resume = shouldResume();
                            syncWithCallback(true, resume, null);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onAddNewPage() {
        hidePotentialShowSubMenu();
        super.onAddNewPage();
    }

    @Override
    protected void onDeletePage() {
        hidePotentialShowSubMenu();
        super.onDeletePage();
    }

    private void hidePotentialShowSubMenu(){
        if (getScribbleSubMenu().isShow()) {
            getScribbleSubMenu().dismiss(false);
        }
    }

    private void initSpanTextView() {
        spanTextHandler = new SpanTextHandler(this, new SpanTextHandler.Callback() {
            @Override
            public void OnFinishedSpan(SpannableStringBuilder builder, final List<Shape> spanShapeList, final ShapeSpan lastShapeSpan) {
                if (builder == null) {
                    return;
                }
                setBuildingSpan(true);
                spanTextView.setText(builder);
                spanTextView.setSelection(builder.length());
                spanTextView.requestFocus();
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
        });

        spanTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DEL:
                            setKeyboardInput(true);
                            onDelete(false);
                            return true;
                        case KeyEvent.KEYCODE_ENTER:
                            onCloseKeyBoard();
                            return false;
                    }
                }
                return false;
            }
        });

        spanTextView.setOnKeyPreImeListener(new LinedEditText.OnKeyPreImeListener() {
            @Override
            public void onKeyPreIme(int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        if (isKeyboardInput()) {
                            onCloseKeyBoard();
                        }
                        break;
                }
            }
        });

        spanTextView.setInputConnectionListener(new LinedEditText.InputConnectionListener() {
            @Override
            public void commitText(CharSequence text, int newCursorPosition) {
                if (isBuildingSpan()) {
                    return;
                }
                int width = (int) spanTextView.getPaint().measureText(text.toString());
                setKeyboardInput(true);
                spanTextHandler.buildTextShape(text.toString(), width, getSpanTextFontHeight());
            }
        });

        spanTextView.post(new Runnable() {
            @Override
            public void run() {
                updateLineLayoutArgs();
                switchScribbleMode(isLineLayoutMode());
            }
        });
    }

    private void updateLineLayoutArgs() {
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
        getNoteViewHelper().setLineLayoutArgs(args);
    }

    private void afterDrawLineLayoutShapes(final List<Shape> lineLayoutShapes) {
        if (checkShapesOutOfRange(lineLayoutShapes)) {
            lineLayoutShapes.clear();
            showOutOfRangeTips();
            syncWithCallback(true, !isKeyboardInput(), new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    loadLineLayoutShapes();
                }
            });
            setBuildingSpan(false);
            return;
        }

        updateLineLayoutCursor();
        final DocumentFlushAction<BaseScribbleActivity> action = new DocumentFlushAction<>(lineLayoutShapes,
                true,
                !isKeyboardInput(),
                shapeDataInfo.getDrawingArgs());
        action.execute(ScribbleActivity.this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setBuildingSpan(false);
            }
        });
    }

    private void updateLineLayoutCursor() {
        int pos = spanTextView.getSelectionStart();
        Layout layout = spanTextView.getLayout();
        int line = layout.getLineForOffset(pos);
        int x = (int) layout.getPrimaryHorizontal(pos);
        LineLayoutArgs args = getNoteViewHelper().getLineLayoutArgs();
        int top = args.getLineTop(line);
        int bottom = args.getLineBottom(line);
        getNoteViewHelper().updateCursorShape(x, top + 1 , x, bottom);
    }

    private void showOutOfRangeTips() {
        ToastUtils.showToast(NoteApplication.getInstance(), getString(R.string.shape_out_of_range));
    }

    private boolean checkShapesOutOfRange(List<Shape> shapes) {
        if (shapes == null || shapes.size() == 0) {
            return false;
        }
        for (Shape shape : shapes) {
            TouchPointList pointList = shape.getPoints();
            if (!getNoteViewHelper().checkTouchPointList(pointList)) {
                return true;
            }
        }
        return false;
    }

    private void onCloseKeyBoard() {
        setKeyboardInput(false);
        syncWithCallback(false, true, null);
    }

    private boolean digestionSpanMenu(final @ScribbleMenuCategory.ScribbleMenuCategoryDef
                                          int category) {
        switch (category) {
            case ScribbleMenuCategory.DELETE:
                onDelete(true);
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

    private void clearLineLayoutMode() {
        spanTextHandler.clear();
        spanTextView.setText("");
    }

    private void switchScribbleMode(boolean isLineLayoutMode) {
        cleanUpAllPopMenu();
        hideSoftInput();
        getNoteViewHelper().clearPageUndoRedo(this);
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
                GotoTargetPageAction<ScribbleActivity> action = new GotoTargetPageAction<>(logicalIndex, false);
                action.execute(ScribbleActivity.this);
            }

            @Override
            public void done(boolean isValueChange, int newValue) {
                GotoTargetPageAction<ScribbleActivity> action =
                        new GotoTargetPageAction<>(isValueChange ? newValue : originalVisualPageIndex -1,true);
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

    private void onExport(final boolean exportCurPage) {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new ExportNoteAction<>(ScribbleActivity.this,
                        shapeDataInfo.getDocumentUniqueId(),
                        shapeDataInfo.getPageNameList().getPageNameList(),
                        noteTitle,
                        exportCurPage,
                        shapeDataInfo.getCurrentPageIndex()).execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        new GotoTargetPageAction<ScribbleActivity>(shapeDataInfo.getCurrentPageIndex(),true).execute(ScribbleActivity.this);
                    }
                });
            }
        });

    }

    private void onExportEditedPic() {
        syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                new ExportEditedPicAction<>(ScribbleActivity.this,
                        shapeDataInfo.getDocumentUniqueId(),
                        shapeDataInfo.getPageNameList().getPageNameList().get(0), editPictUri).
                        execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        syncWithCallback(false, false, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                Handler handler = new Handler(getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBroadcast(new Intent(DeviceReceiver.SYSTEM_UI_SCREEN_SHOT_END_ACTION)
                                                .putExtra(Constant.RELOAD_DOCUMENT_TAG, true));
                                    }
                                }, 2000);
                                ScribbleActivity.this.finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void showExportMenu() {
        hideSoftInput();
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getSupportActionBar().openOptionsMenu();
            }
        });
    }

    private void onSave(final boolean finishAfterSave) {
        hideSoftInput();
        getNoteViewHelper().flushTouchPointList();
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (isPictureEditMode){
                    saveEditPic();
                }else {
                    saveDocument(finishAfterSave);
                }
            }
        });
    }

    private void onRedo() {
        syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final RedoAction<ScribbleActivity> action = new RedoAction<>();
                action.execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, true);
                        loadLineLayoutShapes();
                    }
                });
            }
        });
    }

    private void onUndo() {
        syncWithCallback(false, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final UndoAction<ScribbleActivity> action = new UndoAction<>();
                action.execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onRequestFinished((BaseNoteRequest) request, true);
                        loadLineLayoutShapes();
                    }
                });
            }
        });
    }

    private void onDelete(boolean resume) {
        String groupId = spanTextHandler.getLastGroupId();
        if (StringUtils.isNullOrEmpty(groupId)) {
            syncWithCallback(false, resume, null);
            return;
        }
        RemoveByGroupIdAction<BaseScribbleActivity> removeByPointListAction = new
                RemoveByGroupIdAction<>(groupId, resume);
        removeByPointListAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                loadLineLayoutShapes();
            }
        });
    }

    private void onSpace() {
        spanTextHandler.buildSpaceShape(SpanTextHandler.SPACE_WIDTH, getSpanTextFontHeight());
    }

    private void loadLineLayoutShapes() {
        if (isLineLayoutMode()) {
            spanTextHandler.loadPageShapes();
        }
    }

    private int getSpanTextFontHeight() {
        float bottom = spanTextView.getPaint().getFontMetrics().bottom;
        float top = spanTextView.getPaint().getFontMetrics().top;
        int height = (int) Math.ceil(bottom - top - 2 * ShapeSpan.SHAPE_SPAN_MARGIN);
        return height;
    }

    private void onEnter() {
        float spaceWidth = (int) spanTextView.getPaint().measureText(SpannableRequest.SPACE_SPAN);
        int pos = spanTextView.getSelectionStart();
        Layout layout = spanTextView.getLayout();
        int line = layout.getLineForOffset(pos);
        if (line == (getNoteViewHelper().getLineLayoutArgs().getLineCount() - 1)) {
            showOutOfRangeTips();
            syncWithCallback(true,true, null);
            return;
        }
        int width = spanTextView.getMeasuredWidth();
        float x = layout.getPrimaryHorizontal(pos) - spaceWidth;
        x = x >= width ? 0 : x;
        if (isBuildingSpan()) {
            return;
        }
        spanTextHandler.buildSpaceShape((int) Math.ceil(spanTextView.getMeasuredWidth() - x) - 2 * ShapeSpan.SHAPE_SPAN_MARGIN, getSpanTextFontHeight());
    }

    private void onKeyboard() {
        setKeyboardInput(true);
        syncWithCallback(false, false, null);
        InputMethodManager inputManager = (InputMethodManager)spanTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(spanTextView, 0);
    }

    private void hideSoftInput() {
        setKeyboardInput(false);
        InputMethodManager inputManager = (InputMethodManager)spanTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(spanTextView.getWindowToken(), 0);
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
                            syncWithCallback(true, shouldResume(), null);
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
            case ScribbleSubMenuID.BG_TABLE_GRID:
                onBackgroundChanged(NoteBackgroundType.TABLE);
                break;
            case ScribbleSubMenuID.BG_LINE_COLUMN:
                onBackgroundChanged(NoteBackgroundType.COLUMN);
                break;
            case ScribbleSubMenuID.BG_LEFT_GRID:
                onBackgroundChanged(NoteBackgroundType.LEFT_GRID);
                break;
            case ScribbleSubMenuID.BG_GRID_5_5:
                onBackgroundChanged(NoteBackgroundType.GRID_5_5);
                break;
            case ScribbleSubMenuID.BG_GRID_POINT:
                onBackgroundChanged(NoteBackgroundType.GRID_POINT);
                break;
            case ScribbleSubMenuID.BG_LINE_1_6:
                onBackgroundChanged(NoteBackgroundType.LINE_1_6);
                break;
            case ScribbleSubMenuID.BG_LINE_2_0:
                onBackgroundChanged(NoteBackgroundType.LINE_2_0);
                break;
            case ScribbleSubMenuID.BG_CALENDAR:
                onBackgroundChanged(NoteBackgroundType.CALENDAR);
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
            case ScribbleSubMenuID.TRIANGLE_45_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_TRIANGLE_45, null);
                break;
            case ScribbleSubMenuID.TRIANGLE_60_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_TRIANGLE_60, null);
                break;
            case ScribbleSubMenuID.TRIANGLE_90_STYLE:
                onNoteShapeChanged(true, false, ShapeFactory.SHAPE_TRIANGLE_90, null);
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
        customLineWidth.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                syncWithCallback(true, true, null);
            }
        });
    }

    private void onBackgroundChanged(int type) {
        if (type == NoteBackgroundType.FILE){
            if (!isPictureEditMode){
                return;
            }
            setBackgroundType(type);
            final NoteSetBackgroundAsLocalFileAction<ScribbleActivity> changeBGAction =
                    new NoteSetBackgroundAsLocalFileAction<>(FileUtils.getRealFilePathFromUri(this, editPictUri),
                            !getNoteViewHelper().inUserErasing());
            changeBGAction.execute(ScribbleActivity.this, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(FileUtils.getRealFilePathFromUri(ScribbleActivity.this, editPictUri), options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;
                    getNoteViewHelper().setCustomLimitRect(BitmapUtils.getScaleInSideAndCenterRect(
                            surfaceView.getHeight(), surfaceView.getWidth(), imageHeight, imageWidth, false));
                }
            });
        }

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
                new NoteBackgroundChangeAction<>(getBackgroundType(), shouldResume());
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
        if (!NoteAppConfig.sharedInstance(this).useEduConfig()) {
            adapter.addObject(createFunctionItem(R.drawable.ic_shape, ScribbleMenuCategory.PEN_STYLE));
        }
        if (!isPictureEditMode) {
            adapter.addObject(createFunctionItem(R.drawable.ic_template, ScribbleMenuCategory.BG));
        }
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
        if (isPictureEditMode){
            onBackgroundChanged(NoteBackgroundType.FILE);
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

    private void saveEditPic() {
        final OnyxAlertDialog saveEditPicDialog = new OnyxAlertDialog();
        saveEditPicDialog.setParams(new OnyxAlertDialog.Params().setTittleString(getString(R.string.save))
                .setAlertMsgString(getString(R.string.save_and_exit))
                .setCanceledOnTouchOutside(false)
                .setEnableNeutralButton(true)
                .setNeutralButtonText(getString(R.string.discard))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onExportEditedPic();
                    }
                })
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveEditPicDialog.dismiss();
                        syncWithCallback(true, true, null);
                    }
                })
                .setNeutralAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveEditPicDialog.dismiss();
                        Handler handler = new Handler(getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendBroadcast(new Intent(DeviceReceiver.SYSTEM_UI_SCREEN_SHOT_END_ACTION)
                                        .putExtra(Constant.RELOAD_DOCUMENT_TAG, true));
                            }
                        }, 2000);
                        ScribbleActivity.this.finish();
                    }
                }));
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                saveEditPicDialog.show(getFragmentManager(),"SaveEditPicDialog");
            }
        });
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
                DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, finishAfterSave, shouldResume());
        saveAction.execute(ScribbleActivity.this, null);
    }

    private void saveExistingNoteDocument(final boolean finishAfterSave) {
        String documentUniqueId = shapeDataInfo.getDocumentUniqueId();
        if (StringUtils.isNullOrEmpty(documentUniqueId)) {
            return;
        }
        final DocumentSaveAction<ScribbleActivity> saveAction = new
                DocumentSaveAction<>(documentUniqueId, noteTitle, finishAfterSave, shouldResume());
        saveAction.execute(ScribbleActivity.this, null);
    }

    private void onNoteShapeChanged(boolean render, boolean resume, int type, BaseCallback callback) {
        setCurrentShapeType(type);
        syncWithCallback(render, resume, callback);
    }

    private void onStrokeWidthChanged(float width, BaseCallback callback) {
        if (shapeDataInfo.isInUserErasing()) {
            setCurrentShapeType(ShapeFactory.SHAPE_PENCIL_SCRIBBLE);
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
            ClearAllFreeShapesAction<ScribbleActivity> action = new ClearAllFreeShapesAction<>(resume);
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

    @Override
    protected void reloadLineLayoutData() {
        if (!isLineLayoutMode()) {
            return;
        }

        clearLineLayoutMode();
        loadLineLayoutShapes();
    }

    public boolean isKeyboardInput() {
        return isKeyboardInput;
    }

    public void setKeyboardInput(boolean keyboardInput) {
        isKeyboardInput = keyboardInput;
    }

    @Override
    protected void onStartDrawing() {
        spanTextHandler.removeSpanRunnable();
    }

    public boolean isBuildingSpan() {
        return buildingSpan;
    }

    public void setBuildingSpan(boolean buildingSpan) {
        this.buildingSpan = buildingSpan;
    }

    @Override
    protected void onScreenShotStart() {
        onSave(false);
        super.onScreenShotStart();
    }

    @Override
    protected void onScreenShotEnd(boolean reloadDocument) {
        super.onScreenShotEnd(reloadDocument);
        if (reloadDocument && !TextUtils.isEmpty(uniqueID)) {
            handleDocumentEdit(uniqueID, parentID);
        }
    }

    private boolean processDirectMenuItemClick(int category) {
        switch (category) {
            case ScribbleMenuCategory.ERASER:
                showEraseAllConfirmDialog();
                return true;
        }
        return false;
    }

    private void showEraseAllConfirmDialog() {
        final OnyxCustomDialog customDialog = OnyxCustomDialog.getConfirmDialog(this,
                getApplicationContext().getString(R.string.erase_all_tip),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onEraseClicked(false);
                        dialog.dismiss();
                    }
                }, null);
        customDialog.show();
    }
}
