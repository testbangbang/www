package com.onyx.jdread.reader.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.onyx.jdread.JDReadApplication;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class ReaderViewHelper {
    private SurfaceView readPageView;
    private Paint paint = new Paint();
    private static final int DEFAULT_MULTIPLEX = 1;
    public float dpiMultiplex = 1.0f;

    public ReaderViewHelper() {
        initData();
    }

    private void initData() {
        paint.setColor(Color.BLACK);
        paint.setTextSize(11 * (JDReadApplication.getInstance() != null ? dpiMultiplex : DEFAULT_MULTIPLEX));
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        paint.setStrokeWidth(0);
    }

    public SurfaceView getReadPageView() {
        return readPageView;
    }

    public void setReadPageView(SurfaceView readPageView) {
        this.readPageView = readPageView;
    }

    public int getPageViewWidth(){
        return readPageView.getWidth();
    }

    public int getPageViewHeight(){
        return readPageView.getHeight();
    }

    public void draw(Bitmap bitmap){
        if(readPageView == null){
            return;
        }
        if(bitmap == null){
            return;
        }
        paint.setDither(true);
        Rect rect = new Rect(readPageView.getLeft(), readPageView.getTop(),
                readPageView.getRight(), readPageView.getBottom());
        Canvas canvas = readPageView.getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        readPageView.getHolder().unlockCanvasAndPost(canvas);
    }
}
