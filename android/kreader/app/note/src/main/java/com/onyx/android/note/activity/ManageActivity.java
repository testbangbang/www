package com.onyx.android.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.CreateLibraryAction;
import com.onyx.android.note.actions.GotoUpAction;
import com.onyx.android.note.actions.LoadNoteListAction;
import com.onyx.android.note.actions.ManageLoadPageAction;
import com.onyx.android.note.actions.NoteLibraryRemoveAction;
import com.onyx.android.note.actions.NoteLoadMovableLibraryAction;
import com.onyx.android.note.actions.NoteMoveAction;
import com.onyx.android.note.actions.RenameNoteOrLibraryAction;
import com.onyx.android.note.data.DataItemType;
import com.onyx.android.note.dialog.DialogCreateNewFolder;
import com.onyx.android.note.dialog.DialogMoveFolder;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.android.sdk.data.GAdapterUtil.getUniqueId;
import static com.onyx.android.sdk.data.GAdapterUtil.hasThumbnail;


public class ManageActivity extends OnyxAppCompatActivity {
    private static final String TAG_CONTENT_ID = "content_id";
    private static final String TAG_CONTENT_TAG = "content_tag";
    private SimpleDateFormat dateFormat;
    private
    @SelectionMode.SelectionModeDef
    int currentSelectMode = SelectionMode.NORMAL_MODE;
    private int currentPage;

    private CheckedTextView chooseModeButton;
    private TextView addFolderButton, moveButton, deleteButton;
    private ImageView nextPageBtn, prevPageBtn;
    private LinearLayout controlPanel;
    private Button progressBtn;
    private ArrayList<GObject> chosenItemsList = new ArrayList<>();
    private ArrayList<String> targetMoveIDList = new ArrayList<>();

    public ContentView getContentView() {
        return contentView;
    }

    private ContentView contentView;
    private GAdapter adapter;
    private String currentLibraryId;
    private ImageView toolBarIcon;
    private TextView toolBarTitle;
    private String currentLibraryName;
    private boolean isAlreadyToNewActivity = false;

    public Map<String, Integer> getLookupTable() {
        return lookupTable;
    }

    public void setLookupTable(Map<String, Integer> lookupTable) {
        this.lookupTable = lookupTable;
    }

    private Map<String, Integer> lookupTable = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        initView();
        initNoteViewHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAlreadyToNewActivity = false;
        loadNoteList();
    }

    private void initNoteViewHelper() {
        getNoteViewHelper().reset(contentView);
    }

    public NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getNoteViewHelper();
    }

    public String getCurrentLibraryId() {
        return currentLibraryId;
    }

    public void setCurrentLibraryId(String currentLibraryId) {
        this.currentLibraryId = currentLibraryId;
    }

    public void setCurrentLibraryName(String currentLibraryName) {
        this.currentLibraryName = currentLibraryName;
    }

    private void initView() {
        initSupportActionBarWithCustomBackFunction();
        getSupportActionBar().setTitle(ManageActivity.class.getSimpleName());
        chooseModeButton = (CheckedTextView) findViewById(R.id.selectMode);
        addFolderButton = (TextView) findViewById(R.id.add_folder);
        toolBarIcon = (ImageView) findViewById(R.id.imageView_main_title);
        toolBarTitle = (TextView) findViewById(R.id.textView_main_title);
        moveButton = (TextView) findViewById(R.id.move);
        deleteButton = (TextView) findViewById(R.id.delete);
        nextPageBtn = (ImageView) findViewById(R.id.button_next_page);
        prevPageBtn = (ImageView) findViewById(R.id.button_previous_page);
        progressBtn = (Button) findViewById(R.id.button_page_progress);
        controlPanel = (LinearLayout) findViewById(R.id.control_panel);
        chooseModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseModeButton.setChecked(!chooseModeButton.isChecked());
                switch (currentSelectMode) {
                    case SelectionMode.NORMAL_MODE:
                        switchMode(SelectionMode.MULTISELECT_MODE);
                        break;
                    case SelectionMode.MULTISELECT_MODE:
                        switchMode(SelectionMode.NORMAL_MODE);
                        break;
                    default:
                        break;
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:need distinguish doc/library.
                ArrayList<String> targetRemoveIDList = new ArrayList<>();
                for (GObject object : chosenItemsList) {
                    targetRemoveIDList.add(GAdapterUtil.getUniqueId(object));
                }
                new NoteLibraryRemoveAction(targetRemoveIDList).execute(ManageActivity.this, null);
                switchMode(SelectionMode.NORMAL_MODE);
            }
        });
        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreateNewFolder dlgCreateFolder = new DialogCreateNewFolder();
                dlgCreateFolder.setOnCreatedListener(new DialogCreateNewFolder.OnCreateListener() {
                    @Override
                    public void onCreated(final String title) {
                        final CheckNoteNameLegalityAction action = new CheckNoteNameLegalityAction(title);
                        action.execute(ManageActivity.this, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                if(action.isLegal()){
                                    final CreateLibraryAction action = new CreateLibraryAction(getCurrentLibraryId(), title);
                                    action.execute(ManageActivity.this, null);
                                }else {
                                    showNoteNameIllegal();
                                }
                            }
                        });
                    }
                });
                dlgCreateFolder.show(getFragmentManager());
            }
        });
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetMoveIDList = new ArrayList<>();
                for (GObject object : chosenItemsList) {
                    targetMoveIDList.add(GAdapterUtil.getUniqueId(object));
                }
                ArrayList<String> excludeList = new ArrayList<>();
                excludeList.addAll(targetMoveIDList);
                NoteLoadMovableLibraryAction action = new NoteLoadMovableLibraryAction(getCurrentLibraryId(), excludeList);
                action.execute(ManageActivity.this, null);
            }
        });
        contentView = (ContentView) findViewById(R.id.note_content_view);
        contentView.setBlankAreaAnswerLongClick(false);
        contentView.setupGridLayout(getRows(), getColumns());
        contentView.setShowPageInfoArea(false);
        contentView.setSyncLoad(false);
        contentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void afterPageChanged(ContentView contentView, int newPage, int oldPage) {
                updateTextViewPage();
            }

            @Override
            public void beforePageChanging(ContentView contentView, int newPage, int oldPage) {
                ManageLoadPageAction loadPageAction = new ManageLoadPageAction(getPreloadIDList(newPage, false));
                loadPageAction.execute(ManageActivity.this, null);
            }

            @Override
            public void beforeSetupData(ContentItemView view, GObject object) {
                if (object.isDummyObject()) {
                    return;
                }
                switch (Utils.getItemType(object)) {
                    case DataItemType.TYPE_DOCUMENT:
                        view.setThumbnailScaleType(GAdapterUtil.TAG_THUMBNAIL, ImageView.ScaleType.FIT_XY);
                        view.setImageViewBackGround(GAdapterUtil.TAG_THUMBNAIL, R.drawable.shadow);
                        break;
                    case DataItemType.TYPE_CREATE:
                    case DataItemType.TYPE_LIBRARY:
                        view.setThumbnailScaleType(GAdapterUtil.TAG_THUMBNAIL, ImageView.ScaleType.FIT_CENTER);
                        view.setImageViewBackGround(GAdapterUtil.TAG_THUMBNAIL, 0);
                        break;
                }
            }

            @Override
            public void onItemClick(ContentItemView view) {
                switch (currentSelectMode) {
                    case SelectionMode.NORMAL_MODE:
                        onNormalModeItemClick(view);
                        break;
                    case SelectionMode.MULTISELECT_MODE:
                        GObject temp = view.getData();
                        int dataIndex = adapter.getGObjectIndex(temp);
                        if (view.getData().getBoolean(GAdapterUtil.TAG_SELECTABLE, false)) {
                            temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, false);
                            chosenItemsList.remove(temp);
                        } else {
                            temp.putBoolean(GAdapterUtil.TAG_SELECTABLE, true);
                            chosenItemsList.add(temp);
                        }
                        adapter.setObject(dataIndex, temp);
                        contentView.updateCurrentPage();
                        contentView.setCustomInfo(Integer.toString(chosenItemsList.size()), true);
                        break;
                }
                updateButtonsStatusByMode();
            }

            @Override
            public boolean onItemLongClick(ContentItemView view) {
                switch (currentSelectMode) {
                    case SelectionMode.NORMAL_MODE:
                        renameNoteOrLibrary(view);
                        return true;
                }
                return super.onItemLongClick(view);
            }
        });
        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.prevPage();
            }
        });
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentView.nextPage();
            }
        });
    }

    private void renameNoteOrLibrary(final ContentItemView view) {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.rename));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, view.getData().getString(GAdapterUtil.TAG_TITLE_STRING));
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, false);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(final String input) {
                final CheckNoteNameLegalityAction action = new CheckNoteNameLegalityAction(input);
                action.execute(ManageActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            RenameNoteOrLibraryAction reNameAction = new RenameNoteOrLibraryAction(getUniqueId(view.getData()), input);
                            reNameAction.execute(ManageActivity.this, null);
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
            }

            @Override
            public void onDiscardAction() {

            }
        });
        dialogNoteNameInput.show(getFragmentManager());
    }

    private void showNoteNameIllegal() {
        final OnyxAlertDialog illegalDialog = new OnyxAlertDialog();
        illegalDialog.setParams(new OnyxAlertDialog.Params().setTittleString(getString(R.string.noti))
                .setCustomLayoutResID(R.layout.mx_custom_alert_dialog)
                .setAlertMsgString(getString(R.string.note_name_already_exist))
                .setEnableNegativeButton(false).setCanceledOnTouchOutside(false));
        illegalDialog.show(getFragmentManager(),"illegalDialog");
    }

    private void onNormalModeItemClick(final ContentItemView view) {
        final GObject object = view.getData();
        switch (Utils.getItemType(object)) {
            case DataItemType.TYPE_CREATE:
                createDocument(object);
                break;
            case DataItemType.TYPE_GOTO_UP:
                gotoUp();
                break;
            case DataItemType.TYPE_DOCUMENT:
                editDocument(object);
                break;
            case DataItemType.TYPE_LIBRARY:
                gotoLibrary(GAdapterUtil.getUniqueId(object));
                break;
            case DataItemType.TYPE_INVALID:
                break;
        }
    }


    private void editDocument(GObject object) {
        startScribbleActivity(object, getCurrentLibraryId(), Utils.ACTION_EDIT);
    }

    private void createDocument(GObject object) {
        startScribbleActivity(object, getCurrentLibraryId(), Utils.ACTION_CREATE);
    }

    private void startScribbleActivity(GObject object, final String parentId, final String action) {
        if (!isAlreadyToNewActivity) {
            final Intent intent = Utils.getScribbleIntent(this);
            intent.putExtra(Utils.ACTION_TYPE, action);
            intent.putExtra(Utils.PARENT_LIBRARY_ID, parentId);
            String noteTitle = "";
            String uniqueID = "";
            if (action.equals(Utils.ACTION_CREATE)) {
                noteTitle = getString(R.string.new_document);
                uniqueID = ShapeUtils.generateUniqueId();
            } else {
                noteTitle = StringUtils.isNullOrEmpty(object.getString(GAdapterUtil.TAG_TITLE_STRING)) ?
                        Utils.getDateFormat(getResources().getConfiguration().locale).format(new Date()) :
                        object.getString(GAdapterUtil.TAG_TITLE_STRING);
                uniqueID = GAdapterUtil.getUniqueId(object);
            }
            intent.putExtra(ScribbleActivity.TAG_NOTE_TITLE, noteTitle);
            intent.putExtra(Utils.DOCUMENT_ID, uniqueID);
            startActivity(intent);
            isAlreadyToNewActivity = true;
        }
    }

    private HashMap<String, Integer> getItemViewDataMap(@SelectionMode.SelectionModeDef int mode) {
        HashMap<String, Integer> mapping = new HashMap<>();
        switch (mode) {
            case SelectionMode.PASTE_MODE:
            case SelectionMode.NORMAL_MODE:
                mapping = new HashMap<>();
                mapping.put(GAdapterUtil.TAG_THUMBNAIL, R.id.imageview_bg);
                mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                mapping.put(GAdapterUtil.TAG_SUB_TITLE_STRING, R.id.textview_date);
                break;
            case SelectionMode.MULTISELECT_MODE:
                mapping = new HashMap<>();
                mapping.put(GAdapterUtil.TAG_THUMBNAIL, R.id.imageview_bg);
                mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.checkbox_multi_select);
                mapping.put(GAdapterUtil.TAG_SUB_TITLE_STRING, R.id.textview_date);
                break;
        }
        return mapping;
    }

    private void switchMode(@SelectionMode.SelectionModeDef int selectionMode) {
        chosenItemsList.clear();
        currentPage = contentView.getCurrentPage();
        currentSelectMode = selectionMode;
        contentView.setSubLayoutParameter(R.layout.scribble_item,
                getItemViewDataMap(selectionMode));
        contentView.unCheckAllViews();
        contentView.setupContent(getRows(), getColumns(), adapter, 0, true);
        updateButtonsStatusByMode();
    }

    private int getRows() {
        //TODO:should use res file to control actual rows and cols.
        return 3;
    }

    private int getColumns() {
        //TODO:should use res file to control actual rows and cols.
        return 4;
    }

    public void loadNoteList() {
        final LoadNoteListAction action = new LoadNoteListAction(getCurrentLibraryId());
        action.execute(this, null);
    }

    private void gotoUp() {
        final GotoUpAction action = new GotoUpAction(getCurrentLibraryId());
        action.execute(this, null);
    }

    private void gotoLibrary(final String id) {
        setCurrentLibraryId(id);
        loadNoteList();
    }

    public void updateWithNoteList(final List<NoteModel> noteModelList) {
        contentView.setSubLayoutParameter(R.layout.scribble_item, getItemViewDataMap(currentSelectMode));
        adapter = Utils.adapterFromNoteModelList(noteModelList, R.drawable.ic_student_note_folder_gray,
                R.drawable.ic_student_note_pic_gray);
        adapter.addObject(0, Utils.createNewItem(getString(R.string.add_new_page), R.drawable.ic_business_write_add_box_gray_240dp));
        contentView.setupContent(getRows(), getColumns(), adapter, 0, true);
        contentView.updateCurrentPage();
        updateButtonsStatusByMode();
        updateActivityTitleAndIcon();
    }

    private void updateButtonsStatusByMode() {
        switch (currentSelectMode) {
            case SelectionMode.MULTISELECT_MODE:
                controlPanel.setVisibility(View.VISIBLE);
                if (chosenItemsList.size() <= 0) {
                    deleteButton.setEnabled(false);
                    moveButton.setEnabled(false);
                } else {
                    deleteButton.setEnabled(true);
                    moveButton.setEnabled(true);
                }
                chooseModeButton.setText(R.string.disselect);
                break;
            case SelectionMode.NORMAL_MODE:
                controlPanel.setVisibility(View.GONE);
                chooseModeButton.setText(R.string.select_mode);
                chooseModeButton.setChecked(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentSelectMode != SelectionMode.NORMAL_MODE) {
            switchMode(SelectionMode.NORMAL_MODE);
            return;
        }
        if (currentLibraryId != null) {
            gotoUp();
        } else {
            super.onBackPressed();
        }
    }

    public void showMoveFolderDialog(final List<NoteModel> libraryList) {
        final DialogMoveFolder dialogMoveFolder = new DialogMoveFolder();
        dialogMoveFolder.setDataList(libraryList);
        dialogMoveFolder.setCallback(new DialogMoveFolder.DialogMoveFolderCallback() {
            @Override
            public void onMove(String targetParentId) {
                NoteMoveAction noteMoveAction = new NoteMoveAction<>(targetParentId, targetMoveIDList);
                noteMoveAction.execute(ManageActivity.this, null);
                dialogMoveFolder.dismiss();
                switchMode(SelectionMode.NORMAL_MODE);
            }

            @Override
            public void onDismiss() {
                switchMode(SelectionMode.NORMAL_MODE);
            }
        });
        dialogMoveFolder.show(getFragmentManager());
    }

    private void updateTextViewPage() {
        progressBtn.setText((contentView.getCurrentPage() + 1) + File.separator + contentView.getTotalPageCount());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                contentView.nextPage();
                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                contentView.prevPage();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateActivityTitleAndIcon() {
        int iconRes = 0;
        String titleResString;
        if (currentLibraryId != null) {
            iconRes = R.drawable.title_back;
            titleResString = currentLibraryName;
        } else {
            iconRes = R.drawable.ic_business_write_pen_gray_34dp;
            titleResString = getString(R.string.app_name);
        }
        toolBarIcon.setImageResource(iconRes);
        toolBarTitle.setText(titleResString);
    }

    private List<String> getPreloadIDList(int targetPage, boolean forceUpdate) {
        lookupTable.clear();
        final int begin = contentView.getPageBegin(targetPage);
        final int end = contentView.getPageEnd(targetPage);
        if (begin < 0 || end < 0 || GAdapterUtil.isNullOrEmpty(contentView.getCurrentAdapter())) {
            return null;
        }

        List<String> list = new ArrayList<String>();
        for (int i = begin; i <= end && i < contentView.getCurrentAdapter().size(); ++i) {
            GObject object = contentView.getCurrentAdapter().get(i);
            if (!hasThumbnail(object) || forceUpdate) {
                final String key = GAdapterUtil.getUniqueId(object);
                if (!StringUtils.isNullOrEmpty(key)) {
                    lookupTable.put(key, i);
                    list.add(key);
                }
            }
        }
        return list;
    }
}
