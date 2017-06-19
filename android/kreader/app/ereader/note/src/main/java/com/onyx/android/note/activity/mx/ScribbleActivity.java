package com.onyx.android.note.activity.mx;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.scribble.ClearAllFreeShapesAction;
import com.onyx.android.note.actions.scribble.DocumentDiscardAction;
import com.onyx.android.note.actions.scribble.DocumentEditAction;
import com.onyx.android.note.actions.scribble.DocumentSaveAction;
import com.onyx.android.note.actions.scribble.NoteBackgroundChangeAction;
import com.onyx.android.note.actions.scribble.RedoAction;
import com.onyx.android.note.actions.scribble.UndoAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
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
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.HashMap;
import java.util.List;


/**
 * when any button clicked, flush at first and render page, after that always switch to drawing state.
 */
public class ScribbleActivity extends BaseScribbleActivity {
    static final String TAG = ScribbleActivity.class.getCanonicalName();
    static final boolean TEMP_HIDE_ITEM = true;
    private
    @PenType.PenTypeDef
    int currentPenType = PenType.PENCIL;
    private
    @NoteBackgroundType.NoteBackgroundDef
    int currentNoteBackground = NoteBackgroundType.EMPTY;
    private TextView titleTextView;
    private ContentView penStyleContentView;
    private GAdapter adapter;
    private PenWidthPopupMenu penWidthPopupMenu;
    private BackGroundTypePopupMenu bgTypePopupMenu;
    PenColorPopupMenu penColorPopupMenu;
    private ImageView penColorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.initWithAppConfig(this);
        setContentView(R.layout.mx_activity_scribble);
        initSupportActionBarWithCustomBackFunction();
        initToolbarButtons();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateColorIndicator();
    }

    private void initToolbarButtons() {
        titleTextView = (TextView) findViewById(R.id.note_title);
        ImageView addPageBtn = (ImageView) findViewById(R.id.button_new_page);
        ImageView changeBGBtn = (ImageView) findViewById(R.id.change_note_bg);
        ImageView prevPage = (ImageView) findViewById(R.id.button_previous_page);
        ImageView nextPage = (ImageView) findViewById(R.id.button_next_page);
        RelativeLayout clearPage = (RelativeLayout) findViewById(R.id.clear_page);
        penColorBtn = (ImageView) findViewById(R.id.color_indicator);
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
                currentPenType = PenType.translate(Integer.decode(GAdapterUtil.getUniqueId(temp)));
                penStyleContentView.getCurrentAdapter().setObject(dataIndex, temp);
                penStyleContentView.unCheckOtherViews(dataIndex, true);
                penStyleContentView.updateCurrentPage();
                invokePenStyleCallBack(currentPenType);
            }
        });
        penStyleContentView.setupContent(1, 5, getPenStyleAdapter(), 0);
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
        clearPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        final OnyxAlertDialog confirmDialog = new OnyxAlertDialog();
                        confirmDialog.setParams(new OnyxAlertDialog.Params()
                                .setCanceledOnTouchOutside(false)
                                .setTittleString(getString(R.string.clear))
                                .setAlertMsgString(getString(R.string.clear_confirm))
                                .setCustomLayoutResID(R.layout.mx_custom_alert_dialog)
                                .setPositiveAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirmDialog.dismiss();
                                        onEraseClicked(false);
                                    }
                                })
                                .setNegativeAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirmDialog.dismiss();
                                        syncWithCallback(true, true, null);
                                    }
                                }));
                        confirmDialog.show(getFragmentManager(), "confirmDialog");
                    }
                });
            }
        });
        penColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorClicked();
            }
        });
        if (TEMP_HIDE_ITEM) {
            penColorBtn.setVisibility(View.GONE);
        }
    }

    private void invokePenStyleCallBack(int penType) {
        switch (penType) {
            case PenType.PENCIL:
                onPencilClicked();
                break;
            case PenType.OILY_PEN:
                onOilyPenClicked();
                break;
            case PenType.FOUNTAIN_PEN:
                onFountainPenClicked();
                break;
            case PenType.BRUSH:
                onBrushPenClicked();
                break;
            case PenType.RULER:
                onLineClicked();
                break;
            case PenType.ERASER:
                onEraseClicked(true);
                break;
        }
    }

    private void showBGSetupWindow() {
        syncWithCallback(true, false, new BaseCallback() {
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
        final NoteBackgroundChangeAction<ScribbleActivity> changeBGAction =
                new NoteBackgroundChangeAction<>(newBackground, !getNoteViewHelper().inUserErasing());
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
            if (!TEMP_HIDE_ITEM) {
                adapter.addObject(createPenItem(R.drawable.ic_business_write_rule_gray_70dp, PenType.RULER));
            }
            adapter.addObject(createPenItem(R.drawable.ic_business_write_eraser_gray_60dp, PenType.ERASER));
        }
        return adapter;
    }

    @Override
    protected void handleActivityIntent(final Intent intent) {
        super.handleActivityIntent(intent);
        titleTextView.setText(noteTitle);
    }

    @Override
    protected void cleanUpAllPopMenu() {
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

    @Override
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
            public boolean onConfirmAction(final String input) {
                final CheckNoteNameLegalityAction<ScribbleActivity> action = new
                        CheckNoteNameLegalityAction<>(input, parentID, NoteModel.TYPE_DOCUMENT, true, true);
                action.execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            onDocumentClose(input);
                        } else {
                            showNoteNameIllegal();
                        }
                    }
                });
                return true;
            }

            @Override
            public void onCancelAction() {
                dialogNoteNameInput.dismiss();
                Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        syncWithCallback(true, true, null);
                    }
                }, 500);
            }

            @Override
            public void onDiscardAction() {
                dialogNoteNameInput.dismiss();
                final DocumentDiscardAction<ScribbleActivity> discardAction = new DocumentDiscardAction<>(null);
                discardAction.execute(ScribbleActivity.this);
            }
        });
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogNoteNameInput.show(getFragmentManager());
            }
        });
    }

    private void onDocumentClose(final String title) {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentSaveAction<ScribbleActivity> closeAction = new DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), title, true);
                closeAction.execute(ScribbleActivity.this, null);
            }
        });
    }

    private void saveExistingNoteDocument() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final DocumentSaveAction<ScribbleActivity> closeAction = new DocumentSaveAction<>(shapeDataInfo.getDocumentUniqueId(), noteTitle, true);
                closeAction.execute(ScribbleActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        finish();
                    }
                });
            }
        });
    }

    private void onPencilClicked() {
        setCurrentShapeType(ShapeFactory.SHAPE_PENCIL_SCRIBBLE);
        setStrokeWidth(3.0f);
        syncWithCallback(true, true, null);
    }

    private void undo() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final UndoAction<ScribbleActivity> undoAction = new UndoAction<>();
                undoAction.execute(ScribbleActivity.this);

            }
        });
    }

    private void redo() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                final RedoAction<ScribbleActivity> redoAction = new RedoAction<>();
                redoAction.execute(ScribbleActivity.this);
            }
        });
    }

    private void onOilyPenClicked() {
        setCurrentShapeType(ShapeFactory.SHAPE_OILY_PEN_SCRIBBLE);
        setStrokeWidth(3.0f);
        syncWithCallback(true, true, null);
    }

    private void onFountainPenClicked() {
        setCurrentShapeType(ShapeFactory.SHAPE_FOUNTAIN_PEN_SCRIBBLE);
        setStrokeWidth(4.0f);
        syncWithCallback(true, true, null);
    }

    private void onBrushPenClicked() {
        setCurrentShapeType(ShapeFactory.SHAPE_BRUSH_SCRIBBLE);
        setStrokeWidth(6.0f);
        syncWithCallback(true, true, null);
    }

    private void onLineClicked() {
        setCurrentShapeType(ShapeFactory.SHAPE_LINE);
        setStrokeWidth(3.0f);
        syncWithCallback(true, false, null);
    }

    private void onColorClicked() {
        syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showColorMenu();
            }
        });
    }

    private void showColorMenu() {
        if (penColorPopupMenu == null) {
            penColorPopupMenu = new PenColorPopupMenu(ScribbleActivity.this,
                    getLayoutInflater(),
                    getWindow().getDecorView(),
                    penColorBtn.getLeft() + (penColorBtn.getWidth() / 2) - (getResources().getDimensionPixelSize(R.dimen.pen_color_popup_width) / 2),
                    getWindow().getDecorView().getHeight() -
                            getResources().getDimensionPixelSize(R.dimen.mx_note_menu_height) -
                            getResources().getDimensionPixelSize(R.dimen.pen_color_popup_height) - 10,
                    new PenColorPopupMenu.PopupMenuCallback() {
                        @Override
                        public void onPenColorChanged(int newPenColor) {
                            setStrokeColor(newPenColor);
                            penColorPopupMenu.dismiss();
                            updateColorIndicator();
                        }
                    });
            penColorPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    onColorChange(shapeDataInfo.getStrokeColor());
                }
            });
        }
        penColorPopupMenu.show();
    }

    private void onColorChange(final int currentPenColor) {
        setStrokeColor(currentPenColor);
        syncWithCallback(true, true, null);
    }

    private void onEraseClicked(boolean isPartialErase) {
        if (isPartialErase) {
            setCurrentShapeType(ShapeFactory.SHAPE_ERASER);
            syncWithCallback(true, false, null);
        } else {
            ClearAllFreeShapesAction<ScribbleActivity> action = new ClearAllFreeShapesAction<>();
            action.execute(this, null);
        }
    }

    @Override
    protected void updateDataInfo(final BaseNoteRequest request) {
        super.updateDataInfo(request);
        updatePenIndicator(shapeDataInfo);
        updateColorIndicator();
    }

    private int indexOf(int shapeType) {
        int pen = PenType.shapeToPen(shapeType);
        final List<GObject> list = getPenStyleAdapter().getList();
        for (int i = 0; i < list.size(); ++i) {
            int value = Integer.decode(GAdapterUtil.getUniqueId(list.get(i)));
            if (value == pen) {
                return i;
            }
        }
        return -1;
    }

    private void updatePenIndicator(final ShapeDataInfo shapeDataInfo) {
        int type = shapeDataInfo.getCurrentShapeType();
        int index = indexOf(type);
        if (index < 0) {
            return;
        }
        selectPenStyle(index);
    }

    private void selectPenStyle(int index) {
        final GObject object = penStyleContentView.getCurrentAdapter().get(index);
        object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        penStyleContentView.getCurrentAdapter().setObject(index, object);
        penStyleContentView.unCheckOtherViews(index, true);
        penStyleContentView.updateCurrentPage();
    }

    private GObject createPenItem(final int penIconRes, @PenType.PenTypeDef int penType) {
        GObject object = GAdapterUtil.createTableItem(0, 0, penIconRes, 0, null);
        object.putString(GAdapterUtil.TAG_UNIQUE_ID, Integer.toString(penType));
        if (penType == currentPenType) {
            object.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
        }
        return object;
    }

    private void updateColorIndicator() {
        int targetColorIconRes;
        switch (getCurrentShapeColor()) {
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


    @Override
    protected void handleDocumentEdit(String uniqueId, String parentId) {
        final DocumentEditAction<BaseScribbleActivity> action = new DocumentEditAction<>(uniqueId, parentId);
        //TODO:1)force edit mode resume with pencil as mx request.
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onPencilClicked();
            }
        });
    }
}
