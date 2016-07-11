package com.onyx.android.note.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.*;
import com.onyx.android.note.receiver.DeviceReceiver;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.note.data.PenType;
import com.onyx.android.note.dialog.BackGroundTypePopupMenu;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.dialog.PenColorPopupMenu;
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
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.io.File;
import java.util.HashMap;
import java.util.List;


/**
 * when any button clicked, flush at first and render page, after that always switch to drawing state.
 */
public class ScribbleActivity extends OnyxAppCompatActivity {
    static final String TAG = ScribbleActivity.class.getSimpleName();
    static final String TAG_NOTE_TITLE = "note_title";

    private SurfaceView surfaceView;
    private String activityAction;
    private String noteTitle;
    private
    @PenType.PenTypeDef
    int currentPenType = PenType.PENCIL;
    @NoteBackgroundType.NoteBackgroundDef
    private
    int currentNoteBackground = NoteBackgroundType.EMPTY;
    private float eraserRadius;
    //TODO:just as psd value(minValue 15/5 ->3,add 25 level).
    private int minPenWidth = 1;
    private int maxPenWidth = 20;
    private int currentPenWidth = 1;
    private int currentPenColor = Color.BLACK;
    private TextView titleTextView;
    private ContentView penStyleContentView;
    private GAdapter adapter;
    private PenWidthPopupMenu penWidthPopupMenu;
    private BackGroundTypePopupMenu bgTypePopupMenu;
    PenColorPopupMenu penColorPopupMenu;
    private ImageView addPageBtn, changeBGBtn, prevPage, nextPage, penColorBtn;
    private Button pageIndicator;
    private TouchPoint erasePoint = null;
    private ShapeDataInfo shapeDataInfo;
    private DeviceReceiver deviceReceiver = new DeviceReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        initToolbarButtons();
        registerDeviceReceiver();
    }

    public NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getNoteViewHelper();
    }

    private void registerDeviceReceiver() {
        deviceReceiver.setSystemUIChangeListener(new DeviceReceiver.SystemUIChangeListener() {
             @Override
             public void onSystemUIChanged(String type, boolean open) {
                 if (open) {
                     onSystemUIOpened();
                 } else {
                     onSystemUIClosed();
                 }
             }
        });
        deviceReceiver.registerReceiver(this);
    }

    private void unregisterDeviceReceiver() {
        deviceReceiver.unregisterReceiver(this);
    }

    private void onSystemUIOpened() {
        flushWithCallback(true, false, null);
    }

    private void onSystemUIClosed() {
        flushWithCallback(true, !shapeDataInfo.isInUserErasing(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateColorIndicator();
        initSurfaceView();
    }

    private void initToolbarButtons() {
        titleTextView = (TextView) findViewById(R.id.note_title);
        addPageBtn = (ImageView) findViewById(R.id.button_new_page);
        changeBGBtn = (ImageView) findViewById(R.id.change_note_bg);
        prevPage = (ImageView) findViewById(R.id.button_previous_page);
        nextPage = (ImageView) findViewById(R.id.button_next_page);
        penColorBtn = (ImageView) findViewById(R.id.color_indicator);
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
                onAddNewPage();
            }
        });
        changeBGBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBGSetupWindow();
            }
        });
        prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevPage();
            }
        });
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextPage();
            }
        });
        penColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorClicked();
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
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (bgTypePopupMenu == null) {
                    bgTypePopupMenu = new BackGroundTypePopupMenu(ScribbleActivity.this,
                            getLayoutInflater(),
                            currentNoteBackground,
                            getWindow().getDecorView(),
                            getWindow().getDecorView().getWidth() - getResources().getDimensionPixelSize(R.dimen.note_bg_popup_width) - 10,
                            getSupportActionBar().getHeight() + 10,
                            new BackGroundTypePopupMenu.PopupMenuCallback() {
                                @Override
                                public void onBackGroundChanged(@NoteBackgroundType.NoteBackgroundDef int newBackground) {
                                    currentNoteBackground = newBackground;
                                    bgTypePopupMenu.dismiss();
                                }
                            });
                    bgTypePopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            onBackgroundChanged(currentNoteBackground);
                        }
                    });
                }
                bgTypePopupMenu.show();
            }
        });
    }

    private void onBackgroundChanged(int newBackground) {
        final NoteBackgroundChangeAction changeBGAction = new NoteBackgroundChangeAction(newBackground);
        changeBGAction.execute(ScribbleActivity.this, null);
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
                clearSurfaceView();
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
            public void onNewTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                ScribbleActivity.this.onNewTouchPointListReceived(shape, pointList);
            }

            @Override
            public void onBeginErasing() {
                ScribbleActivity.this.onBeginErasing();
            }

            @Override
            public void onErasing(final MotionEvent touchPoint) {
                ScribbleActivity.this.onErasing(touchPoint);
            }

            @Override
            public void onEraseTouchPointListReceived(TouchPointList pointList) {
                ScribbleActivity.this.onFinishErasing(pointList);
            }
        };
    }

    private void onNewTouchPointListReceived(final Shape shape, TouchPointList pointList) {
        //final AddShapeInBackgroundAction<ScribbleActivity> action = new AddShapeInBackgroundAction<>(shape);
        //action.execute(this, null);
    }

    private void onBeginErasing() {
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                erasePoint = new TouchPoint();
            }
        });
    }

    private void onErasing(final MotionEvent touchPoint) {
        if (erasePoint == null) {
            return;
        }
        erasePoint.x = touchPoint.getX();
        erasePoint.y = touchPoint.getY();
        drawPage();
    }

    private void onFinishErasing(TouchPointList pointList) {
        erasePoint = null;
        drawPage();
        RemoveByPointListAction<ScribbleActivity> removeByPointListAction = new RemoveByPointListAction<>(pointList);
        removeByPointListAction.execute(this, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterDeviceReceiver();
        flushWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getNoteViewHelper().quit();
            }
        });

    }

    @Override
    protected void onDestroy() {
        cleanUpAllPopMenu();
        clearSurfaceView();
        super.onDestroy();
    }

    private void cleanUpAllPopMenu() {
        if (penWidthPopupMenu != null && penWidthPopupMenu.isShowing()) {
            penWidthPopupMenu.dismiss();
        }
        if (bgTypePopupMenu != null && bgTypePopupMenu.isShowing()) {
            bgTypePopupMenu.dismiss();
        }
        if (penColorPopupMenu != null && penColorPopupMenu.isShowing()) {
            penColorPopupMenu.dismiss();
        }
        penWidthPopupMenu = null;
        bgTypePopupMenu = null;
    }

    public void onBackPressed() {
        getNoteViewHelper().pauseDrawing();
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
                onDocumentClose(input);
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
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogNoteNameInput.show(getFragmentManager());
            }
        });
    }

    private void onDocumentClose(final String title) {
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentCloseAction<ScribbleActivity> closeAction = new DocumentCloseAction<>(title);
                closeAction.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void saveExistingNoteDocument() {
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentCloseAction<ScribbleActivity> closeAction = new DocumentCloseAction<>(noteTitle);
                closeAction.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void onPencilClicked() {
        flushWithCallback(true, true, null);
    }

    private void onRulerClicked() {
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (penWidthPopupMenu == null) {
                    penWidthPopupMenu = new PenWidthPopupMenu(ScribbleActivity.this, getLayoutInflater(), currentPenWidth, minPenWidth, maxPenWidth, getWindow().getDecorView(), 50, getWindow().getDecorView().getHeight() -
                            getResources().getDimensionPixelSize(R.dimen.sub_menu_height) -
                            getResources().getDimensionPixelSize(R.dimen.pen_width_popup_height) - 10, new PenWidthPopupMenu.PopupMenuCallback() {

                        @Override
                        public void onValueChanged(int newValue) {
                            currentPenWidth = newValue;
                            penWidthPopupMenu.dismiss();
                        }
                    });
                    penWidthPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            onStrokeWidthChanged(currentPenWidth);
                        }
                    });
                }
                penWidthPopupMenu.show();
            }
        });
    }

    private void onStrokeWidthChanged(final int newWidth) {
        flushWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                NoteStrokeWidthChangeAction action = new NoteStrokeWidthChangeAction(newWidth);
                action.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void onColorClicked() {
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (penColorPopupMenu == null) {
                    penColorPopupMenu = new PenColorPopupMenu(ScribbleActivity.this,
                            getLayoutInflater(),
                            getWindow().getDecorView(),
                            penColorBtn.getLeft() + (penColorBtn.getWidth() / 2) - (getResources().getDimensionPixelSize(R.dimen.pen_color_popup_width) / 2),
                            getWindow().getDecorView().getHeight() -
                                    getResources().getDimensionPixelSize(R.dimen.sub_menu_height) -
                                    getResources().getDimensionPixelSize(R.dimen.pen_color_popup_height) - 10,
                            new PenColorPopupMenu.PopupMenuCallback() {
                                @Override
                                public void onPenColorChanged(int newPenColor) {
                                    currentPenColor = newPenColor;
                                    penColorPopupMenu.dismiss();
                                    updateColorIndicator();
                                }
                            });
                    penColorPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            onColorChange(currentPenColor);
                        }
                    });
                }
                penColorPopupMenu.show();
            }
        });
    }

    private void onColorChange(final int currentPenColor){
        flushWithCallback(true, true, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final ChangePenColorAction changePenColorAction = new ChangePenColorAction(currentPenColor);
                changePenColorAction.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void onEraseClicked() {
        flushWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final ChangePenStateAction changePenStateAction = new ChangePenStateAction(NoteViewHelper.PenState.PEN_USER_ERASING);
                changePenStateAction.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void handleDocumentCreate(final String uniqueId, final String parentId) {
        final DocumentCreateAction<ScribbleActivity> action = new DocumentCreateAction<>(uniqueId, parentId);
        action.execute(this, null);
    }

    private void handleDocumentEdit(final String uniqueId, final String parentId) {
        final DocumentEditAction<ScribbleActivity> action = new DocumentEditAction<>(uniqueId, parentId);
        action.execute(this, null);
    }

    private void flushWithCallback(boolean render, boolean resume, final BaseCallback callback) {
        final List<Shape> stash = getNoteViewHelper().deatchStash();
        final DocumentFlushAction<ScribbleActivity> action = new DocumentFlushAction<>(stash, render, resume);
        action.execute(this, callback);
    }

    private void onAddNewPage() {
        flushWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentAddNewPageAction<ScribbleActivity> action = new DocumentAddNewPageAction<>(-1);
                action.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void onNextPage() {
        flushWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final GotoNextPageAction<ScribbleActivity> action = new GotoNextPageAction<>();
                action.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void onPrevPage() {
        flushWithCallback(false, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final GotoPrevPageAction<ScribbleActivity> action = new GotoPrevPageAction<>();
                action.execute(ScribbleActivity.this, null);
            }
        });
    }

    public void onRequestFinished(final BaseNoteRequest request, boolean updatePage) {
        if (request.isAbort()) {
            return;
        }
        updateDataInfo(request);
        if (updatePage) {
            drawPage();
        }
    }

    private void updateDataInfo(final BaseNoteRequest request) {
        shapeDataInfo = request.getShapeDataInfo();
        int currentPageIndex = shapeDataInfo.getCurrentPageIndex() + 1;
        int pageCount = shapeDataInfo.getPageCount();
        pageIndicator.setText(currentPageIndex + File.separator + pageCount);
        currentNoteBackground = shapeDataInfo.getBackground();
        currentPenWidth = (int) shapeDataInfo.getStrokeWidth();
        eraserRadius = shapeDataInfo.getEraserRadius();
    }

    private void clearSurfaceView() {
        Rect rect = getViewportSize();
        Canvas canvas = beforeDraw(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        cleanup(canvas, paint, rect);
        afterDraw(canvas);
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
        drawContent(canvas, paint);
        drawErasingIndicator(canvas, paint);
        afterDraw(canvas);
    }

    private Canvas beforeDraw(final Rect rect) {
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        return canvas;
    }

    private void afterDraw(final Canvas canvas) {
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private void drawContent(final Canvas canvas, final Paint paint) {
        Bitmap bitmap = getNoteViewHelper().getViewBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
    }

    private void drawErasingIndicator(final Canvas canvas, final Paint paint) {
        if (erasePoint == null || erasePoint.getX() <= 0 || erasePoint.getY() <= 0) {
            return;
        }

        float x = erasePoint.getX();
        float y = erasePoint.getY();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        canvas.drawCircle(x, y, eraserRadius, paint);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                onNextPage();
                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                onPrevPage();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateColorIndicator() {
        int targetColorIconRes;
        switch (currentPenColor) {
            case Color.BLACK:
                targetColorIconRes = R.drawable.ic_business_write_color_black_black_46dp;
                break;
            case Color.LTGRAY:
                targetColorIconRes = R.drawable.ic_business_write_color_gray_1_gray_46dp;
                break;
            case Color.GRAY:
                targetColorIconRes = R.drawable.ic_business_write_color_gray_2_gray_46dp;
                break;
            case Color.WHITE:
                targetColorIconRes = R.drawable.ic_business_write_color_white_46dp;
                break;
            default:
                targetColorIconRes = R.drawable.ic_business_write_color_black_black_46dp;
                break;
        }
        penColorBtn.setImageResource(targetColorIconRes);
    }
}
