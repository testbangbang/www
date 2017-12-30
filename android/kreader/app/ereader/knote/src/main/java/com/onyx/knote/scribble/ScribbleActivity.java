package com.onyx.knote.scribble;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.api.event.BuildLineBreakShapeEvent;
import com.onyx.android.sdk.scribble.api.event.BuildTextShapeEvent;
import com.onyx.android.sdk.scribble.api.event.DeleteSpanEvent;
import com.onyx.android.sdk.scribble.api.event.LoadSpanPageShapesEvent;
import com.onyx.android.sdk.scribble.api.event.RawDataReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.SpanFinishedEvent;
import com.onyx.android.sdk.scribble.api.event.SpanTextShowOutOfRangeEvent;
import com.onyx.android.sdk.scribble.api.event.UpdateLineLayoutArgsEvent;
import com.onyx.android.sdk.scribble.api.event.UpdateLineLayoutCursorEvent;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.scribble.view.LinedEditText;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.data.MenuId;
import com.onyx.android.sdk.ui.data.MenuItem;
import com.onyx.android.sdk.ui.data.MenuManager;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.android.sdk.ui.dialog.DialogSetValue;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.knote.BR;
import com.onyx.knote.HandlerManager;
import com.onyx.knote.NoteApplication;
import com.onyx.knote.R;
import com.onyx.knote.actions.common.CheckNoteNameLegalityAction;
import com.onyx.knote.actions.scribble.DocumentDiscardAction;
import com.onyx.knote.actions.scribble.DocumentFlushAction;
import com.onyx.knote.actions.scribble.GotoTargetPageAction;
import com.onyx.knote.actions.scribble.RenderInBackgroundAction;
import com.onyx.knote.data.ScribbleAction;
import com.onyx.knote.data.ScribbleFunctionBarMenuID;
import com.onyx.knote.data.ScribbleSubMenuID;
import com.onyx.knote.databinding.ActivityScribbleBinding;
import com.onyx.knote.handler.HandlerArgs;
import com.onyx.knote.receiver.DeviceReceiver;
import com.onyx.knote.scribble.event.ChangeScribbleModeEvent;
import com.onyx.knote.scribble.event.CustomWidthEvent;
import com.onyx.knote.scribble.event.GoToTargetPageEvent;
import com.onyx.knote.scribble.event.HandlerActivateEvent;
import com.onyx.knote.scribble.event.QuitScribbleEvent;
import com.onyx.knote.scribble.event.RequestInfoUpdateEvent;
import com.onyx.knote.scribble.event.ShowInputKeyBoardEvent;
import com.onyx.knote.scribble.event.ShowSubMenuEvent;
import com.onyx.knote.scribble.event.SpanLineBreakerEvent;
import com.onyx.knote.scribble.event.UpdateScribbleTitleEvent;
import com.onyx.knote.ui.HideSubMenuEvent;
import com.onyx.knote.ui.SubMenuClickEvent;
import com.onyx.knote.ui.dialog.DialogNoteNameInput;
import com.onyx.knote.util.Constant;
import com.onyx.knote.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ScribbleActivity extends OnyxAppCompatActivity{
    private static final String TAG = ScribbleActivity.class.getSimpleName();
    ActivityScribbleBinding mBinding;
    ScribbleViewModel mViewModel;
    MenuManager menuManager;
    protected SurfaceHolder.Callback surfaceCallback;
    DeviceReceiver deviceReceiver = new DeviceReceiver();
    NoteManager noteManager;
    HandlerManager handlerManager;
    private String docUniqueID;
    private @ScribbleAction.ScribbleActionDef
    int mScribbleAction;
    private EditMode currentEditMode = EditMode.NormalMode;
    private Uri editPictUri;

    private enum EditMode {NormalMode, PicEditMode}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        noteManager = NoteApplication.getInstance().getNoteManager();
        mViewModel = new ScribbleViewModel(this);
        handlerManager = new HandlerManager(mViewModel);
        // Link View and ViewModel
        mBinding.setViewModel(mViewModel);
        initSpanTextView();
        checkEditMode();
        initMenu();
    }

    private void checkEditMode() {
        Intent editIntent = getIntent();
        if (TextUtils.isEmpty(editIntent.getAction())) {
            return;
        }
        switch (editIntent.getAction()) {
            case Intent.ACTION_EDIT:
                currentEditMode = EditMode.PicEditMode;
                editPictUri = editIntent.getData();
                // TODO: 2017/9/4 for change menuManager
//                mBinding.pageCountControl.setVisibility(View.GONE);
//                mBinding.pageIndicator.setVisibility(View.GONE);
                break;
        }
    }

    private void initMenu() {
        menuManager = new MenuManager();
        menuManager.addMainMenu(mBinding.layoutFooter,
                noteManager.getEventBus(),
                R.layout.scribble_main_menu,
                BR.item,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                MenuItem.createVisibleMenus(handlerManager.getActiveProvider().buildMainMenuIds()));
        menuManager.addToolbarMenu(mBinding.toolMenu,
                noteManager.getEventBus(),
                R.layout.scribble_toolbar_menu,
                BR.item,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT),
                MenuItem.createVisibleMenus(handlerManager.getActiveProvider().buildToolBarMenuIds()));
        mBinding.subMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSubMenu();
            }
        });
    }

    @Subscribe
    public void onRequestFinished(RequestInfoUpdateEvent event) {
        if (!event.getRequest().isAbort() && event.getThrowable() == null) {
            int currentPage = event.getShapeDataInfo().getHumanReadableCurPageIndex();
            int totalPage = event.getShapeDataInfo().getPageCount();
            menuManager.getMainMenu().setText(MenuId.PAGE, getString(R.string.page_count, currentPage, totalPage));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteManager.registerEventBus(this);
        deviceReceiver.registerReceiver(this);
    }

    @Override
    protected void onResume() {
        addSurfaceViewCallback();
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        noteManager.sync(false, false);
        DeviceUtils.setFullScreenOnResume(this, false);
        removeSurfaceViewCallback();
    }

    @Override
    protected void onStop() {
        deviceReceiver.unregisterReceiver(this);
        handlerManager.quit();
        noteManager.unregisterEventBus(this);
        noteManager.quit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        bottomMenu.onDestroy();
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    private void handleIntent(Intent intent) {
        mScribbleAction =
                intent.getIntExtra(Constant.SCRIBBLE_ACTION_TAG,
                        currentEditMode == EditMode.PicEditMode ? ScribbleAction.EDIT : ScribbleAction.INVALID);
        if (!ScribbleAction.isValidAction(mScribbleAction)) {
            //TODO:direct call finish here.because we don't want incorrect illegal call.
            finish();
            return;
        }
        docUniqueID = intent.getStringExtra(Constant.NOTE_ID_TAG);
        String parentID = intent.getStringExtra(Constant.NOTE_PARENT_ID_TAG);
        BaseCallback callback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                switch (currentEditMode) {
                    case PicEditMode:
                        handlerManager.changeScribbleMode(ScribbleMode.MODE_PIC_EDIT, new HandlerArgs().setEditPicUri(editPictUri));
                        break;
                    case NormalMode:
                        handlerManager.changeScribbleMode(ScribbleMode.MODE_NORMAL_SCRIBBLE);
                        break;
                }
            }
        };

        mViewModel.start(docUniqueID, parentID, mScribbleAction, callback);
    }

    private void addSurfaceViewCallback() {
        mBinding.noteView.getHolder().addCallback(getSurfaceCallback());
    }

    private void removeSurfaceViewCallback() {
        if (surfaceCallback != null) {
            mBinding.noteView.getHolder().removeCallback(surfaceCallback);
        }
    }

    protected SurfaceHolder.Callback getSurfaceCallback() {
        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    noteManager.clearSurfaceView(mBinding.noteView);
                    noteManager.setView(mBinding.noteView);
                    handleIntent(getIntent());
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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

    private void onDocumentClose() {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                handlerManager.saveDocument(true, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (!request.isAbort() && e == null) {
                            if (currentEditMode == EditMode.PicEditMode) {
                                Handler handler = new Handler(getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBroadcast(new Intent(DeviceReceiver.SYSTEM_UI_SCREEN_SHOT_END_ACTION)
                                                .putExtra(Constant.RELOAD_DOCUMENT_TAG, true));
                                    }
                                }, 2000);
                            }
                            ScribbleActivity.super.onBackPressed();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        //TODO:need back key to dismiss sub menuManager first or direct exit even sub menuManager showing?
        if (!hideSubMenu()) {
            switch (mScribbleAction) {
                case ScribbleAction.CREATE:
                    saveNewNoteDocument();
                    break;
                case ScribbleAction.EDIT:
                    switch (currentEditMode) {
                        case NormalMode:
                            onDocumentClose();
                            break;
                        case PicEditMode:
                            saveEditPic();
                            break;
                    }
            }
        }
    }

    private void saveEditPic() {
        final OnyxAlertDialog saveEditPicDialog = getExportedPicDialog();
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                saveEditPicDialog.show(getFragmentManager(), "SaveEditPicDialog");
            }
        });
    }

    private OnyxAlertDialog getExportedPicDialog() {
        final OnyxAlertDialog dialog = new OnyxAlertDialog();
        dialog.setParams(new OnyxAlertDialog.Params().setTittleString(getString(R.string.save))
                .setAlertMsgString(getString(R.string.save_and_exit))
                .setCanceledOnTouchOutside(false)
                .setEnableNeutralButton(true)
                .setNeutralButtonText(getString(R.string.discard))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        onDocumentClose();
                    }
                })
                .setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        afterDialogDismiss();
                    }
                })
                .setNeutralAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
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
        return dialog;
    }

    private void saveNewNoteDocument() {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.save_note));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, mViewModel.mNoteTitle.get());
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, true);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(final String input) {
                final CheckNoteNameLegalityAction action = new
                        CheckNoteNameLegalityAction(input, mViewModel.getParentUniqueID(),
                        NoteModel.TYPE_DOCUMENT, true, true);
                action.execute(noteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            mViewModel.mNoteTitle.set(input);
                            onDocumentClose();
                        } else {
                            Utils.showNoteNameIllegal(ScribbleActivity.this, getFragmentManager(), true);
                        }
                    }
                });
                return true;
            }

            @Override
            public void onCancelAction() {
                dialogNoteNameInput.dismiss();
                afterDialogDismiss();
            }

            @Override
            public void onDiscardAction() {
                dialogNoteNameInput.dismiss();
                final DocumentDiscardAction discardAction = new DocumentDiscardAction(null);
                discardAction.execute(noteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        ScribbleActivity.super.onBackPressed();
                    }
                });
            }
        });

        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogNoteNameInput.show(getFragmentManager());
            }
        });
    }

    private void initSpanTextView() {
        mBinding.spanTextView.setInputConnectionListener(new LinedEditText.InputConnectionListener() {
            @Override
            public void commitText(CharSequence text, int newCursorPosition) {
                buildTextShapeImpl(text.toString());
            }
        });
        mBinding.spanTextView.setOnKeyPreImeListener(new LinedEditText.OnKeyPreImeListener() {
            @Override
            public void onKeyPreIme(int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        if (mViewModel.isKeyboardInput()) {
                            onCloseKeyBoard();
                        }
                        break;
                }
            }
        });
        mBinding.spanTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DEL:
                            mViewModel.setKeyboardInput(true);
                            noteManager.post(new DeleteSpanEvent(false));
                            return true;
                        case KeyEvent.KEYCODE_ENTER:
                            onCloseKeyBoard();
                            return false;
                        case KeyEvent.KEYCODE_1:
                        case KeyEvent.KEYCODE_2:
                        case KeyEvent.KEYCODE_3:
                        case KeyEvent.KEYCODE_4:
                        case KeyEvent.KEYCODE_5:
                        case KeyEvent.KEYCODE_6:
                        case KeyEvent.KEYCODE_7:
                        case KeyEvent.KEYCODE_8:
                        case KeyEvent.KEYCODE_9:
                        case KeyEvent.KEYCODE_0:
                            char displayLabel = event.getDisplayLabel();
                            buildTextShapeImpl(String.valueOf(displayLabel));
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void buildTextShapeImpl(String text) {
        if (mViewModel.isBuildingSpan()) {
            return;
        }
        mViewModel.setKeyboardInput(true);
        noteManager.post(new BuildTextShapeEvent(mBinding.spanTextView, text));
    }

    private void afterDrawLineLayoutShapes(final List<Shape> lineLayoutShapes) {
        if (noteManager.checkShapesOutOfRange(lineLayoutShapes)) {
            lineLayoutShapes.clear();
            showOutOfRangeTips();
            noteManager.syncWithCallback(true, !mViewModel.isKeyboardInput(), new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    noteManager.post(new LoadSpanPageShapesEvent());
                }
            });
            mViewModel.setBuildingSpan(false);
            return;
        }

        noteManager.post(new UpdateLineLayoutCursorEvent(mBinding.spanTextView));
        final DocumentFlushAction action = new DocumentFlushAction(lineLayoutShapes,
                true,
                !mViewModel.isKeyboardInput(),
                noteManager.getShapeDataInfo().getDrawingArgs());
        action.execute(noteManager, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mViewModel.setBuildingSpan(false);
            }
        });
    }

    private void onCloseKeyBoard() {
        mViewModel.setKeyboardInput(false);
        noteManager.sync(false, true);
    }

    private void showSubMenu(int parentId) {
        mBinding.subMenuLayout.removeAllViews();
        mBinding.subMenuLayout.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        menuManager.addSubMenu(mBinding.subMenuLayout,
                noteManager.getEventBus(),
                getSubLayoutId(parentId),
                BR.item,
                lp,
                getSubItems(parentId));
        menuManager.getSubMenu().
                unCheckAll().
                check(getChosenSubMenuId(parentId, noteManager.inSpanLayoutMode()));
    }

    private int getSubLayoutId(int parentId) {
        switch (parentId) {
            case MenuId.PEN_STYLE:
                return R.layout.pen_style_menu;
            case MenuId.BG:
                return R.layout.scribble_bg_menu;
            case MenuId.PEN_WIDTH:
                return R.layout.pen_width_menu;
            case MenuId.ERASER:
                return R.layout.scribble_erase_menu;
        }
        return R.layout.pen_style_menu;
    }

    private SparseArray<MenuItem> getSubItems(int parentId) {
        List<Integer> subMenuIds = handlerManager.getActiveProvider().buildSubMenuIds().get(parentId);
        return MenuItem.createVisibleMenus(subMenuIds, getResources().getInteger(R.integer.note_menu_columns));
    }

    public int getChosenSubMenuId(int mainMenuID, boolean isLineLayoutMode) {
        NoteManager manager = NoteApplication.getInstance().getNoteManager();
        int targetID = Integer.MIN_VALUE;
        switch (mainMenuID) {
            case ScribbleFunctionBarMenuID.ERASER:
            case ScribbleFunctionBarMenuID.PEN_STYLE:
                targetID = ScribbleSubMenuID.menuIdFromShapeType(manager.getShapeDataInfo().getCurrentShapeType());
                break;
            case ScribbleFunctionBarMenuID.BG:
                targetID = ScribbleSubMenuID.menuIdFromBg(isLineLayoutMode ?
                        manager.getShapeDataInfo().getLineLayoutBackground() : manager.getShapeDataInfo().getBackground());
                break;
            case ScribbleFunctionBarMenuID.PEN_WIDTH:
                targetID = ScribbleSubMenuID.menuIdFromStrokeWidth(manager.getShapeDataInfo().getStrokeWidth());
                break;
        }
        return targetID;
    }

    private boolean hideSubMenu() {
        mBinding.subMenuLayout.removeAllViews();
        mBinding.subMenuLayout.setVisibility(View.GONE);
        return false;
    }

    @Subscribe
    public void onQuitScribbleEvent(QuitScribbleEvent event) {
        onBackPressed();
    }

    @Subscribe
    public void onUpdateScribbleTitleEvent(UpdateScribbleTitleEvent event) {
        menuManager.getToolbarMenu().setText(MenuId.SCRIBBLE_TITLE, event.getTitle());
    }

    @Subscribe
    public void onHideSubMenuEvent(HideSubMenuEvent event) {
        hideSubMenu();
    }

    @Subscribe
    public void onSubMenuClickEvent(SubMenuClickEvent event) {
        handlerManager.handleSubMenuFunction(event.getMenuId());
        hideSubMenu();
    }

    private void showOutOfRangeTips() {
        ToastUtils.showToast(NoteApplication.getInstance(), "Out Of Range");
    }

    @Subscribe
    public void onRawDataReceived(RawDataReceivedEvent event) {
        if (!noteManager.inSpanLayoutMode()) {
            new RenderInBackgroundAction().execute(noteManager, null);
        }
    }

    @Subscribe
    public void onBuildLinerBreaker(SpanLineBreakerEvent event) {
        if (mViewModel.isBuildingSpan()) {
            return;
        }
        noteManager.post(new BuildLineBreakShapeEvent(mBinding.spanTextView));
    }

    @Subscribe
    public void onShowOutOfRangeEvent(SpanTextShowOutOfRangeEvent event) {
        showOutOfRangeTips();
    }

    @Subscribe
    public void onSpanFinished(final SpanFinishedEvent event) {
        if (event.getBuilder() == null) {
            return;
        }
        mViewModel.setBuildingSpan(true);
        mBinding.spanTextView.setText(event.getBuilder());
        mBinding.spanTextView.setSelection(event.getBuilder().length());
        mBinding.spanTextView.requestFocus();
        if (event.getLastShapeSpan() != null) {
            event.getLastShapeSpan().setCallback(new ShapeSpan.Callback() {
                @Override
                public void onFinishDrawShapes(List<Shape> shapes) {
                    afterDrawLineLayoutShapes(event.getSpanShapeList());
                }
            });
        } else {
            afterDrawLineLayoutShapes(event.getSpanShapeList());
        }
    }

    @Subscribe
    public void showSubMenu(final ShowSubMenuEvent event) {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showSubMenu(event.getFunctionBarMenuID());
            }
        });
    }

    @Subscribe
    public void showInputKeyboard(ShowInputKeyBoardEvent event) {
        mViewModel.setKeyboardInput(true);
        noteManager.syncWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mBinding.spanTextView.requestFocus();
                InputMethodUtils.showForcedInputKeyboard(ScribbleActivity.this, getCurrentFocus());
            }
        });
    }

    /**
     * When Changing Scribble Mode:
     * 1.change view visibility.
     * 2.update NoteManager Scribble Mode and clean undo/redo history.
     * 3.update LineLayoutArgs after mode change.
     * 4.hide all ime.
     * 5.finally change handler,different initial will place in handler activate.
     *
     * @param event
     */
    @Subscribe
    public void switchScribbleMode(ChangeScribbleModeEvent event) {
        hideSubMenu();
        mBinding.spanTextView.setVisibility(event.getTargetScribbleMode() ==
                ScribbleMode.MODE_SPAN_SCRIBBLE ? View.VISIBLE : View.GONE);
        noteManager.setCurrentScribbleMode(event.getTargetScribbleMode());
        noteManager.clearPageUndoRedo(ScribbleActivity.this);
        noteManager.clearShapeSelectRecord();
        if (noteManager.inSpanLayoutMode()) {
            mBinding.spanTextView.post(new Runnable() {
                @Override
                public void run() {
                    noteManager.post(new UpdateLineLayoutArgsEvent(mBinding.spanTextView));
                }
            });
        }
        InputMethodUtils.hideInputKeyboard(ScribbleActivity.this);
        handlerManager.changeScribbleMode(event.getTargetScribbleMode());
    }

    @Subscribe
    public void showCustomLineWidthDialog(CustomWidthEvent event) {
        final DialogCustomLineWidth customLineWidth = new DialogCustomLineWidth(ScribbleActivity.this,
                (int) noteManager.getShapeDataInfo().getStrokeWidth(),
                20, Color.BLACK, event.getDoneCallBack());
        customLineWidth.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                afterDialogDismiss();
            }
        });
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                customLineWidth.show();
            }
        });
    }

    @Subscribe
    public void onHandlerActivate(HandlerActivateEvent activateEvent) {
        menuManager.updateMainMenuDataSet(BR.item, noteManager.getEventBus(),
                MenuItem.createVisibleMenus(handlerManager.getActiveProvider().buildMainMenuIds()));
        menuManager.updateToolbarMenuDataSet(BR.item, noteManager.getEventBus(),
                MenuItem.createVisibleMenus(handlerManager.getActiveProvider().buildToolBarMenuIds()));
        menuManager.getToolbarMenu().setText(MenuId.SCRIBBLE_TITLE, activateEvent.getNoteTitle());
        menuManager.getMainMenu().setText(MenuId.PAGE,
                getString(R.string.page_count, activateEvent.getCurrentPage(), activateEvent.getTotalPage()));
    }

    @Subscribe
    public void showGotoPageDialog(GoToTargetPageEvent event) {
        final int originalVisualPageIndex = noteManager.getShapeDataInfo().getHumanReadableCurPageIndex();
        final DialogSetValue dlg = new DialogSetValue();
        Bundle args = new Bundle();
        args.putString(DialogSetValue.ARGS_DIALOG_TITLE, getString(R.string.go_to_page));
        args.putString(DialogSetValue.ARGS_VALUE_TITLE, getString(R.string.current_page));
        args.putInt(DialogSetValue.ARGS_CURRENT_VALUE, originalVisualPageIndex);
        args.putInt(DialogSetValue.ARGS_MAX_VALUE, noteManager.getShapeDataInfo().getPageCount());
        args.putInt(DialogSetValue.ARGS_MIN_VALUE, 1);
        dlg.setArguments(args);
        dlg.setCallback(new DialogSetValue.DialogCallback() {
            @Override
            public void valueChange(int newValue) {
                int logicalIndex = newValue - 1;
                GotoTargetPageAction action = new GotoTargetPageAction(logicalIndex, false);
                action.execute(noteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest)request;
                        noteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
                    }
                });
            }

            @Override
            public void done(boolean isValueChange, int newValue) {
                GotoTargetPageAction action =
                        new GotoTargetPageAction(isValueChange ? newValue : originalVisualPageIndex -1,true);
                action.execute(noteManager, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null) {
                            AsyncBaseNoteRequest noteRequest = (AsyncBaseNoteRequest)request;
                            noteManager.post(new RequestInfoUpdateEvent(noteRequest.getShapeDataInfo(), request, e));
                        }
                    }
                });
            }

            @Override
            public void dismiss() {
                afterDialogDismiss();
            }
        });
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dlg.show(getFragmentManager());
            }
        });
    }

    private void afterDialogDismiss() {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                noteManager.sync(true, !noteManager.inUserErasing());
            }
        }, 500);
    }
}