package com.onyx.android.sample.activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.onyx.android.sample.R;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class EpdcTestActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epdc_test);
        surfaceView = (SurfaceView)findViewById(R.id.epdc_surfaceview);
        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                clearSurface();
                trigger();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    private void clearSurface() {
        final Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void trigger() {
        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return epdcTest();
            }})
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer o) throws Exception {
                        trigger();
                    }
                });

    }

    private int epdcTest() {
        int limit = TestUtils.randInt(1000, 5000);
        int round = 0;
        while (round++ < limit) {
            EpdController.enablePost(surfaceView, 0);
            int count = TestUtils.randInt(10, 500);
            for (int i = 0; i < count; ++i) {
                epdcDrawLine();
            }
            EpdController.enablePost(surfaceView, 1);
            clearSurface();
        }
        return limit;
    }

    private void epdcDrawLine() {
        int x1 = TestUtils.randInt(1, surfaceView.getWidth() - 1);
        int y1 = TestUtils.randInt(1, surfaceView.getHeight() - 1);
        int x2 = x1 + TestUtils.randInt(1, 20);
        int y2 = y1 + TestUtils.randInt(1, 20);
        int width = TestUtils.randInt(1, 3);
        EpdController.moveTo(surfaceView, x1, y1, width);
        EpdController.quadTo(surfaceView, x2, y2, UpdateMode.DU);

    }
}
