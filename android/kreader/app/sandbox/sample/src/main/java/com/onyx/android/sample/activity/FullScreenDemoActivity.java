package com.onyx.android.sample.activity;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.onyx.android.sample.R;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.TreeObserverUtils;



import butterknife.Bind;
import butterknife.ButterKnife;

public class FullScreenDemoActivity extends AppCompatActivity  {

    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;

    private Handler handler = new Handler(Looper.getMainLooper());
    private int initColor = Color.WHITE;
    private boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_demo);
        DeviceUtils.setFullScreenOnResume(this, true);

        ButterKnife.bind(this);
        final ViewTreeObserver observer = surfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(surfaceView.getViewTreeObserver(), this);
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                final Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                holder.unlockCanvasAndPost(canvas);

                stop = false;
                triggerNext();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                stop = true;
            }
        });
    }


    private void triggerNext() {
        if (stop) {
            return;
        }
        EpdController.setViewDefaultUpdateMode(surfaceView, UpdateMode.GC);
        final Canvas canvas = surfaceView.getHolder().lockCanvas();
        Paint paint = new Paint();
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);
        int value = initColor;
        paint.setColor(value);
        for(int i = 0; i < canvas.getWidth(); ++i) {
            canvas.drawLine(i, 0, i, canvas.getHeight(), paint);
            value = (value ^ Color.WHITE) | 0xff000000;
            paint.setColor(value);
        }
        initColor = ((initColor ^ Color.WHITE) | 0xff000000);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                triggerNext();
            }
        }, 4000);
    }
}
