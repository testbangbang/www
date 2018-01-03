package com.onyx.android.sample.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sample.R;
import com.onyx.android.sample.scribble.Stroke;
import com.onyx.android.sample.scribble.StrokePoint;
import com.onyx.android.sample.scribble.StrokeRenderer;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.utils.Benchmark;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BrushStrokeDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.button_pen)
    Button buttonPen;
    @Bind(R.id.button_eraser)
    Button buttonEraser;
    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;

    StrokeRenderer renderer;
    Bitmap bitmap = null;

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
                // ignore multi touch
                if (e.getPointerCount() > 1) {
                    return false;
                }

                switch (e.getAction() & MotionEvent.ACTION_MASK) {
                    case (MotionEvent.ACTION_DOWN):
                        renderer = new StrokeRenderer(new Stroke());
                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_OUTSIDE):
                        break;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Canvas canvas = new Canvas(bitmap);
                        Benchmark.globalBenchmark().restart();
                            int n = e.getHistorySize();
                            for (int i = 0; i < n; i++) {
                                StrokePoint p = new StrokePoint(e.getHistoricalX(i),
                                        e.getHistoricalY(i),
                                        e.getHistoricalSize(i),
                                        e.getHistoricalPressure(i),
                                        e.getHistoricalEventTime(i),
                                        e.getToolType(0));
                                renderer.addPoint(canvas,  p);
                            }
                            StrokePoint p = new StrokePoint(e.getX(), e.getY(), e.getSize(), e.getPressure(), e.getEventTime(), e.getToolType(0));
                            renderer.addPoint(canvas, p);
                        Benchmark.globalBenchmark().reportError("draw points: " + n);

                            canvas = surfaceView.getHolder().lockCanvas();
                            if (canvas != null) {
                                canvas.drawBitmap(bitmap, 0, 0, null);
                                surfaceView.getHolder().unlockCanvasAndPost(canvas);
                            }
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
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonPen)) {
            return;
        } else if (v.equals(buttonEraser)) {
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

        bitmap = Bitmap.createBitmap(surfaceView.getWidth(), surfaceView.getHeight(),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }

}
