package com.onyx.edu.note.scribble;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.edu.note.HandlerManager;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.R;
import com.onyx.edu.note.data.ScribbleAction;
import com.onyx.edu.note.databinding.ActivityScribbleBinding;
import com.onyx.edu.note.receiver.DeviceReceiver;
import com.onyx.edu.note.util.Constant;
import com.onyx.edu.note.util.NoteViewUtil;

public class ScribbleActivity extends OnyxAppCompatActivity implements ScribbleNavigator {
    private static final String TAG = ScribbleActivity.class.getSimpleName();
    ActivityScribbleBinding mBinding;
    ScribbleViewModel mViewModel;
    protected SurfaceHolder.Callback surfaceCallback;
    DeviceReceiver deviceReceiver = new DeviceReceiver();
    NoteManager mNoteManager;
    HandlerManager mHandlerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        mNoteManager = NoteManager.sharedInstance(this);
        mHandlerManager = new HandlerManager(this);
        mViewModel = new ScribbleViewModel(this);
        // Link View and ViewModel
        mBinding.setViewModel(mViewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        mNoteManager.sync(false, false);
        removeSurfaceViewCallback();
    }

    @Override
    protected void onStop() {
        deviceReceiver.unregisterReceiver(this);
        mNoteManager.quit();
        super.onStop();
    }

    private void handleIntent(Intent intent) {
        @ScribbleAction.ScribbleActionDef int scribbleAction = intent.getIntExtra(Constant.SCRIBBLE_ACTION_TAG, ScribbleAction.INVALID);
        if (!ScribbleAction.isValidAction(scribbleAction)) {
            //TODO:direct call finish here.because we don't want incorrect illegal call.
            finish();
            return;
        }
        String uniqueID = intent.getStringExtra(Constant.NOTE_ID_TAG);
        String parentID = intent.getStringExtra(Constant.NOTE_PARENT_ID_TAG);
        BaseCallback callback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                InputMethodUtils.hideInputKeyboard(ScribbleActivity.this);
                mNoteManager.syncWithCallback(true, true, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        NoteViewUtil.drawPage(mBinding.noteView, mNoteManager.getViewBitmap(), mNoteManager.getDirtyShape());
                    }
                });
            }
        };
        mViewModel.start(uniqueID, parentID, scribbleAction, callback);
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
                    mNoteManager.clearSurfaceView(mBinding.noteView);
                    mNoteManager.setView(ScribbleActivity.this, mBinding.noteView, inputCallback());
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

    protected NoteViewHelper.InputCallback inputCallback() {
        return new NoteViewHelper.InputCallback() {
            @Override
            public void onBeginRawData() {
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
            }

            @Override
            public void onBeginErasing() {
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
            }

            @Override
            public void onDrawingTouchDown(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    NoteViewUtil.drawPage(mBinding.noteView, mNoteManager.getViewBitmap(), mNoteManager.getDirtyShape());
                }
            }

            @Override
            public void onDrawingTouchMove(final MotionEvent motionEvent, final Shape shape, boolean last) {
                if (last && !shape.supportDFB()) {
                    NoteViewUtil.drawPage(mBinding.noteView, mNoteManager.getViewBitmap(), mNoteManager.getDirtyShape());
                }
            }

            @Override
            public void onDrawingTouchUp(final MotionEvent motionEvent, final Shape shape) {
                if (!shape.supportDFB()) {
                    NoteViewUtil.drawPage(mBinding.noteView, mNoteManager.getViewBitmap(), mNoteManager.getDirtyShape());
                }
            }

        };
    }

    @Override
    public void onBackPressed() {
        mNoteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mViewModel.onSaveDocument(true, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (!request.isAbort() && e == null) {
                            ScribbleActivity.super.onBackPressed();
                        }
                    }
                });           
            }
        });
    }

    @Override
    public void prevPage() {

    }

    @Override
    public void nextPage() {

    }

    @Override
    public void goToTargetPage() {

    }

    @Override
    public void addPage() {

    }

    @Override
    public void deletePage() {

    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public void save() {

    }

    @Override
    public void goToSetting() {

    }

    @Override
    public void switchScribbleMode() {

    }
}
