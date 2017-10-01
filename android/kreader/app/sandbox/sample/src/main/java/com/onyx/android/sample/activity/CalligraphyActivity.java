package com.onyx.android.sample.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.hanvon.core.Algorithm;
import com.onyx.android.sample.R;
import com.onyx.android.sample.utils.ImageUtils;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class CalligraphyActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private List<TouchPoint> points = new ArrayList<>();
    private long lastMoveTime;
    private float previousVelocity;
    private float previousBrushSize;
    private Bitmap bitmap;
    private Bitmap brush;
    private float lastX, lastY;
    private Canvas myCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calligraphy);
        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView)findViewById(R.id.calligraphy_surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                bitmap = ImageUtils.loadBitmapFromFile("/mnt/sdcard/bk.png");
                brush =  BitmapFactory.decodeResource(getResources(), R.drawable.brush);

                List<Rect> list = new ArrayList<Rect>();
                list.add(new Rect(10, 10, 30, 30));
                ImageUtils.drawRectanglesOnBitmap(bitmap, list);
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                RectF dst = new RectF(10, 10, 30, 40);
                canvas.drawBitmap(brush, null, dst, null);
                holder.unlockCanvasAndPost(canvas);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                processTouchEvent(event);
                return true;
            }
        });
    }

    private boolean processTouchEvent(final MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case (MotionEvent.ACTION_DOWN):
                myCanvas = new Canvas(bitmap);
                lastX = e.getX();
                lastY = e.getY();
                drawStroke(lastX, lastY, lastX, lastY, false);
                lastMoveTime = System.currentTimeMillis();
                return true;
            case (MotionEvent.ACTION_CANCEL):
            case (MotionEvent.ACTION_OUTSIDE):
                break;
            case MotionEvent.ACTION_UP:
                drawStroke(lastX, lastY, e.getX(), e.getY(), false);
                lastMoveTime = System.currentTimeMillis();
                myCanvas = null;
                BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/calligraphy-.png");
                return true;
            case MotionEvent.ACTION_MOVE:
                int n = e.getHistorySize();
                for (int i = 0; i < n; i++) {
                    drawStroke(lastX, lastY, e.getHistoricalX(i), e.getHistoricalY(i), false);
                    lastX = e.getHistoricalX(i);
                    lastY = e.getHistoricalY(i);
                }
                drawStroke(lastX, lastY, e.getX(), e.getY(), false);
                lastX = e.getX();
                lastY = e.getY();
                lastMoveTime = System.currentTimeMillis();
                return true;
            default:
                break;
        }
        return true;
    }

    private float curve(float t, float b, float c, float d) {
        return c*t/d + b;
    }

    private void drawStroke(float x1, float y1, float x2, float y2, boolean brushEnded) {

        points.add(new TouchPoint(x2, y2, 1, 1, System.currentTimeMillis()));
        if (points.size() <= 3) {
            return;
        }

        // Draw Stroke part (Spline)
        // ---- stroke setup
        long t = System.currentTimeMillis() - lastMoveTime;
        float distance = distance(x1, y1, x2, y2);
        float velocity = distance / Math.max(1, t);
        float accelerate = (this.previousVelocity == 0) ? 0 : velocity / this.previousVelocity;


        float brushSize = Math.max(0, curve(velocity, 30, -30 - 0, 5));
        int segCount = 30; // spline
        int buffLen = points.size();
        List<TouchPoint> temp = points.subList(buffLen - 4, buffLen);
        temp.add(0, temp.get(0));
        temp.add(temp.get(temp.size() - 1));

        for (int j = 0, m = temp.size() - 3; j < m; j++) {
            TouchPoint p0 = temp.get(j);
            TouchPoint p1 = temp.get(j + 1);
            TouchPoint p2 = temp.get(j + 2);
            TouchPoint p3 = temp.get(j + 3);
            TouchPoint v0 = new TouchPoint();
            v0.x = (p2.x - p0.x) / 2;
            v0.y = (p2.y - p0.y) / 2;
            TouchPoint v1 = new TouchPoint();
            v1.x = (p3.x - p1.x) / 2;
            v1.y = (p3.y - p1.y) / 2;

            float tmp1 = (2 * p1.x - 2 * p2.x + v0.x + v1.x);
            float tmp2 = (-3 * p1.x + 3 * p2.x - 2 * v0.x - v1.x);
            float tmp3 = (2 * p1.y - 2 * p2.y + v0.y + v1.y);
            float tmp4 = (-3 * p1.y + 3 * p2.y - 2 * v0.y - v1.y);

            for (int i = 1, n = segCount + 1; i <= n; i++) {
                float seg = i / segCount;

                float tX = (float)((tmp1 * Math.pow(seg, 3)) + (tmp2 * Math.pow(seg, 2)) + v0.x * seg + p1.x);
                float tY = (float)((tmp3 * Math.pow(seg, 3)) + (tmp4 * Math.pow(seg, 2)) + v0.y * seg + p1.y);

                float tS = this.previousBrushSize + ((brushSize - this.previousBrushSize) / segCount) * i;

                if (this.previousBrushSize == brushSize && Math.random() < 0.3) {
                    continue;
                }

                drawImage(tX - (tS / 2), tY - (tS / 2), tS, tS);

            }
        }
        this.previousBrushSize = brushSize;
    }

    private void drawImage(float x, float y, float width, float height) {
        Log.e("########", "draw image with: " + x + " " + y + " " + width + " " + height);
        RectF dst = new RectF(x, y, x + width, y + height);
        myCanvas.drawBitmap(brush, null, dst, null);
    }

    private float distance(final float x1, final float y1, final float x2, final float y2) {
        float dx = (x1 - x2) * (x1 - x2);
        float dy = (y1 - y2) * (y1 - y2);
        return (float)Math.sqrt(dx + dy);
    }
}
