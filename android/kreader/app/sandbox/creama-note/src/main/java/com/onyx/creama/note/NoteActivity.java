package com.onyx.creama.note;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.api.TouchHelper;
import com.onyx.android.sdk.scribble.api.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.api.event.BeginRawErasingEvent;
import com.onyx.android.sdk.scribble.api.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.EndRawDataEvent;
import com.onyx.android.sdk.scribble.api.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.api.event.RawErasePointListReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawErasePointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.api.event.RawTouchPointMoveReceivedEvent;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.creama.note.event.ChangePenWidthEvent;
import com.onyx.creama.note.event.EraseEvent;
import com.onyx.creama.note.event.ShapeChangeEvent;
import com.onyx.creama.note.view.NoteSubMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = NoteActivity.class.getSimpleName();
    private SurfaceView surfaceView;
    private TouchHelper touchHelper;
    private EventBus eventBus;
    private Toolbar toolbar;
    private View topDivider, bottomDivider;
    private RelativeLayout layoutFooter;
    private NoteSubMenu subMenu;
    private int shapeType = ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
    private float strokeWidth = 3f;
    private List<Shape> stashList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        eventBus = EventBus.getDefault();
        initView();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        topDivider = findViewById(R.id.top_divider);
        bottomDivider = findViewById(R.id.bottom_divider);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        layoutFooter = (RelativeLayout) findViewById(R.id.layout_footer);
        LinearLayout widthButton = (LinearLayout) findViewById(R.id.button_width);
        LinearLayout shapeButton = (LinearLayout) findViewById(R.id.button_shape);
        LinearLayout eraserButton = (LinearLayout) findViewById(R.id.button_eraser);
        widthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteSubMenu(0);
            }
        });
        shapeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteSubMenu(1);
            }
        });
        eraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoteSubMenu(2);
            }
        });
        initSurfaceView();
        initToolBar();
    }

    private void showNoteSubMenu(int category) {
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.pauseRawDrawing();
        getNoteSubMenu().show(category);
    }

    private NoteSubMenu getNoteSubMenu() {
        if (subMenu == null) {
            subMenu = new NoteSubMenu(this, (RelativeLayout) findViewById(R.id.activity_note), new NoteSubMenu.MenuCallback() {
                @Override
                public void onItemSelect(int item) {

                }

                @Override
                public void onLayoutStateChanged() {

                }

                @Override
                public void onCancel() {
                    touchHelper.resumeRawDrawing();
                }
            }, R.id.bottom_divider, true);
        }
        return subMenu;
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
        touchHelper.resumeRawDrawing();
    }

    @Override
    protected void onDestroy() {
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.stopRawDrawing();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
        touchHelper.pauseRawDrawing();
    }

    private void initSurfaceView() {
        touchHelper = new TouchHelper(eventBus);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                List<Rect> exclude = new ArrayList<>();
                exclude.add(touchHelper.getRelativeRect(surfaceView, toolbar));
                exclude.add(touchHelper.getRelativeRect(surfaceView, topDivider));
                exclude.add(touchHelper.getRelativeRect(surfaceView, bottomDivider));
                exclude.add(touchHelper.getRelativeRect(surfaceView, layoutFooter));
                Rect limit = new Rect();
                surfaceView.getLocalVisibleRect(limit);
                cleanSurfaceView();
                touchHelper.setup(surfaceView)
                        .setStrokeWidth(strokeWidth)
                        .setUseRawInput(true)
                        .setLimitRect(limit, exclude)
                        .startRawDrawing();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void cleanSurfaceView() {
        if (surfaceView.getHolder() == null) {
            return;
        }
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.WHITE);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void onChangePenWidthEvent(ChangePenWidthEvent e) {
        Log.d(TAG, "onChangePenWidthEvent");
        strokeWidth = e.getWidth();
        touchHelper.setStrokeWidth(strokeWidth);
    }

    @Subscribe
    public void onShapeChangeEvent(ShapeChangeEvent e) {
        Log.d(TAG, "onChangePenWidthEvent");
        shapeType = e.getShapeType();
        touchHelper.setRenderByFramework(ShapeFactory.isDFBShape(shapeType));
    }

    @Subscribe
    public void onEraseEvent(EraseEvent eraseEvent) {
        Log.d(TAG, "onEraseEvent: ");
        if (eraseEvent.isEraseAll()) {
            stashList.clear();
            EpdController.leaveScribbleMode(surfaceView);
            touchHelper.pauseRawDrawing();
            cleanSurfaceView();
            touchHelper.resumeRawDrawing();
        } else {

        }
    }

    private Shape createNewShape(int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(strokeWidth);
        shape.setColor(Color.BLACK);
        shape.setLayoutType(ShapeFactory.POSITION_FREE);
        return shape;
    }

    private Rect checkSurfaceView(SurfaceView surfaceView) {
        if (surfaceView == null || !surfaceView.getHolder().getSurface().isValid()) {
            Log.e(TAG, "surfaceView is not valid");
            return null;
        }
        return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
    }

    private void clearBackground(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    public void renderToSurfaceView(List<Shape> shapes, SurfaceView surfaceView) {
        if (shapes == null) {
            return;
        }
        Rect rect = checkSurfaceView(surfaceView);
        if (rect == null) {
            return;
        }

        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }
        Paint paint = new Paint();
        clearBackground(canvas, paint, rect);
        RenderContext renderContext = RenderContext.create(canvas, paint, null);
        for (Shape shape : shapes) {
            shape.render(renderContext);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    // below are callback events sent from TouchHelper

    @Subscribe
    public void onErasingTouchEvent(ErasingTouchEvent e) {
        Log.d(TAG, "onErasingTouchEvent");
    }

    @Subscribe
    public void onDrawingTouchEvent(DrawingTouchEvent e) {
        Log.d(TAG, "onDrawingTouchEvent");
    }

    @Subscribe
    public void onBeginRawDataEvent(BeginRawDataEvent e) {
        Log.d(TAG, "onBeginRawDataEvent");
    }

    @Subscribe
    public void onEndRawDataEvent(EndRawDataEvent e) {
        Log.e(TAG, "onEndRawDataEvent");
        renderToSurfaceView(stashList, surfaceView);
    }

    @Subscribe
    public void onRawTouchPointMoveReceivedEvent(RawTouchPointMoveReceivedEvent e) {
        Log.d(TAG, "onRawTouchPointMoveReceivedEvent");
    }

    @Subscribe
    public void onRawTouchPointListReceivedEvent(RawTouchPointListReceivedEvent e) {
        Log.d(TAG, "onRawTouchPointListReceivedEvent");
        Shape shape  = createNewShape(shapeType);
        shape.addPoints(e.getTouchPointList());
        stashList.add(shape);
    }

    @Subscribe
    public void onRawErasingStartEvent(BeginRawErasingEvent e) {
        Log.d(TAG, "onRawErasingStartEvent");
    }

    @Subscribe
    public void onRawErasingFinishEvent(RawErasePointListReceivedEvent e) {
        Log.d(TAG, "onRawErasingFinishEvent");
    }

    @Subscribe
    public void onRawErasePointMoveReceivedEvent(RawErasePointMoveReceivedEvent e) {
        Log.d(TAG, "onRawErasePointMoveReceivedEvent");
    }

    @Subscribe
    public void onRawErasePointListReceivedEvent(RawErasePointListReceivedEvent e) {
        Log.d(TAG, "onRawErasePointListReceivedEvent");
    }

}
