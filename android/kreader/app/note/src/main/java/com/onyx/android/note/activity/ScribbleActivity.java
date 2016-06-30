package com.onyx.android.note.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.*;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.RawInputProcessor;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import java.util.Date;

/**
 * when any button clicked, flush at first and render page, after that always switch to drawing state.
 */
public class ScribbleActivity extends OnyxAppCompatActivity {

    private SurfaceView surfaceView;
    private ImageView pencilButton;
    private ImageView eraseButton;
    private String activityAction;

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
                drawPage();
                getNoteViewHelper().setView(ScribbleActivity.this, surfaceView, inputCallback());
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
        activityAction = intent.getStringExtra(Utils.ACTION_TYPE);
        if (Utils.ACTION_CREATE.equals(activityAction)) {
            handleDocumentCreate(intent.getStringExtra(Utils.DOCUMENT_ID),
                    intent.getStringExtra(Utils.PARENT_LIBRARY_ID));
        } else if (Utils.ACTION_EDIT.equals(activityAction)) {
            handleDocumentEdit(intent.getStringExtra(Utils.DOCUMENT_ID),
                    intent.getStringExtra(Utils.PARENT_LIBRARY_ID));
        }
    }

    private RawInputProcessor.InputCallback inputCallback() {
        return new RawInputProcessor.InputCallback() {
            @Override
            public void onBeginHandWriting() {

            }

            @Override
            public void onNewTouchPointListReceived(TouchPointList pointList) {

            }

            @Override
            public void onBeginErasing() {
                ScribbleActivity.this.onBeginErasing();
            }

            @Override
            public void onErasing(TouchPoint touchPoint) {
                drawPage();
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {

            }
        };
    }

    private void onBeginErasing() {
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<ScribbleActivity>(getNoteViewHelper().deatchStash(), false);
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                drawPage();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        getNoteViewHelper().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onBackPressed() {
        getNoteViewHelper().stopDrawing();
        if (Utils.ACTION_CREATE.equals(activityAction)) {
            saveNewNoteDocument();
        } else {
            saveExistingNoteDocument();
        }
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

    private void saveNewNoteDocument() {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.save_note));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, Utils.getDateFormat(getResources().getConfiguration().locale).format(new Date()));
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, true);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(String input) {
                final DocumentSaveAndCloseAction<ScribbleActivity> closeAction = new DocumentSaveAndCloseAction<>(input);
                closeAction.execute(ScribbleActivity.this, null);
                return true;
            }

            @Override
            public void onCancelAction() {
                dialogNoteNameInput.dismiss();
            }

            @Override
            public void onDiscardAction() {
                dialogNoteNameInput.dismiss();
                final DocumentDiscardAction<ScribbleActivity> discardAction = new DocumentDiscardAction<>(null);
                discardAction.execute(ScribbleActivity.this, null);
            }
        });
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<ScribbleActivity>(getNoteViewHelper().deatchStash(), false);
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogNoteNameInput.show(getFragmentManager());
            }
        });
    }

    private void saveExistingNoteDocument() {
        final DocumentSaveAndCloseAction<ScribbleActivity> closeAction = new DocumentSaveAndCloseAction<>(null);
        closeAction.execute(this, null);
    }

    private void onPencilClicked() {
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<ScribbleActivity>(getNoteViewHelper().deatchStash(), true);
        action.execute(this, null);
    }

    private void onEraseClicked() {
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<ScribbleActivity>(getNoteViewHelper().deatchStash(), false);
        action.execute(this, null);
    }

    private void handleDocumentCreate(final String uniqueId, final String parentId) {
        final DocumentCreateAction<ScribbleActivity> action = new DocumentCreateAction<ScribbleActivity>(uniqueId, parentId);
        action.execute(this, null);
    }

    private void handleDocumentEdit(final String uniqueId, final String parentId) {
        final DocumentEditAction<ScribbleActivity> action = new DocumentEditAction<ScribbleActivity>(uniqueId, parentId);
        action.execute(this, null);
    }

    private void onAddNewPage() {
        final DocumentAddNewPageAction<ScribbleActivity> action = new DocumentAddNewPageAction<ScribbleActivity>();
        action.execute(this, null);
    }

    public void onRequestFinished(boolean updatePage) {
        if (updatePage) {
            drawPage();
        }
    }

    private void cleanup(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    public void drawPage() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        drawBackground(canvas, paint);
        drawContent(canvas, paint);
        afterDraw(canvas);
    }

    private Canvas beforeDraw(final Rect rect) {
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        return canvas;
    }

    private void afterDraw(final Canvas canvas) {
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawBackground(final Canvas canvas, final Paint paint) {
    }

    private void drawContent(final Canvas canvas, final Paint paint) {
        Bitmap bitmap = getNoteViewHelper().getShapeBitmap();
        if (bitmap != null) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    public Rect getViewportSize() {
        return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
    }
}
