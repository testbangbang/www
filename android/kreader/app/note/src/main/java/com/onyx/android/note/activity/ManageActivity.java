package com.onyx.android.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.CreateLibraryAction;
import com.onyx.android.note.actions.GotoUpAction;
import com.onyx.android.note.actions.LoadNoteListAction;
import com.onyx.android.note.actions.NoteLibraryRemoveAction;
import com.onyx.android.note.data.DataItemType;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ManageActivity extends OnyxAppCompatActivity {
    private static final String TAG_CONTENT_ID = "content_id";
    private static final String TAG_CONTENT_TAG = "content_tag";
    private SimpleDateFormat dateFormat;
    private
    @SelectionMode.SelectionModeDef
    int currentSelectMode = SelectionMode.NORMAL_MODE;
    private int currentPage;

    private TextView chooseModeButton, addFolderButton, moveButton, deleteButton;
    private ArrayList<GObject> chosenItemsList = new ArrayList<GObject>();
    private ContentView contentView;
    private GAdapter adapter;
    private String currentLibraryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNoteViewHelper();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNoteList();
    }

    private void initNoteViewHelper() {
        getNoteViewHelper().reset();
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

    private void initView() {
        initSupportActionBarWithCustomBackFunction();
        getSupportActionBar().setTitle(ManageActivity.class.getSimpleName());
        chooseModeButton = (TextView) findViewById(R.id.selectMode);
        addFolderButton = (TextView) findViewById(R.id.add_folder);
        moveButton = (TextView) findViewById(R.id.move);
        deleteButton = (TextView) findViewById(R.id.delete);
        chooseModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                new NoteLibraryRemoveAction(targetRemoveIDList).execute(ManageActivity.this);
                switchMode(SelectionMode.NORMAL_MODE);
            }
        });
        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bound = (int) (3 * Math.random());
                for (int i = 0; i < 3 + bound; i++) {
                    final CreateLibraryAction action = new CreateLibraryAction(getCurrentLibraryId(), Integer.toString(i));
                    action.execute(ManageActivity.this);
                }
            }
        });
        contentView = (ContentView) findViewById(R.id.note_content_view);
        contentView.setBlankAreaAnswerLongClick(false);
        contentView.setupGridLayout(getRows(), getColumns());
        contentView.setShowPageInfoArea(false);
        contentView.setCallback(new ContentView.ContentViewCallback() {
            @Override
            public void beforeSetupData(ContentItemView view, GObject object) {
                if (object.isDummyObject()) {
                    return;
                }
                switch (Utils.getItemType(object)) {
                    case DataItemType.TYPE_CREATE:
                    case DataItemType.TYPE_DOCUMENT:
                        view.setImageViewBackGround(GAdapterUtil.TAG_IMAGE_RESOURCE, R.drawable.image_border);
                        break;
                    case DataItemType.TYPE_LIBRARY:
                        view.setImageViewBackGround(GAdapterUtil.TAG_IMAGE_RESOURCE, 0);
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

        });
    }

    private void onNormalModeItemClick(final ContentItemView view) {
        final GObject object = view.getData();
        switch (Utils.getItemType(object)) {
            case DataItemType.TYPE_CREATE:
                editDocument(true);
                break;
            case DataItemType.TYPE_GOTO_UP:
                gotoUp();
                break;
            case DataItemType.TYPE_DOCUMENT:
                editDocument(false, GAdapterUtil.getUniqueId(object));
                break;
            case DataItemType.TYPE_LIBRARY:
                gotoLibrary(GAdapterUtil.getUniqueId(object));
                break;
            case DataItemType.TYPE_INVALID:
                break;
        }
    }

    private void editDocument(boolean isNew, String... id) {
        final Intent intent = Utils.getScribbleIntent(this);
        String targetID;
        if (isNew) {
            targetID = ShapeUtils.generateUniqueId();
        } else {
            targetID = id[0];
        }
        intent.putExtra(Utils.DOCUMENT_ID, targetID);
        startActivity(intent);
    }

    private HashMap<String, Integer> getItemViewDataMap(@SelectionMode.SelectionModeDef int mode) {
        HashMap<String, Integer> mapping = new HashMap<>();
        switch (mode) {
            case SelectionMode.PASTE_MODE:
            case SelectionMode.NORMAL_MODE:
                mapping = new HashMap<>();
                mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_bg);
                mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                break;
            case SelectionMode.MULTISELECT_MODE:
                mapping = new HashMap<>();
                mapping.put(GAdapterUtil.TAG_IMAGE_RESOURCE, R.id.imageview_bg);
                mapping.put(GAdapterUtil.TAG_TITLE_STRING, R.id.textview_title);
                mapping.put(GAdapterUtil.TAG_SELECTABLE, R.id.checkbox_multi_select);
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
        return 3;
    }

    private int getColumns() {
        return 3;
    }

    public void loadNoteList() {
        final LoadNoteListAction action = new LoadNoteListAction(getCurrentLibraryId());
        action.execute(this);
    }

    private void gotoUp() {
        final GotoUpAction action = new GotoUpAction(getCurrentLibraryId());
        action.execute(this);
    }

    private void gotoLibrary(final String id) {
        setCurrentLibraryId(id);
        loadNoteList();
    }

    public void updateWithNoteList(final List<NoteModel> noteModelList) {
        contentView.setSubLayoutParameter(R.layout.scribble_item, getItemViewDataMap(currentSelectMode));
        adapter = Utils.adapterFromNoteModelList(noteModelList, R.drawable.ic_student_note_folder_gray_250dp,
                R.drawable.ic_student_note_pic_gray_250dp);
        adapter.addObject(0, Utils.createNewItem(getString(R.string.add_new_page), R.drawable.ic_student_note_plus_gray_250dp));
        contentView.setupContent(getRows(), getColumns(), adapter, 0, true);
        contentView.updateCurrentPage();
        updateButtonsStatusByMode();
    }

    private void updateButtonsStatusByMode() {
        switch (currentSelectMode) {
            case SelectionMode.MULTISELECT_MODE:
                deleteButton.setVisibility(View.VISIBLE);
                moveButton.setVisibility(View.VISIBLE);
                if (chosenItemsList.size() <= 0) {
                    deleteButton.setEnabled(false);
                    moveButton.setEnabled(false);
                } else {
                    deleteButton.setEnabled(true);
                    moveButton.setEnabled(true);
                }
                break;
            case SelectionMode.NORMAL_MODE:
                deleteButton.setVisibility(View.GONE);
                moveButton.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLibraryId != null) {
            gotoUp();
        } else {
            super.onBackPressed();
        }
    }
}
