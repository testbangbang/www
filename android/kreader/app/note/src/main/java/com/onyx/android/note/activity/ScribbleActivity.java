package com.onyx.android.note.activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

public class ScribbleActivity extends OnyxAppCompatActivity {

    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble);
        initSupportActionBarWithCustomBackFunction();
    }

    private NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getNoteViewHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView = (SurfaceView)findViewById(R.id.note_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                cleanup(surfaceView);
                getNoteViewHelper().setView(surfaceView);
                getNoteViewHelper().reset();
                getNoteViewHelper().startDrawing();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        getNoteViewHelper().reset();
    }

    private void cleanup(final SurfaceView surfaceView) {
        Rect rect = new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);

    }
}
