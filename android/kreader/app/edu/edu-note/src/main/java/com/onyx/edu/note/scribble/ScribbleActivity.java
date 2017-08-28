package com.onyx.edu.note.scribble;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeSpan;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.DialogCustomLineWidth;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.edu.note.HandlerManager;
import com.onyx.edu.note.NoteApplication;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.R;
import com.onyx.edu.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.edu.note.actions.scribble.DocumentDiscardAction;
import com.onyx.edu.note.actions.scribble.DocumentFlushAction;
import com.onyx.edu.note.actions.scribble.RenderInBackgroundAction;
import com.onyx.edu.note.data.ScribbleAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleFunctionMenuIDType;
import com.onyx.android.sdk.scribble.data.ScribbleMode;
import com.onyx.edu.note.databinding.ActivityScribbleBinding;
import com.onyx.edu.note.databinding.ScribbleFunctionItemBinding;
import com.onyx.edu.note.receiver.DeviceReceiver;
import com.onyx.edu.note.scribble.event.ChangeScribbleModeEvent;
import com.onyx.edu.note.scribble.event.CustomWidthEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawDataReceivedEvent;
import com.onyx.edu.note.scribble.event.ShowInputKeyBoardEvent;
import com.onyx.edu.note.scribble.event.ShowSubMenuEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.SpanFinishedEvent;
import com.onyx.edu.note.scribble.event.SpanLineBreakerEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.SpanTextShowOutOfRangeEvent;
import com.onyx.edu.note.scribble.view.ScribbleSubMenu;
import com.onyx.edu.note.ui.PageAdapter;
import com.onyx.edu.note.ui.dialog.DialogNoteNameInput;
import com.onyx.android.sdk.scribble.view.LinedEditText;
import com.onyx.edu.note.util.Constant;
import com.onyx.edu.note.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.List;

public class ScribbleActivity extends OnyxAppCompatActivity implements ScribbleNavigator, ScribbleItemNavigator {
    private static final String TAG = ScribbleActivity.class.getSimpleName();
    ActivityScribbleBinding mBinding;
    ScribbleViewModel mViewModel;
    ScribbleFunctionAdapter mFunctionBarAdapter, mToolBarAdapter;
    protected SurfaceHolder.Callback surfaceCallback;
    DeviceReceiver deviceReceiver = new DeviceReceiver();
    NoteManager noteManager;
    HandlerManager handlerManager;
    ScribbleSubMenu mSubMenu;
    private @ScribbleAction.ScribbleActionDef int mScribbleAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        noteManager = NoteApplication.getInstance().getNoteManager();
        mViewModel = new ScribbleViewModel(this);
        mViewModel.setNavigator(this);
        // Link View and ViewModel
        mBinding.setViewModel(mViewModel);
        initRecyclerView();
        initSpanTextView();
        buildSubMenu();
        handlerManager = new HandlerManager(this, mViewModel);
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
        noteManager.unregisterEventBus(this);
        noteManager.quit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    private void buildSubMenu() {
        mSubMenu = new ScribbleSubMenu(this,
                mBinding.mainLayout, new ScribbleSubMenu.Callback() {
            @Override
            public void onLayoutStateChanged() {

            }

            @Override
            public void onCancel() {
                noteManager.sync(true, true);
            }
        }, R.id.divider);
    }

    private void handleIntent(Intent intent) {
        mScribbleAction =
                intent.getIntExtra(Constant.SCRIBBLE_ACTION_TAG, ScribbleAction.INVALID);
        if (!ScribbleAction.isValidAction(mScribbleAction)) {
            //TODO:direct call finish here.because we don't want incorrect illegal call.
            finish();
            return;
        }
        String uniqueID = intent.getStringExtra(Constant.NOTE_ID_TAG);
        String parentID = intent.getStringExtra(Constant.NOTE_PARENT_ID_TAG);
        BaseCallback callback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                handlerManager.changeScribbleMode(ScribbleMode.MODE_NORMAL_SCRIBBLE);
            }
        };
        mViewModel.start(uniqueID, parentID, mScribbleAction, callback);
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
                            handlerManager.quit();
                            ScribbleActivity.super.onBackPressed();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        //TODO:need back key to dismiss sub menu first or direct exit even sub menu showing?
        if (!hideSubMenu()) {
            if (mScribbleAction == ScribbleAction.CREATE) {
                saveNewNoteDocument();
            } else {
                onDocumentClose();
            }
        }
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
                Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        noteManager.sync(true, !noteManager.inUserErasing());
                    }
                }, 500);
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
                            noteManager.deleteSpan(false);
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
        noteManager.buildTextShape(text, mBinding.spanTextView);
    }

    private void initRecyclerView() {
        mBinding.functionRecyclerView.setLayoutManager(new DisableScrollGridManager(this));
        mBinding.toolBarRecyclerView.setLayoutManager(new DisableScrollGridManager(this));
        mBinding.toolBarRecyclerView.setHasFixedSize(true);
        mBinding.functionRecyclerView.setHasFixedSize(true);
        buildBarIconAdapter();
        mBinding.functionRecyclerView.setAdapter(mFunctionBarAdapter);
        mBinding.toolBarRecyclerView.setAdapter(mToolBarAdapter);
    }

    private void buildBarIconAdapter() {
        mFunctionBarAdapter = new ScribbleFunctionAdapter(this, ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU);
        mToolBarAdapter = new ScribbleFunctionAdapter(this, ScribbleFunctionMenuIDType.TOOL_BAR_MENU);
    }

    private void afterDrawLineLayoutShapes(final List<Shape> lineLayoutShapes) {
        if (noteManager.checkShapesOutOfRange(lineLayoutShapes)) {
            lineLayoutShapes.clear();
            showOutOfRangeTips();
            noteManager.syncWithCallback(true, !mViewModel.isKeyboardInput(), new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    loadLineLayoutShapes();
                }
            });
            mViewModel.setBuildingSpan(false);
            return;
        }

        noteManager.updateLineLayoutCursor(mBinding.spanTextView);
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

    private void loadLineLayoutShapes() {
        if (noteManager.inSpanScribbleMode()) {
            noteManager.loadPageShapes();
        }
    }

    private void onCloseKeyBoard() {
        mViewModel.setKeyboardInput(false);
        noteManager.sync(false, true);
    }

    private void showSubMenu(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int mainMenuID) {
        mSubMenu.show(mainMenuID, mViewModel.getSubMenuIDList(mainMenuID), noteManager.inSpanScribbleMode());
    }

    private boolean hideSubMenu() {
        if (mSubMenu != null && mSubMenu.isShow()) {
            mSubMenu.dismiss(true);
            return true;
        }
        return false;
    }

    @Override
    public void onFunctionBarMenuFunctionItem(final int mainMenuID) {
        Log.e(TAG, "onFunctionBarMenuFunctionItem: " + mainMenuID);
        handlerManager.handleFunctionBarMenuFunction(mainMenuID);
    }

    @Override
    public void onSubMenuFunctionItem(int subMenuID) {
        Log.e(TAG, "onSubMenuFunctionItem: " + subMenuID);
        mSubMenu.dismiss(false);
        handlerManager.handleSubMenuFunction(subMenuID);
    }

    @Override
    public void onToolBarMenuFunctionItem(int toolBarMenuID) {
        Log.e(TAG, "onToolBarMenuFunctionItem: " + toolBarMenuID);
        handlerManager.handleToolBarMenuFunction(toolBarMenuID);
    }

    private void showOutOfRangeTips() {
        ToastUtils.showToast(NoteApplication.getInstance(), "Out Of Range");
    }

    @Subscribe
    public void onRawDataReceived(RawDataReceivedEvent event) {
        if (!noteManager.inSpanScribbleMode()) {
            new RenderInBackgroundAction().execute(noteManager, null);
        }
    }

    @Subscribe
    public void onBuildLinerBreaker(SpanLineBreakerEvent event){
        if (mViewModel.isBuildingSpan()){
            return;
        }
        noteManager.buildLineBreakShape(mBinding.spanTextView);
    }

    @Subscribe
    public void onShowOutOfRangeEvent(SpanTextShowOutOfRangeEvent event){
        showOutOfRangeTips();
    }

    @Subscribe
    public void onSpanFinished(final SpanFinishedEvent event){
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
        }else {
            afterDrawLineLayoutShapes(event.getSpanShapeList());
        }
    }

    @Subscribe
    public void showSubMenu(final ShowSubMenuEvent event){
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showSubMenu(event.getFunctionBarMenuID());
            }
        });
    }

    @Subscribe
    public void showInputKeyboard(ShowInputKeyBoardEvent event){
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
        if (noteManager.inSpanScribbleMode()) {
            mBinding.spanTextView.post(new Runnable() {
                @Override
                public void run() {
                    noteManager.updateLineLayoutArgs(mBinding.spanTextView);
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
                noteManager.sync(true, true);
            }
        });
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                customLineWidth.show();
            }
        });
    }

    public static class ScribbleFunctionAdapter extends PageAdapter<ScribbleFunctionItemViewHolder, Integer, ScribbleFunctionItemViewModel> {
        private ScribbleActivity mItemNavigator;
        private LayoutInflater mLayoutInflater;
        private final @ScribbleFunctionMenuIDType.ScribbleMenuIDTypeDef
        int mMenuType;
        /*
        * TODO:Because PageRecyclerView need it's own notifyDataSetChanged() (not the adapter one)to update page status.
        * so we had to obtain a weakReference (avoid leak)to update page info text when first load.
        * Maybe OnPagingListener should always trigger when data load into view,which we didn't
        * need to update some page info text manually for first time loading.
        */
        private WeakReference<ScribbleActivity> activityWeakReference;

        ScribbleFunctionAdapter(ScribbleActivity itemNavigator, int menuType) {
            mItemNavigator = itemNavigator;
            mLayoutInflater = itemNavigator.getLayoutInflater();
            activityWeakReference = new WeakReference<>(itemNavigator);
            mMenuType = menuType;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public ScribbleFunctionItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            return new ScribbleFunctionItemViewHolder(ScribbleFunctionItemBinding.inflate(mLayoutInflater, parent, false));
        }

        @Override
        public void onPageBindViewHolder(ScribbleFunctionItemViewHolder holder, int position) {
            holder.bindTo(getItemVMList().get(position));
        }

        @Override
        public void setRawData(List<Integer> rawData, Context context) {
            super.setRawData(rawData, context);
            for (Integer mainMenuID : rawData) {
                ScribbleFunctionItemViewModel viewModel = new ScribbleFunctionItemViewModel(mainMenuID, mMenuType);
                viewModel.setNavigator(mItemNavigator);
                getItemVMList().add(viewModel);
            }
            if (activityWeakReference.get() != null) {
                switch (mMenuType) {
                    case ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU:
                        activityWeakReference.get().mBinding.functionRecyclerView.notifyDataSetChanged();
                        break;
                    case ScribbleFunctionMenuIDType.TOOL_BAR_MENU:
                        activityWeakReference.get().mBinding.toolBarRecyclerView.notifyDataSetChanged();
                        break;
                }
            }
        }
    }
}
