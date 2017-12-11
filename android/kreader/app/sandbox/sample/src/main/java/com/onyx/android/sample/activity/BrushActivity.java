package com.onyx.android.sample.activity;

import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.Touch;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sample.R;
import com.onyx.android.sample.utils.ImageUtils;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class BrushActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    public List<TouchPoint> points = new ArrayList<>();
    private float lastWidth = 1.0f;
    private float lastVelocity = 1.0f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brush);
        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView)findViewById(R.id.brush_surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
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
                points.add(new TouchPoint(e));
                return true;
            case (MotionEvent.ACTION_CANCEL):
            case (MotionEvent.ACTION_OUTSIDE):
                break;
            case MotionEvent.ACTION_UP:
                points.add(new TouchPoint(e));
                drawAll();
                return true;
            case MotionEvent.ACTION_MOVE:
                int n = e.getHistorySize();
                for (int i = 0; i < n; i++) {
                    points.add(TouchPoint.fromHistorical(e, i));
                }
                return true;
            default:
                break;
        }
        return true;
    }

    private void drawAll() {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);

        Matrix matrix = new Matrix();
        matrix.setScale(5.0f, 5.0f);
//        canvas.setMatrix(matrix);

        Path path = new Path();
        Paint paint = new Paint();
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        TouchPoint last = null;
        TouchPoint current = null;

        for(int i = 0; i < points.size(); ++i) {
            current = points.get(i);
            if (last == null) {
                path.moveTo(current.getX(), current.getY());
            } else {
                path.quadTo((current.getX() + last.getX()) / 2,
                        (current.getY() + last.getY()) / 2,
                        current.getX(), current.getY());

                drawPerp(canvas, last, current);
            }
            last = current;
        }
        canvas.drawPath(path, paint);

        surfaceHolder.unlockCanvasAndPost(canvas);
        points.clear();
        lastWidth = 1.0f;
        lastVelocity = 0f;
    }

    private static float velocity(final TouchPoint p1, final TouchPoint p2) {
        float dx = p1.getX() - p2.getX();
        float dy = p1.getY() - p2.getY();
        float dist = dx * dx + dy * dy;
        float ts = p2.getTimestamp() - p1.getTimestamp();
        if (Float.compare(ts, 0) == 0) {
            return dist;
        }
        return dist / ts;
    }

    private float nextWidth(final float vel) {
        if (vel > lastVelocity + 5.0f) {
            return lastWidth - 0.3f;
        } else if (vel < Math.abs(lastVelocity - 5.0f)) {
            return lastWidth + 0.3f;
        }
        return lastWidth;

    }

    private void drawPerp(final Canvas canvas, final TouchPoint p1, final TouchPoint p2) {

        Paint paint = new Paint();
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);

        final Line l1 = new Line(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        final Line l2 = new Line();
        final Line l3 = new Line();
        float vel = velocity(p1, p2);
        Log.e("###########", "velc " + vel);
        float width = nextWidth(vel);
        if (!Line.perp(l1, l2, l3, 6.0f)) {
            return;
        }
        lastWidth = width;
        lastVelocity = vel;

        if (true) {
            Path path = new Path();
            path.moveTo(l2.first.getX(), l2.first.getY());
            path.lineTo(l2.second.getX(), l2.second.getY());
            path.lineTo(l3.second.getX(), l3.second.getY());
            path.lineTo(l3.first.getX(), l3.first.getY());
            path.close();
            canvas.drawPath(path, paint);
        }

        if (false) {
            canvas.drawLine(l2.first.getX(), l2.first.getY(),
                    l2.second.getX(), l2.second.getY(),
                    paint);

            canvas.drawLine(l3.first.getX(), l3.first.getY(),
                    l3.second.getX(), l3.second.getY(),
                    paint);
        }
    }

    static public class Line {
        public TouchPoint first = new TouchPoint();
        public TouchPoint second = new TouchPoint();

        public Line() {
        }

        public Line(float x1, float y1, float x2, float y2) {
            first = new TouchPoint(x1, y1, 0, 0, 0);
            second = new TouchPoint(x2, y2, 0, 0, 0);
        }

        public Line(final TouchPoint p1, final TouchPoint p2) {
            set(p1, p2);
        }

        public void set(final TouchPoint p1, final TouchPoint p2) {
            first.set(p1);
            second.set(p2);
        }

        public static boolean perp(final Line line, final Line line1, final Line line2, float dist) {
            float dx = line.second.getX() - line.first.getX();
            float dy = line.second.getY() - line.first.getY();
            if (Float.compare(dx, 0) == 0 && Float.compare(dy, 0) == 0) {
                return false;
            }

            if (Float.compare(dx, 0) == 0) {
                TouchPoint p3 = new TouchPoint(line.first.getX() - dist, line.first.getY(), 0, 0, 0);
                TouchPoint p4 = new TouchPoint(line.first.getX() + dist, line.first.getY(), 0, 0, 0);
                line1.set(p3, p4);

                p3 = new TouchPoint(line.second.getX() - dist, line.second.getY(), 0, 0, 0);
                p4 = new TouchPoint(line.second.getX() + dist, line.second.getY(), 0, 0, 0);
                line2.set(p3, p4);
            } else if (Float.compare(dy, 0) == 0) {
                TouchPoint p3 = new TouchPoint(line.first.getX(), line.first.getY() - dist, 0, 0, 0);
                TouchPoint p4 = new TouchPoint(line.first.getX(), line.first.getY() + dist, 0, 0, 0);
                line1.set(p3, p4);

                p3 = new TouchPoint(line.second.getX(), line.second.getY() - dist, 0, 0, 0);
                p4 = new TouchPoint(line.second.getX(), line.second.getY() + dist, 0, 0, 0);
                line2.set(p3, p4);
            } else {

                double theta = Math.atan(-dx / dy);
                float xdist = (float) (dist * Math.cos(theta));
                float ydist = (float) (dist * Math.sin(theta));
                TouchPoint p3 = new TouchPoint(line.first.getX() - xdist, line.first.getY() - ydist, 0, 0, 0);
                TouchPoint p4 = new TouchPoint(line.first.getX() + xdist, line.first.getY() + ydist, 0, 0, 0);
                line1.set(p3, p4);

                p3 = new TouchPoint(line.second.getX() - xdist, line.second.getY() - ydist, 0, 0, 0);
                p4 = new TouchPoint(line.second.getX() + xdist, line.second.getY() + ydist, 0, 0, 0);
                line2.set(p3, p4);
            }
            return true;
        }


    }


}
