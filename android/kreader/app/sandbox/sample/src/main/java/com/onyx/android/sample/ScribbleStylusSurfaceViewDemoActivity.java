package com.onyx.android.sample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

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
    private PenReader penReader;
    private Matrix viewMatrix;

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
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initPenReader();
                cleanSurfaceView();
                updateViewMatrix();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                cleanSurfaceView();
                updateViewMatrix();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }


    public PenReader getPenReader() {
        if (penReader == null) {
            penReader = new PenReader(this);
        }
        return penReader;
    }

    private void updateViewMatrix() {
        int viewPosition[] = {0, 0};
        surfaceView.getLocationOnScreen(viewPosition);
        viewMatrix = new Matrix();
        viewMatrix.postTranslate(-viewPosition[0], -viewPosition[1]);
    }

    private void initPenReader() {
        getPenReader().setPenReaderCallback(new PenReader.PenReaderCallback() {
            final float baseWidth = 5;
            final float pressure = 1;
            final float size = 1;
            boolean begin = false;
            @Override
            public void onBeginRawData() {
                begin = true;
                enterScribbleMode();
            }

            @Override
            public void onEndRawData() {

            }

            @Override
            public void onRawTouchPointListReceived(TouchPointList touchPointList) {
                for (TouchPoint touchPoint : touchPointList.getPoints()) {
                    TouchPoint point = mapScreenPointToPage(touchPoint);
                    float dst[] = mapPoint(point.getX(), point.getY());
                    if (begin) {
                        EpdController.startStroke(baseWidth, dst[0], dst[1], pressure, size, System.currentTimeMillis());
                    }else {
                        EpdController.addStrokePoint(baseWidth, dst[0], dst[1], pressure, size, System.currentTimeMillis());
                    }
                    begin = false;
                }
            }

            @Override
            public void onBeginErasing() {

            }

            @Override
            public void onEndErasing() {

            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList touchPointList) {

            }

        });

        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                penStart();
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
        getPenReader().pause();
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
        getPenReader().start();
        getPenReader().resume();
    }

    private void leaveScribbleMode() {
        scribbleMode = false;
        EpdController.leaveScribbleMode(surfaceView);
        getPenReader().stop();
    }

    private float[] mapPoint(float x, float y) {
        x = Math.min(Math.max(0, x), surfaceView.getWidth());
        y = Math.min(Math.max(0, y), surfaceView.getHeight());

        final int viewLocation[] = {0, 0};
        surfaceView.getLocationOnScreen(viewLocation);
        final Matrix viewMatrix = new Matrix();
        DeviceConfig deviceConfig = DeviceConfig.sharedInstance(this, "note");
        viewMatrix.postRotate(deviceConfig.getViewPostOrientation());
        viewMatrix.postTranslate(deviceConfig.getViewPostTx(), deviceConfig.getViewPostTy());

        float screenPoints[] = {viewLocation[0] + x, viewLocation[1] + y};
        float dst[] = {0, 0};
        viewMatrix.mapPoints(dst, screenPoints);
        return dst;
    }

    private TouchPoint mapScreenPointToPage(final TouchPoint touchPoint) {
        float dstPoint[] = {0, 0};
        float srcPoint[] = {0, 0};
        dstPoint[0] = touchPoint.x;
        dstPoint[1] = touchPoint.y;
        if (viewMatrix != null) {
            srcPoint[0] = touchPoint.x;
            srcPoint[1] = touchPoint.y;
            viewMatrix.mapPoints(dstPoint, srcPoint);
        }
        touchPoint.x = dstPoint[0];
        touchPoint.y = dstPoint[1];
        return touchPoint;
    }

    @Override
    protected void onResume() {
        getPenReader().resume();
        super.onResume();
    }

}
