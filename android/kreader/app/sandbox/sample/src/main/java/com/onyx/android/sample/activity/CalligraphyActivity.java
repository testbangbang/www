package com.onyx.android.sample.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewDebug;

import com.hanvon.core.Algorithm;
import com.onyx.android.sample.R;
import com.onyx.android.sample.requests.CalligraphyRequest;
import com.onyx.android.sample.utils.ImageUtils;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.Debug;

import java.util.ArrayList;
import java.util.List;

public class CalligraphyActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    public List<TouchPoint> points = new ArrayList<>();
    private volatile long lastMoveTime;
    private float previousVelocity;
    private volatile float previousBrushSize;
    private Bitmap bitmap;
    private Bitmap compareBitmap;
    private Bitmap brush;
    public volatile float lastX, lastY;
    private Path path;
    private RequestManager requestManager = new RequestManager();
    private BitmapShader brushShader;

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
                bitmap = ImageUtils.create(BitmapFactory.decodeResource(getResources(), R.drawable.bk));
                compareBitmap = ImageUtils.create(BitmapFactory.decodeResource(getResources(), R.drawable.bk));
                brush =  BitmapUtils.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.brush2), 30, 30);
                brushShader = new BitmapShader(brush,
                        Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

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

    private CalligraphyRequest createRequest(final float x, final float y, boolean finished) {
        return new CalligraphyRequest(x, y, finished);
    }

    private final Runnable generateRunnable(final CalligraphyRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.execute(CalligraphyActivity.this);
                } catch (java.lang.Exception exception) {
                    Debug.d(exception.toString());
                    request.setException(exception);
                } finally {
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    private void submitRequest(final float x, final float y, boolean finished) {
        final CalligraphyRequest request = createRequest(x, y, finished);
        final Runnable runnable = generateRunnable(request);
        requestManager.submitRequest(this, request, runnable, null);
    }

    private boolean processTouchEvent(final MotionEvent e) {
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case (MotionEvent.ACTION_DOWN):
                lastMoveTime = System.currentTimeMillis();
                submitRequest(e.getX(), e.getY(), false);
                return true;
            case (MotionEvent.ACTION_CANCEL):
            case (MotionEvent.ACTION_OUTSIDE):
                break;
            case MotionEvent.ACTION_UP:
                submitRequest(e.getX(), e.getY(), true);
                return true;
            case MotionEvent.ACTION_MOVE:
                int n = e.getHistorySize();
                for (int i = 0; i < n; i++) {
                    lastMoveTime = System.currentTimeMillis();
                    submitRequest(e.getHistoricalX(i), e.getHistoricalY(i), false);
                }
                lastMoveTime = System.currentTimeMillis();
                submitRequest(e.getX(), e.getY(), false);
                return true;
            default:
                break;
        }
        return true;
    }

    private float curve(float t, float b, float c, float d) {
        return c*t/d + b;
    }

    private void drawPerp(final Canvas canvas, final Paint paint, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        float dist = 5.0f;
        if (Float.compare(dx, 0) == 0) {
            canvas.drawLine((x1 + x2) / 2 - dist, (y1 + y2) / 2, (x1 + x2) / 2 + dist, (y1 + y2) / 2, paint);
            return;
        } else if (Float.compare(dy, 0) == 0) {
            canvas.drawLine((x1 + x2) / 2 , (y1 + y2) / 2 - dist, (x1 + x2) / 2 , (y1 + y2) / 2 + dist, paint);
            return;
        }
        float b = y1 - x1 * dy / dx;
        float ix = (x1 + x2) / 2;
        float iy =  ix * dy / dx + b;
        float b2 = iy + ix * dx / dy;

        float x3 = ix - 5;
        float y3 = - x3 * dx / dy + b2;

        float x4 = ix + 5;
        float y4 = -dx/dy * x4 + b2;



        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        float degree = (float)Math.toDegrees(-dx/dy);
        canvas.save();
        canvas.rotate(-degree);
        canvas.drawOval(new RectF(ix-5,
                (ix - 5)* (-dx/dy) + b2,
                ix + 5,
                (ix + 5)* (-dx/dy) + b2),
                paint);

        canvas.restore();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        canvas.drawLine(x3, y3, x4, y4, paint);
    }

    public void drawWaterDrop(final Canvas canvas, final Paint pathPaint, final Paint paint) {
        Path temp = new Path();
        temp.moveTo(200,700);
        temp.quadTo(50,900,200,910);
        temp.quadTo(350,900,200,700);
        canvas.drawPath(temp, pathPaint);

        paint.setStrokeWidth(3.0f);
        canvas.drawPoint(200, 700, paint);
        canvas.drawPoint(50, 900, paint);
        canvas.drawPoint(200, 910, paint);
        canvas.drawPoint(350, 900, paint);
        canvas.drawPoint(200, 700, paint);

    }

    public void drawStroke2(float x1, float y1, float x2, float y2, boolean brushEnded) {
        if (!brushEnded) {
            return;
        }

        Log.e("###########", "generate strokes");

        Canvas canvas = new Canvas(compareBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1.0f);

        Paint pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStrokeWidth(1.0f);

        drawWaterDrop(canvas, pathPaint, paint);

        path = new Path();
        TouchPoint touchPoint = points.get(0);
        path.moveTo(touchPoint.x, touchPoint.y);
        for(int i = 1; i < points.size(); ++i) {
            TouchPoint tp = points.get(i);
            //drawPerp(canvas, paint, touchPoint.x, touchPoint.y, tp.x, tp.y);
            path.quadTo((tp.x + touchPoint.x) / 2, (tp.y + touchPoint.y) / 2, tp.x, tp.y);
            touchPoint = tp;
        }

        canvas.drawPath(path, pathPaint);
        float aCoordinates[] = {0f, 0f};

        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        float step = 3.0f;
        float dist = 0;
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);
        while (dist < length) {
            pm.getPosTan(dist, aCoordinates, null);
            dist += step;
            canvas.drawBitmap(brush, aCoordinates[0] - brush.getWidth() / 2, aCoordinates[1] - brush.getHeight() / 2, null);
        }

        dist = 0;
        while (dist < length) {
            pm.getPosTan(dist, aCoordinates, null);
            dist += step;
            canvas.drawPoint(aCoordinates[0], aCoordinates[1], paint);
        }

        Log.e("###########", "generate strokes finished");

        BitmapUtils.saveBitmap(compareBitmap, "/mnt/sdcard/quad.png");
        Log.e("###########", "bitmap saved");
    }

    public void drawStroke(float x1, float y1, float x2, float y2, boolean brushEnded) {
        if (points.size() <= 3) {
            return;
        }

        // Draw Stroke part (Spline)
        // ---- stroke setup
        long t = System.currentTimeMillis() - lastMoveTime;
        float distance = distance(x1, y1, x2, y2);
        float velocity = distance / Math.max(1, t);
        float accelerate = (this.previousVelocity == 0) ? 0 : velocity / this.previousVelocity;


        float brushSize = Math.max(0, curve(velocity, 20, -20 - 0, 5));
        int segCount = 20; // spline
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
                    //
                    continue;
                }

                drawImage(tX - (tS / 2), tY - (tS / 2), tS, tS, brushEnded);

            }
        }
        this.previousBrushSize = brushSize;
    }

    private void drawImage(float x, float y, float width, float height, boolean end) {
        Log.e("########", "draw image with: " + x + " " + y + " " + width + " " + height);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.BLACK);
        RectF dst = new RectF(x, y, x + 1, y + 1);
        canvas.drawRect(dst, paint);
        //canvas.drawBitmap(brush, null, dst, null);

        if (end) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawBitmap(bitmap, 0, 0, null);
            surfaceHolder.unlockCanvasAndPost(canvas);

            BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/data.png");
        }
    }

    private float distance(final float x1, final float y1, final float x2, final float y2) {
        float dx = (x1 - x2) * (x1 - x2);
        float dy = (y1 - y2) * (y1 - y2);
        return (float)Math.sqrt(dx + dy);
    }
}
