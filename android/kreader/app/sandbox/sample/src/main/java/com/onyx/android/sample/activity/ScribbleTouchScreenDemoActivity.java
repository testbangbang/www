package com.onyx.android.sample.activity;

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

import com.onyx.android.sample.R;
import com.onyx.android.sample.device.DeviceConfig;
import com.onyx.android.sdk.api.device.epd.EpdController;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScribbleTouchScreenDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.button_pen)
    Button buttonPen;
    @Bind(R.id.button_eraser)
    Button buttonEraser;
    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;

    boolean scribbleMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble_touch_screen_demo);

        ButterKnife.bind(this);
        buttonPen.setOnClickListener(this);
        buttonEraser.setOnClickListener(this);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cleanSurfaceView();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                cleanSurfaceView();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (!scribbleMode) {
                    return false;
                }

                // ignore multi touch
                if (e.getPointerCount() > 1) {
                    return false;
                }

                final float baseWidth = 5;

                switch (e.getAction() & MotionEvent.ACTION_MASK) {
                    case (MotionEvent.ACTION_DOWN):
                        float dst[] = mapPoint(e.getX(), e.getY());
                        EpdController.startStroke(baseWidth, dst[0], dst[1], e.getPressure(), e.getSize(), e.getEventTime());
                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_OUTSIDE):
                        break;
                    case MotionEvent.ACTION_UP:
                        dst = mapPoint(e.getX(), e.getY());
                        EpdController.finishStroke(baseWidth, dst[0], dst[1], e.getPressure(), e.getSize(), e.getEventTime());
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int n = e.getHistorySize();
                        for (int i = 0; i < n; i++) {
                            dst = mapPoint(e.getHistoricalX(i), e.getHistoricalY(i));
                            EpdController.addStrokePoint(baseWidth,  dst[0], dst[1], e.getPressure(), e.getSize(), e.getEventTime());
                        }
                        dst = mapPoint(e.getX(), e.getY());
                        EpdController.addStrokePoint(baseWidth, dst[0], dst[1], e.getPressure(), e.getSize(), e.getEventTime());
                        return true;
                    default:
                        break;
                }
                return true;
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
        leaveScribbleMode();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonPen)) {
            enterScribbleMode();
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

    private void leaveScribbleMode() {
        scribbleMode = false;
        EpdController.leaveScribbleMode(surfaceView);
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

}
