package com.onyx.android.note.activity;

import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.DocumentCreateAction;
import com.onyx.android.note.actions.DocumentEditAction;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

public class ScribbleActivity extends OnyxAppCompatActivity {

    private SurfaceView surfaceView;
    private ImageView pencilButton;
    private ImageView eraseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble);
        initToolbar();
        initSupportActionBarWithCustomBackFunction();
    }

    public NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getNoteViewHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView)findViewById(R.id.note_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                cleanup(surfaceView);
                getNoteViewHelper().setView(surfaceView);
                handleActivityIntent(getIntent());
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    private void handleActivityIntent(final Intent intent) {
        if (!intent.hasExtra(Utils.ACTION_TYPE)) {
            return;
        }
        final String action = intent.getStringExtra(Utils.ACTION_TYPE);
        if (Utils.ACTION_CREATE.equals(action)) {
            handleDocumentCreate(intent.getStringExtra(Utils.DOCUMENT_ID));
        } else if (Utils.ACTION_EDIT.equals(action)) {
            handleDocumentEdit(intent.getStringExtra(Utils.DOCUMENT_ID));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getNoteViewHelper().stop();
    }

    private void initToolbar() {
        pencilButton = (ImageView)findViewById(R.id.pencil_button);
        pencilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPencilClicked();
            }
        });

        eraseButton = (ImageView)findViewById(R.id.erase_button);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEraseClicked();
            }
        });
    }

    private void cleanup(final SurfaceView surfaceView) {
        Rect rect = getViewportSize();
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

    private void onPencilClicked() {
        NoteApplication.getNoteViewHelper().startDrawing();
    }

    private void onEraseClicked() {
        // reset and render page.
        NoteApplication.getNoteViewHelper().stopDrawing();
    }

    public void startDrawing() {
        getNoteViewHelper().startDrawing();
    }

    private void handleDocumentCreate(final String uniqueId) {
        final DocumentCreateAction<ScribbleActivity> action = new DocumentCreateAction<ScribbleActivity>(uniqueId);
        action.execute(this);
    }

    private void handleDocumentEdit(final String uniqueId) {
        final DocumentEditAction<ScribbleActivity> action = new DocumentEditAction<ScribbleActivity>(uniqueId);
        action.execute(this);
    }

    public void onRequestFinished() {
        drawPage();
        startDrawing();
    }

    public void drawPage() {
        Rect rect = getViewportSize();
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);


        Bitmap bitmap = getNoteViewHelper().getShapeBitmap();
        if (bitmap != null) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    public Rect getViewportSize() {
        return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
    }
}
