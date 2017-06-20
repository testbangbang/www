package com.onyx.android.note.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.manager.GotoUpAction;
import com.onyx.android.note.actions.manager.LoadNoteListAction;
import com.onyx.android.note.actions.manager.ManageLoadPageAction;
import com.onyx.android.note.actions.manager.NoteLibraryRemoveAction;
import com.onyx.android.note.actions.manager.NoteLoadMovableLibraryAction;
import com.onyx.android.note.data.DataItemType;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.android.note.activity.BaseScribbleActivity.TAG_NOTE_TITLE;
import static com.onyx.android.sdk.data.GAdapterUtil.hasThumbnail;

/**
 * Created by solskjaer49 on 16/8/3 12:24.
 */

public abstract class BaseManagerActivity extends OnyxAppCompatActivity implements ManagerInterface {
    protected ContentView contentView;
    protected String currentLibraryId;
    protected String currentLibraryName;
    protected String currentLibraryPath;
    protected ImageView toolBarIcon;
    protected TextView toolBarTitle;
    protected Button progressBtn;
    protected boolean isAlreadyToNewActivity = false;
    protected
    @SelectionMode.SelectionModeDef
    int currentSelectMode = SelectionMode.NORMAL_MODE;
    protected ArrayList<GObject> chosenItemsList = new ArrayList<>();
    protected ArrayList<String> targetMoveIDList = new ArrayList<>();
    protected GAdapter adapter;
    protected int currentPage;
    protected int scribbleItemLayoutID = R.layout.onyx_scribble_item;

    public void setLookupTable(Map<String, Integer> lookupTable) {
        this.lookupTable = lookupTable;
    }

    protected Map<String, Integer> lookupTable = new HashMap<>();

    protected void showNoteNameIllegal() {
        final OnyxAlertDialog illegalDialog = new OnyxAlertDialog();
        OnyxAlertDialog.Params params = new OnyxAlertDialog.Params().setTittleString(getString(R.string.noti))
                .setAlertMsgString(getString(R.string.note_name_already_exist))
                .setEnableNegativeButton(false).setCanceledOnTouchOutside(false);
        if (NoteAppConfig.sharedInstance(this).useMXUIStyle()) {
            params.setCustomLayoutResID(R.layout.mx_custom_alert_dialog);
        }
        illegalDialog.setParams(params);
        illegalDialog.show(getFragmentManager(), "illegalDialog");
    }

    protected NoteViewHelper getNoteViewHelper() {
        return NoteApplication.getInstance().getNoteViewHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAlreadyToNewActivity = false;
        loadNoteList();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN:
                if (contentView != null) {
                    contentView.nextPage();
                }
                return true;
            case KeyEvent.KEYCODE_PAGE_UP:
                if (contentView != null) {
                    contentView.prevPage();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    public ContentView getContentView() {
        return contentView;
    }

    @Override
    public Map<String, Integer> getLookupTable() {
        return lookupTable;
    }

    @Override
    public void updateCurLibID(String curLibID) {
        this.currentLibraryId = curLibID;
    }

    @Override
    public void updateCurLibName(String curLibName) {
        this.currentLibraryName = curLibName;
    }

    @Override
    public void updateCurLibPath(String curLibPath) {
        this.currentLibraryPath = curLibPath;
    }

    @Override
    public void loadNoteList() {
        final LoadNoteListAction<BaseManagerActivity> action = new LoadNoteListAction<>(getCurrentLibraryId());
        action.execute(this);
    }

    @Override
    public void submitRequest(BaseNoteRequest request, BaseCallback callback) {
        getNoteViewHelper().submit(this, request, callback);
    }

    @Override
    public void submitRequestWithIdentifier(String identifier, BaseNoteRequest request, BaseCallback callback) {
        getNoteViewHelper().submitRequestWithIdentifier(this, identifier, request, callback);
    }

    protected void updateTextViewPage() {
        progressBtn.setText((contentView.getCurrentPage() + 1) + File.separator + contentView.getTotalPageCount());
    }

    protected abstract void updateActivityTitleAndIcon();

    protected List<String> getPreloadIDList(int targetPage, boolean forceUpdate) {
        lookupTable.clear();
        final int begin = contentView.getPageBegin(targetPage);
        final int end = contentView.getPageEnd(targetPage);
        if (begin < 0 || end < 0 || GAdapterUtil.isNullOrEmpty(contentView.getCurrentAdapter())) {
            return null;
        }

        List<String> list = new ArrayList<>();
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

    public String getCurrentLibraryId() {
        return currentLibraryId;
    }

    public void setCurrentLibraryId(String currentLibraryId) {
        this.currentLibraryId = currentLibraryId;
    }

    public void setCurrentLibraryName(String currentLibraryName) {
        this.currentLibraryName = currentLibraryName;
    }

    protected ContentView.ContentViewCallback getContentViewCallBack() {
        return new ContentView.ContentViewCallback() {
            @Override
            public void afterPageChanged(ContentView contentView, int newPage, int oldPage) {
                updateTextViewPage();
            }

            @Override
            public void beforePageChanging(ContentView contentView, int newPage, int oldPage) {
                ManageLoadPageAction<BaseManagerActivity> loadPageAction = new ManageLoadPageAction<>(getPreloadIDList(newPage, false));
                loadPageAction.execute(BaseManagerActivity.this);
            }

            @Override
            public void beforeSetupData(ContentItemView view, GObject object) {
               beforeSetupItemData(view, object);
            }

            @Override
            public void onItemClick(ContentItemView view) {
                switch (currentSelectMode) {
                    case SelectionMode.NORMAL_MODE:
                        onNormalModeItemClick(view);
                        break;
                    case SelectionMode.MULTISELECT_MODE:
                        GObject temp = view.getData();
                        if (!(Utils.getItemType(temp) == DataItemType.TYPE_CREATE)) {
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
                        }
                        break;
                }
                updateButtonsStatusByMode();
            }

            @Override
            public boolean onItemLongClick(ContentItemView view) {
              return onItemLongClicked(view);
            }
        };
    }

    protected void onNormalModeItemClick(final ContentItemView view) {
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

    protected void editDocument(GObject object) {
        startScribbleActivity(object, getCurrentLibraryId(), Utils.ACTION_EDIT);
    }

    protected void createDocument(GObject object) {
        startScribbleActivity(object, getCurrentLibraryId(), Utils.ACTION_CREATE);
    }

    protected HashMap<String, Integer> getItemViewDataMap(@SelectionMode.SelectionModeDef int mode) {
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

    protected void switchMode(@SelectionMode.SelectionModeDef int selectionMode) {
        chosenItemsList.clear();
        currentPage = contentView.getCurrentPage();
        currentSelectMode = selectionMode;
        contentView.setSubLayoutParameter(scribbleItemLayoutID,
                getItemViewDataMap(selectionMode));
        if (selectionMode == SelectionMode.MULTISELECT_MODE) {
            contentView.unCheckOtherViews(0, false);
        } else {
            contentView.unCheckAllViews(true);
        }
        contentView.setupContent(getRows(), getColumns(), adapter, 0, true);
        updateButtonsStatusByMode();
    }

    private void gotoUp() {
        final GotoUpAction<BaseManagerActivity> action = new GotoUpAction<>(getCurrentLibraryId());
        action.execute(this);
    }

    private void gotoLibrary(final String id) {
        setCurrentLibraryId(id);
        loadNoteList();
    }

    protected int getRows() {
        //TODO:should use res file to control actual rows and cols.
        return 3;
    }

    protected int getColumns() {
        //TODO:should use res file to control actual rows and cols.
        return 4;
    }

    protected Intent buildScribbleIntent(GObject object, final String parentId, final String action) {
        return buildScribbleIntent(object, parentId, action, true);
    }

    protected Intent buildScribbleIntent(GObject object, final String parentId, final String action, boolean useDateAsNewDocumentTitle) {
        final Intent intent = NoteAppConfig.sharedInstance(this).getScribbleIntent(this);
        intent.putExtra(Utils.ACTION_TYPE, action);
        intent.putExtra(Utils.PARENT_LIBRARY_ID, parentId);
        String noteTitle = "";
        String uniqueID = "";
        if (action.equals(Utils.ACTION_CREATE)) {
            if (useDateAsNewDocumentTitle) {
                noteTitle = Utils.getDateFormat(getResources().getConfiguration().locale).format(new Date());
            }else {
                noteTitle = getString(R.string.new_document);
            }
            uniqueID = ShapeUtils.generateUniqueId();
        } else {
            noteTitle = StringUtils.isNullOrEmpty(object.getString(GAdapterUtil.TAG_TITLE_STRING)) ?
                    Utils.getDateFormat(getResources().getConfiguration().locale).format(new Date()) :
                    object.getString(GAdapterUtil.TAG_TITLE_STRING);
            uniqueID = GAdapterUtil.getUniqueId(object);
        }
        intent.putExtra(TAG_NOTE_TITLE, noteTitle);
        intent.putExtra(Utils.DOCUMENT_ID, uniqueID);
        return intent;
    }

    protected abstract void startScribbleActivity(GObject object, final String parentId, final String action);

    protected abstract void updateButtonsStatusByMode();

    protected abstract void renameNoteOrLibrary(final GObject object);

    protected boolean onItemLongClicked(ContentItemView view) {
        switch (currentSelectMode) {
            case SelectionMode.NORMAL_MODE:
                if (!(Utils.getItemType(view.getData()) == DataItemType.TYPE_CREATE)) {
                    renameNoteOrLibrary(view.getData());
                }
                return true;
        }
        return false;
    }

    protected void onItemDelete(){
        ArrayList<String> targetRemoveIDList = new ArrayList<>();
        for (GObject object : chosenItemsList) {
            targetRemoveIDList.add(GAdapterUtil.getUniqueId(object));
        }
        new NoteLibraryRemoveAction<>(targetRemoveIDList).execute(BaseManagerActivity.this);
        switchMode(SelectionMode.NORMAL_MODE);
    }

    protected void onItemMove() {
        targetMoveIDList = new ArrayList<>();
        for (GObject object : chosenItemsList) {
            targetMoveIDList.add(GAdapterUtil.getUniqueId(object));
        }
        ArrayList<String> excludeList = new ArrayList<>();
        excludeList.addAll(targetMoveIDList);
        NoteLoadMovableLibraryAction<BaseManagerActivity> action = new NoteLoadMovableLibraryAction<>(getCurrentLibraryId(), excludeList);
        action.execute(BaseManagerActivity.this);
    }

    protected void beforeSetupItemData(ContentItemView view, GObject object){
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
}
