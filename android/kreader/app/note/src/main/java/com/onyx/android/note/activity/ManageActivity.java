package com.onyx.android.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.GotoUpAction;
import com.onyx.android.note.actions.LoadNoteListAction;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
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

    private TextView chooseModeButton, addFolderButton, cutButton, deleteButton;
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
        loadNoteList();
    }

    private void initNoteViewHelper() {
        getNoteViewHelper().stopDrawing();
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
        contentView = (ContentView) findViewById(R.id.note_content_view);
        contentView.setBlankAreaAnswerLongClick(false);
        contentView.setupGridLayout(getRows(), getColumns());
        contentView.setShowPageInfoArea(false);
        contentView.setCallback(new ContentView.ContentViewCallback() {
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
            }

        });
    }

    private void onNormalModeItemClick(final ContentItemView view) {
        final GObject object = view.getData();
        if (Utils.isLibrary(object)) {
            gotoLibrary(GAdapterUtil.getUniqueId(object));
            return;
        }

        if (Utils.isDocument(object)) {
            editExistingDocument(GAdapterUtil.getUniqueId(object));
            return;
        }

        if (Utils.isNew(object)) {
            createNewDocument();
            return;
        }

        if (Utils.isGotoUp(object)) {
            gotoUp();
            return;
        }

    }

    private void createNewDocument() {
        final Intent intent = new Intent(ManageActivity.this, ScribbleActivity.class);
        intent.putExtra(Utils.DOCUMENT_ID, ShapeUtils.generateUniqueId());
        intent.putExtra(Utils.ACTION_TYPE, Utils.ACTION_CREATE);
        startActivity(intent);
    }

    private void editExistingDocument(final String id) {
        final Intent intent = new Intent(ManageActivity.this, ScribbleActivity.class);
        intent.putExtra(Utils.DOCUMENT_ID, id);
        intent.putExtra(Utils.ACTION_TYPE, Utils.ACTION_EDIT);
        startActivity(intent);
    }

    private HashMap<String, Integer> getItemViewDataMap(@SelectionMode.SelectionModeDef int mode) {
        HashMap<String, Integer> mapping = new HashMap<String, Integer>();
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
        currentPage = contentView.getCurrentPage();
        currentSelectMode = selectionMode;
        switch (selectionMode) {
            case SelectionMode.NORMAL_MODE:
                switchToNormalMode();
                break;
            case SelectionMode.MULTISELECT_MODE:
                switchToMultiSelectionMode();
                break;
        }
    }

    private void switchToNormalMode() {
        contentView.setSubLayoutParameter(R.layout.scribble_item,
                getItemViewDataMap(SelectionMode.NORMAL_MODE));
        contentView.setupContent(getRows(), getColumns(), getCurrentAdapter(), 0, true);
        contentView.unCheckAllViews();
    }

    private void switchToMultiSelectionMode() {
        contentView.setSubLayoutParameter(R.layout.scribble_item,
                getItemViewDataMap(SelectionMode.MULTISELECT_MODE));
        contentView.setupContent(getRows(), getColumns(), getCurrentAdapter(), 0, true);
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

    private GAdapter getCurrentAdapter() {
        return adapter;
    }

    public void updateWithNoteList(final List<NoteModel> noteModelList) {
        contentView.setSubLayoutParameter(R.layout.scribble_item, getItemViewDataMap(currentSelectMode));
        adapter = Utils.adapterFromNoteModelList(noteModelList, R.drawable.ic_student_note_folder_gray_250dp,
                R.drawable.ic_student_note_pic_gray_250dp);
        adapter.addObject(0, Utils.createNewItem(Integer.toString(0), R.drawable.ic_student_note_plus_gray_250dp));
        contentView.setAdapter(adapter, 0);
    }



}
