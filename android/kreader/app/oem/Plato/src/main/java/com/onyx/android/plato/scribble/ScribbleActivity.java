package com.onyx.android.plato.scribble;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.PopupWindow;

import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.activity.BaseActivity;
import com.onyx.android.plato.adapter.ShapePageAdapter;
import com.onyx.android.plato.bean.ScribbleToolBean;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.databinding.ActivityScribbleBinding;
import com.onyx.android.plato.event.ShapePageItemEvent;
import com.onyx.android.plato.event.SubjectiveResultEvent;
import com.onyx.android.plato.view.DisableScrollGridManager;
import com.onyx.android.plato.view.DividerItemDecoration;
import com.onyx.android.plato.view.ScribbleToolPopupWindow;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.api.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageGoToTargetIndexRequest;
import com.onyx.android.sdk.utils.DeviceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

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
    private ScribbleToolPopupWindow toolPopupWindow;
    private boolean isShow;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        scribbleBinding = (ActivityScribbleBinding) binding;
        toolPopupWindow = new ScribbleToolPopupWindow(this);
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
        scribbleBinding.titleBar.titleBarImageOne.setVisibility(View.VISIBLE);
        scribbleBinding.titleBar.titleBarImageTwo.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initListener() {
        scribbleBinding.setListener(this);
        scribbleBinding.titleBar.titleBarTitle.setOnClickListener(this);
        scribbleBinding.titleBar.titleBarImageOne.setOnClickListener(this);
        scribbleBinding.titleBar.titleBarImageTwo.setOnClickListener(this);

        toolPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                noteManager.sync(true, true);
            }
        });
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        noteManager.sync(true, false);
        removeSurfaceViewCallback();
    }

    @Override
    protected void onStop() {
        deviceReceiver.unregisterReceiver(this);
        noteManager.unregisterEventBus(this);
        noteManager.sync(false, false);
        noteManager.quit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        if (toolPopupWindow != null) {
            toolPopupWindow = null;
        }
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

    @Subscribe
    public void onDrawingTouchEvent(DrawingTouchEvent event) {
        scribbleHandler.onDrawingTouchEvent(event);
    }

    @Subscribe
    public void onSubMenuClickEvent(SubMenuClickEvent event) {
        scribbleHandler.handleSubMenuEvent(event.getMenuId());
        if (toolPopupWindow != null && toolPopupWindow.isShowing()) {
            toolPopupWindow.dismiss();
        }
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
                EventBus.getDefault().post(new SubjectiveResultEvent(questionID));
                finish();
                break;
            case R.id.title_bar_image_one:
                showEraserToolMenu();
                break;
            case R.id.title_bar_image_two:
                showShapeToolMenu();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new SubjectiveResultEvent(questionID));
        super.onBackPressed();
    }

    private void showShapeToolMenu() {
        List<ScribbleToolBean> list = new ArrayList<>();
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.NORMAL_PEN_STYLE, R.drawable.ic_shape_pencil));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.BRUSH_PEN_STYLE, R.drawable.ic_shape_brush));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.LINE_STYLE, R.drawable.ic_shape_line));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.TRIANGLE_STYLE, R.drawable.ic_shape_triangle));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.CIRCLE_STYLE, R.drawable.ic_shape_circle));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.RECT_STYLE, R.drawable.ic_shape_square));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.TRIANGLE_45_STYLE, R.drawable.ic_shape_triangle_45));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.TRIANGLE_60_STYLE, R.drawable.ic_shape_triangle_60));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.PenStyle.TRIANGLE_90_STYLE, R.drawable.ic_shape_triangle_90));
        showToolMenuPop(list);
    }

    private void showEraserToolMenu() {
        List<ScribbleToolBean> list = new ArrayList<>();
        list.add(new ScribbleToolBean(ScribbleSubMenuID.Eraser.ERASE_PARTIALLY, R.drawable.ic_eraser_part));
        list.add(new ScribbleToolBean(ScribbleSubMenuID.Eraser.ERASE_TOTALLY, R.drawable.ic_eraser_all));
        showToolMenuPop(list);
    }

    private void deletePage() {
        scribbleHandler.deletePage();
    }

    public void showToolMenuPop(final List<ScribbleToolBean> tools) {
        noteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                toolPopupWindow.show(scribbleBinding.titleBar.titleBarImageOne, tools);
            }
        });
    }
}
