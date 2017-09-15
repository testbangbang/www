package com.onyx.android.sample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sample.device.DeviceConfig;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.api.PenReader;
import com.onyx.android.sdk.scribble.asyncrequest.TouchHelper;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScribbleStylusSurfaceViewDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.button_pen)
    Button buttonPen;
    @Bind(R.id.button_eraser)
    Button buttonEraser;
    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;

    boolean scribbleMode = false;
    private EventBus eventBus = new EventBus();
    private TouchHelper touchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble_surfaceview_stylus_demo);

        ButterKnife.bind(this);
        buttonPen.setOnClickListener(this);
        buttonEraser.setOnClickListener(this);

        initSurfaceView();
    }

    private void initSurfaceView() {
        touchHelper = new TouchHelper(eventBus);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cleanSurfaceView();
                touchHelper.setup(surfaceView);
                touchHelper.setStrokeWidth(3.0f);
                touchHelper.setUseRawInput(true);
                touchHelper.startRawDrawing();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                cleanSurfaceView();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        leaveScribbleMode();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        touchHelper.quit();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonPen)) {
            penStart();
            return;
        } else if (v.equals(buttonEraser)) {
            leaveScribbleMode();
            cleanSurfaceView();
            return;
        }
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

    private void enterScribbleMode() {
        EpdController.enterScribbleMode(surfaceView);
        scribbleMode = true;
    }

    private void penStart() {
        enterScribbleMode();
        touchHelper.resumeRawDrawing();
    }

    private void leaveScribbleMode() {
        scribbleMode = false;
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.pauseRawDrawing();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
