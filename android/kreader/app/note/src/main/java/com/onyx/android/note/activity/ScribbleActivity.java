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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.DocumentAddNewPageAction;
import com.onyx.android.note.actions.DocumentCreateAction;
import com.onyx.android.note.actions.DocumentDiscardAction;
import com.onyx.android.note.actions.DocumentEditAction;
import com.onyx.android.note.actions.DocumentFlushAction;
import com.onyx.android.note.actions.DocumentSaveAndCloseAction;
import com.onyx.android.note.data.NoteBackgroundType;
import com.onyx.android.note.data.PenType;
import com.onyx.android.note.dialog.BackGroundTypePopupMenu;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.dialog.PenWidthPopupMenu;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.RawInputProcessor;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;


/**
 * when any button clicked, flush at first and render page, after that always switch to drawing state.
 */
public class ScribbleActivity extends OnyxAppCompatActivity {
    static final String TAG_NOTE_TITLE = "note_title";

    private SurfaceView surfaceView;
    private String activityAction;
    private String noteTitle;
    private
    @PenType.PenTypeDef
    int currentPenType = PenType.PENCIL;
    @NoteBackgroundType.NoteBackgroundDef
    int currentNoteBackground = NoteBackgroundType.EMPTY;
    //TODO:just as psd value.
    int minPenWidth = 15;
    int maxPenWidth = 40;
    int currentPenWidth = 32;
    private TextView titleTextView;
    private ContentView penStyleContentView;
    private GAdapter adapter;
    PenWidthPopupMenu penWidthPopupMenu;
    BackGroundTypePopupMenu bgTypePopupMenu;
    private ImageView addPageBtn, changeBGBtn;
    private Button pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        initView();
    }

    public NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getNoteViewHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSurfaceView();
    }

    private void initView() {
        titleTextView = (TextView) findViewById(R.id.note_title);
        addPageBtn = (ImageView) findViewById(R.id.button_new_page);
        changeBGBtn = (ImageView) findViewById(R.id.change_note_bg);
        //TODO:update page status by this widget.
        pageIndicator = (Button) findViewById(R.id.button_page_progress);
        penStyleContentView = (ContentView) findViewById(R.id.pen_style_content_view);
        penStyleContentView.setShowPageInfoArea(false);
        penStyleContentView.setSubLayoutParameter(R.layout.pen_style_item, getItemViewDataMap());
        penStyleContentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void onItemClick(ContentItemView view) {
                GObject temp = view.getData();
                int dataIndex = penStyleContentView.getCurrentAdapter().getGObjectIndex(temp);
                temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                currentPenType = Integer.decode(GAdapterUtil.getUniqueId(temp));
                penStyleContentView.getCurrentAdapter().setObject(dataIndex, temp);
                penStyleContentView.unCheckOtherViews(dataIndex, true);
                penStyleContentView.updateCurrentPage();
                invokePenStyleCallBack(currentPenType);
            }
        });
        penStyleContentView.setupContent(1, 6, getPenStyleAdapter(), 0);
        addPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        changeBGBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBGSetupWindow();
            }
        });
    }

    private void invokePenStyleCallBack(int penType) {
        switch (penType) {
            case PenType.PENCIL:
                onPencilClicked();
                break;
            case PenType.OILY_PEN:
                break;
            case PenType.FOUNTAIN_PEN:
                break;
            case PenType.BRUSH:
                break;
            case PenType.RULER:
                onRulerClicked();
                break;
            case PenType.ERASER:
                onEraseClicked();
                break;
        }
    }

    private void showBGSetupWindow() {
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<>(getNoteViewHelper().deatchStash(), false);
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (bgTypePopupMenu == null) {
                    bgTypePopupMenu = new BackGroundTypePopupMenu(ScribbleActivity.this, getLayoutInflater(), currentNoteBackground, getWindow().getDecorView(), getWindow().getDecorView().getWidth() -
                            getResources().getDimensionPixelSize(R.dimen.note_bg_popup_width) - 10,
                            getSupportActionBar().getHeight() + 10, new BackGroundTypePopupMenu.PopupMenuCallback() {
                        @Override
                        public void onBackGroundChanged(@NoteBackgroundType.NoteBackgroundDef int newBackground) {

                        }
                    });
                    bgTypePopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            resumeWriting();
                        }
                    });
                }
                bgTypePopupMenu.show();
            }
        });
    }

    private void onRulerClicked() {
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<>(getNoteViewHelper().deatchStash(), false);
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (penWidthPopupMenu == null) {
                    penWidthPopupMenu = new PenWidthPopupMenu(ScribbleActivity.this, getLayoutInflater(), currentPenWidth, minPenWidth, maxPenWidth, getWindow().getDecorView(), 50, getWindow().getDecorView().getHeight() -
                            getResources().getDimensionPixelSize(R.dimen.sub_menu_height) -
                            getResources().getDimensionPixelSize(R.dimen.pen_width_popup_height) - 10, new PenWidthPopupMenu.PopupMenuCallback() {

                        @Override
                        public void onValueChanged(int newValue) {

                        }
                    });
                    penWidthPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            resumeWriting();
                        }
                    });
                }
                penWidthPopupMenu.show();
            }
        });
    }

    private HashMap<String, Integer> getItemViewDataMap() {
        HashMap<String, Integer> mapping = new HashMap<>();
        mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.pen_img);
        mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.pen_indicator);
        return mapping;
    }

    private GAdapter getPenStyleAdapter() {
        if (adapter == null) {
            adapter = new GAdapter();
            adapter.addObject(createPenItem(R.drawable.ic_business_write_pencil_black_70dp, PenType.PENCIL));
            adapter.addObject(createPenItem(R.drawable.ic_business_write_marker_gray_70dp, PenType.OILY_PEN));
            adapter.addObject(createPenItem(R.drawable.ic_business_write_pen_gray_70dp, PenType.FOUNTAIN_PEN));
            adapter.addObject(createPenItem(R.drawable.ic_business_write_brush_gray_70dp, PenType.BRUSH));
            adapter.addObject(createPenItem(R.drawable.ic_business_write_rule_gray_70dp, PenType.RULER));
            adapter.addObject(createPenItem(R.drawable.ic_business_write_eraser_gray_60dp, PenType.ERASER));
        }
        return adapter;
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.note_view);
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
        noteTitle = intent.getStringExtra(TAG_NOTE_TITLE);
        titleTextView.setText(noteTitle);
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
        cleanUpAllPopMenu();
        super.onDestroy();
    }

    private void cleanUpAllPopMenu() {
        if (penWidthPopupMenu != null && penWidthPopupMenu.isShowing()) {
            penWidthPopupMenu.dismiss();
        }
        if (bgTypePopupMenu != null && bgTypePopupMenu.isShowing()) {
            bgTypePopupMenu.dismiss();
        }
        penWidthPopupMenu = null;
        bgTypePopupMenu = null;
    }

    public void onBackPressed() {
        getNoteViewHelper().stopDrawing();
        if (Utils.ACTION_CREATE.equals(activityAction)) {
            saveNewNoteDocument();
        } else {
            saveExistingNoteDocument();
        }
    }

    private void saveNewNoteDocument() {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.save_note));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, noteTitle);
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
        final DocumentSaveAndCloseAction<ScribbleActivity> closeAction = new DocumentSaveAndCloseAction<>(noteTitle);
        closeAction.execute(this, null);
    }

    private void resumeWriting() {
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<ScribbleActivity>(getNoteViewHelper().deatchStash(), true);
        action.execute(this, null);
    }

    private void onPencilClicked() {
        resumeWriting();
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

    private GObject createPenItem(final int penIconRes, @PenType.PenTypeDef int penType) {
        GObject object = GAdapterUtil.createTableItem(0, 0, penIconRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, Integer.toString(penType));
        if (penType == currentPenType) {
            object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        }
        return object;
    }
}
