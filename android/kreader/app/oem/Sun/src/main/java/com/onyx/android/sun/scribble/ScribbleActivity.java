package com.onyx.android.sun.scribble;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.view.SurfaceHolder;
import android.view.View;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.api.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageGoToTargetIndexRequest;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.activity.BaseActivity;
import com.onyx.android.sun.adapter.ShapePageAdapter;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.databinding.ActivityScribbleBinding;
import com.onyx.android.sun.event.ShapePageItemEvent;
import com.onyx.android.sun.event.SubjectiveResultEvent;
import com.onyx.android.sun.view.DisableScrollGridManager;
import com.onyx.android.sun.view.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by hehai on 17-10-13.
 */

public class ScribbleActivity extends BaseActivity implements View.OnClickListener {
    NoteManager noteManager;
    private ActivityScribbleBinding scribbleBinding;
    private ScribbleViewModel mViewModel;
    private ScribbleHandler scribbleHandler;
    DeviceReceiver deviceReceiver = new DeviceReceiver();
    private SurfaceHolder.Callback surfaceCallback;
    private ShapePageAdapter shapePageAdapter;
    private String questionID;
    private String questionTitle;
    private String question;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        scribbleBinding = (ActivityScribbleBinding) binding;
        noteManager = SunApplication.getInstance().getNoteManager();
        mViewModel = new ScribbleViewModel(this);
        scribbleHandler = new ScribbleHandler(noteManager);
        scribbleHandler.onStrokeWidthChanged(ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_BOLD);
        noteManager.getDirtyStash();
        scribbleBinding.shapePageRecycler.setLayoutManager(new DisableScrollGridManager(SunApplication.getInstance()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(SunApplication.getInstance(), DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setDrawLine(false);
        scribbleBinding.shapePageRecycler.addItemDecoration(dividerItemDecoration);
        shapePageAdapter = new ShapePageAdapter();
        scribbleBinding.shapePageRecycler.setAdapter(shapePageAdapter);
        scribbleBinding.titleBar.titleBarRecord.setVisibility(View.GONE);
        scribbleBinding.titleBar.titleBarSubmit.setVisibility(View.GONE);
    }

    @Override
    protected void initListener() {
        scribbleBinding.setListener(this);
        scribbleBinding.titleBar.titleBarTitle.setOnClickListener(this);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_scribble;
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

    private void addSurfaceViewCallback() {
        scribbleBinding.surfaceView.getHolder().addCallback(getSurfaceCallback());
    }

    private void removeSurfaceViewCallback() {
        if (surfaceCallback != null) {
            scribbleBinding.surfaceView.getHolder().removeCallback(surfaceCallback);
        }
    }

    protected SurfaceHolder.Callback getSurfaceCallback() {
        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    noteManager.clearSurfaceView(scribbleBinding.surfaceView);
                    noteManager.setView(scribbleBinding.surfaceView);
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

    private void handleIntent(Intent intent) {
        questionID = intent.getStringExtra(Constants.QUESTION_ID);
        questionTitle = intent.getStringExtra(Constants.QUESTION_TITLE);
        question = intent.getStringExtra(Constants.QUESTION_TAG);
        scribbleBinding.titleBar.setTitle(questionTitle);
        scribbleBinding.setQuestion(question);
        mViewModel.start(questionID, null, ScribbleAction.EDIT, null);
    }

    private void onDocumentClose() {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                scribbleHandler.saveDocument(questionID, questionTitle, true, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        EventBus.getDefault().post(new SubjectiveResultEvent(questionID));
                    }
                });
            }
        });
    }

    public void saveDocument() {
        onDocumentClose();
    }

    public void prev(View view) {
        scribbleHandler.prevPage();
    }

    public void next(View view) {
        scribbleHandler.nextPage();
    }

    public void addPage() {
        scribbleHandler.addPage();
    }

    public void restart(View view) {
        handleIntent(null);
    }

    public void eraserLine(View view) {
        scribbleHandler.onEraserChanged(ScribbleSubMenuID.Eraser.ERASE_PARTIALLY);
    }

    public void eraserAll(View view) {
        scribbleHandler.onEraserChanged(ScribbleSubMenuID.Eraser.ERASE_TOTALLY);
    }

    public void stroke(View view) {
        scribbleHandler.onStrokeWidthChanged(ScribbleSubMenuID.Thickness.THICKNESS_ULTRA_BOLD);
    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent event) {
        scribbleHandler.onRawTouchPointListReceivedEvent(event);
    }

    @Subscribe
    public void onErasingTouchEvent(ErasingTouchEvent event) {
        scribbleHandler.onErasingTouchEvent(event);
    }

    @Subscribe
    public void onRequestInfoUpdateEvent(RequestInfoUpdateEvent event) {
        shapePageAdapter.setShapes(noteManager.getShapeDataInfo().getPageCount());
    }

    @Subscribe
    public void onShapePageItemEvent(ShapePageItemEvent event) {
        PageGoToTargetIndexRequest request = new PageGoToTargetIndexRequest(event.getPosition(), true);
        noteManager.submitRequest(request, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scribble_save:
                saveDocument();
                break;
            case R.id.scribble_add_page:
                addPage();
                break;
            case R.id.scribble_delete_page:
                deletePage();
                break;
            case R.id.title_bar_title:
                finish();
                break;
        }
    }

    private void deletePage() {
        scribbleHandler.deletePage();
    }
}
