package com.onyx.android.note.activity.onyx;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.manager.CreateLibraryAction;
import com.onyx.android.note.actions.manager.GetOldScribbleCountAction;
import com.onyx.android.note.actions.manager.ImportScribbleAction;
import com.onyx.android.note.actions.manager.LoadNoteListAction;
import com.onyx.android.note.actions.manager.ManageLoadPageAction;
import com.onyx.android.note.actions.manager.NoteMoveAction;
import com.onyx.android.note.actions.manager.RenameNoteOrLibraryAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.data.DataItemType;
import com.onyx.android.note.dialog.DialogCreateNewFolder;
import com.onyx.android.note.dialog.DialogMoveFolder;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.dialog.DialogSortBy;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.NotePreference;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.note.view.CheckableImageView;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.ui.dialog.OnyxCustomDialog;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.util.List;

import static com.onyx.android.sdk.data.GAdapterUtil.getUniqueId;


public class ManagerActivity extends BaseManagerActivity {
    static final String TAG = ManagerActivity.class.getCanonicalName();
    private CheckableImageView chooseModeButton;
    private ImageView addFolderButton;
    private ImageView moveButton;
    private ImageView deleteButton;
    private ImageView settingButton;
    private ImageView backupButton;
    private LinearLayout controlPanel;
    private @SortBy.SortByDef int currentSortBy = SortBy.CREATED_AT;
    private @AscDescOrder.AscDescOrderDef int ascOrder= AscDescOrder.DESC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.initWithAppConfig(this);
        setContentView(R.layout.onyx_activity_manager);
        loadSortByAndAsc();
        initView();
        initNoteViewHelper();
        checkOldScribbleData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this, NoteAppConfig.sharedInstance(this).useFullScreen());
        if (AppCompatUtils.isColorDevice(this)){
            Device.currentDevice().postInvalidate(getWindow().getDecorView(), UpdateMode.GC);
        }
    }
    
    private void loadSortByAndAsc() {
        currentSortBy = SortBy.translate(NotePreference.getIntValue(this, NotePreference.KEY_NOTE_SORT_BY, SortBy.CREATED_AT));
        ascOrder = AscDescOrder.translate(NotePreference.getIntValue(this, NotePreference.KEY_NOTE_ASC_ORDER, AscDescOrder.DESC));
    }

    private void checkOldScribbleData() {
        boolean imported = NotePreference.getBooleanValue(this, NotePreference.KEY_HAS_IMPORT_OLD_SCRIBBLE, false);
        boolean hasOpened = NotePreference.getBooleanValue(this, NotePreference.KEY_HAS_OPEN_IMPORT_OLD_SCRIBBLE_DIALOG, false);
        if (imported || hasOpened) {
            return;
        }
        final GetOldScribbleCountAction action = new GetOldScribbleCountAction();
        action.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                int count = action.getCount();
                if (count > 0) {
                    NotePreference.setBooleanValue(NotePreference.KEY_HAS_OPEN_IMPORT_OLD_SCRIBBLE_DIALOG, true);
                    OnyxCustomDialog.getConfirmDialog(ManagerActivity.this, getString(R.string.find_old_scribble), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            importScribbleData();
                        }
                    }, null).show();
                }
            }
        });
    }

    private void initNoteViewHelper() {
        getNoteViewHelper().reset(contentView);
    }

    private void initView() {
        initSupportActionBarWithCustomBackFunction();
        getSupportActionBar().setTitle(ManagerActivity.class.getSimpleName());
        getSupportActionBar().addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            //AppCompat would not called onOptionMenuClosed();
            // Use this listener to obtain menu visibility.
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible && currentSelectMode == SelectionMode.NORMAL_MODE) {
                    chosenItemsList.clear();
                }
            }
        });

        //TODO:temp hide icon for color devices.
        if (AppCompatUtils.isColorDevice(this)) {
            findViewById(R.id.imageView_main_title).setVisibility(View.GONE);
        }
        //disable choose mode function.if confirm remove,clean these code.
//        chooseModeButton = (CheckableImageView) findViewById(R.id.multi_select_mode);
//        chooseModeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseModeButton.setChecked(!chooseModeButton.isChecked());
//                switch (currentSelectMode) {
//                    case SelectionMode.NORMAL_MODE:
//                        switchMode(SelectionMode.MULTISELECT_MODE);
//                        break;
//                    case SelectionMode.MULTISELECT_MODE:
//                        switchMode(SelectionMode.NORMAL_MODE);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
        addFolderButton = (ImageView) findViewById(R.id.add_folder_btn);
        toolBarIcon = (ImageView) findViewById(R.id.imageView_main_title);
        toolBarTitle = (TextView) findViewById(R.id.textView_main_title);
        moveButton = (ImageView) findViewById(R.id.move_btn);
        deleteButton = (ImageView) findViewById(R.id.delete_btn);
        settingButton = (ImageView) findViewById(R.id.setting_btn);
        backupButton = (ImageView) findViewById(R.id.backup_restore_btn);
        controlPanel = (LinearLayout) findViewById(R.id.control_panel);
        ImageView sortByButton = (ImageView) findViewById(R.id.button_sort_by);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onItemDelete();
            }
        });
        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogCreateNewFolder dlgCreateFolder = new DialogCreateNewFolder();
                dlgCreateFolder.setOnCreatedListener(new DialogCreateNewFolder.OnCreateListener() {
                    @Override
                    public boolean onCreated(final String title) {
                        final CheckNoteNameLegalityAction<ManagerActivity> action =
                                new CheckNoteNameLegalityAction<>(title, currentLibraryId, NoteModel.TYPE_LIBRARY, true, true);
                        action.execute(ManagerActivity.this, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                if (action.isLegal()) {
                                    final CreateLibraryAction<ManagerActivity> action =
                                            new CreateLibraryAction<>(getCurrentLibraryId(), title);
                                    action.execute(ManagerActivity.this, new BaseCallback() {
                                        @Override
                                        public void done(BaseRequest request, Throwable e) {
                                            loadNoteList();
                                            dlgCreateFolder.dismiss();
                                        }
                                    });
                                } else {
                                    showNoteNameIllegal();
                                }
                            }
                        });
                        return false;
                    }
                });
                dlgCreateFolder.show(getFragmentManager());
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().openOptionsMenu();
            }
        });
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackupRestore();
            }
        });
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemMove();
            }
        });
        contentView = (ContentView) findViewById(R.id.note_content_view);
        contentView.setBlankAreaAnswerLongClick(false);
        contentView.setupGridLayout(getRows(), getColumns());
        contentView.setShowPageInfoArea(true);
        contentView.setInfoTittle(R.string.total);
        contentView.setSyncLoad(false);
        contentView.setCallback(getContentViewCallBack());
        sortByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSortBy();
            }
        });
    }

    @Override
    protected void renameNoteOrLibrary(final GObject object) {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.rename));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, object.getString(GAdapterUtil.TAG_ORIGIN_TITLE_STRING));
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, false);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(final String input) {
                //TODO:we trust upper method would only pass doc or lib to here.
                int itemType = Utils.isDocument(object) ? NoteModel.TYPE_DOCUMENT : NoteModel.TYPE_LIBRARY;
                final CheckNoteNameLegalityAction<ManagerActivity> action =
                        new CheckNoteNameLegalityAction<>(input, currentLibraryId, itemType, true, true);
                action.execute(ManagerActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            RenameNoteOrLibraryAction<ManagerActivity> reNameAction = new RenameNoteOrLibraryAction<>(getUniqueId(object), input);
                            reNameAction.execute(ManagerActivity.this, new BaseCallback() {
                                @Override
                                public void done(BaseRequest request, Throwable e) {
                                    dialogNoteNameInput.dismiss();
                                    loadNoteList();
                                }
                            });
                        } else {
                            showNoteNameIllegal();
                        }
                    }
                });
                return false;
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

    @Override
    protected void startScribbleActivity(GObject object, final String parentId, final String action) {
        if (!isAlreadyToNewActivity) {
            startActivity(buildScribbleIntent(object, parentId, action));
            isAlreadyToNewActivity = true;
        }
    }

    @Override
    protected void updateButtonsStatusByMode() {
        switch (currentSelectMode) {
            case SelectionMode.MULTISELECT_MODE:
                if (controlPanel != null)
                    controlPanel.setVisibility(View.VISIBLE);
                if (chosenItemsList.size() <= 0) {
                    deleteButton.setEnabled(false);
                    moveButton.setEnabled(false);
                } else {
                    deleteButton.setEnabled(true);
                    moveButton.setEnabled(true);
                }
                addFolderButton.setEnabled(false);
                chooseModeButton.setChecked(true);
                break;
            case SelectionMode.NORMAL_MODE:
                if (controlPanel != null)
                    controlPanel.setVisibility(View.GONE);
//                chooseModeButton.setChecked(false);
                addFolderButton.setEnabled(true);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.onyx_manager_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                onItemDelete();
                break;
            case R.id.export:
                break;
            case R.id.import_scribble:
                importScribbleData();
                break;
            case R.id.move:
                onItemMove();
                break;
            case R.id.rename:
                renameNoteOrLibrary(chosenItemsList.get(0));
                break;
            case R.id.setting:
                onSettings();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBackupRestore() {
        startActivity(new Intent(ManagerActivity.this,BackupRestoreActivity.class));
    }

    private void importScribbleData() {
        NotePreference.setBooleanValue(NotePreference.KEY_HAS_IMPORT_OLD_SCRIBBLE, true);
        final ImportScribbleAction<ManagerActivity> scribbleAction = new ImportScribbleAction();
        scribbleAction.execute(this, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    loadNoteList();
                }
            }
        });
    }

    private void onSettings() {
        startActivity(new Intent(ManagerActivity.this,SettingActivity.class));
    }

    private void onSortBy() {
        DialogSortBy dialogSortBy = new DialogSortBy();
        Bundle bundle = new Bundle();
        bundle.putInt(DialogSortBy.ARGS_SORT_BY,currentSortBy);
        bundle.putInt(DialogSortBy.ARGS_ASC,ascOrder);
        dialogSortBy.setArguments(bundle);
        dialogSortBy.setCallBack(new DialogSortBy.Callback() {
            @Override
            public void onSortBy(@SortBy.SortByDef int sortBy, @AscDescOrder.AscDescOrderDef int ascOrder) {
                currentSortBy = sortBy;
                ManagerActivity.this.ascOrder = ascOrder;
                loadNoteList();
                saveSortByAscArgs();
            }
        });
        dialogSortBy.show(getFragmentManager());
    }

    private void saveSortByAscArgs() {
        NotePreference.setIntValue(NotePreference.KEY_NOTE_SORT_BY, currentSortBy);
        NotePreference.setIntValue(NotePreference.KEY_NOTE_ASC_ORDER, ascOrder);
    }

    @Override
    public void loadNoteList() {
        final LoadNoteListAction<BaseManagerActivity> action = new LoadNoteListAction<>(getCurrentLibraryId(), currentSortBy, ascOrder);
        action.execute(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.export).setVisible(NoteAppConfig.sharedInstance(this).isEnableExport());
        if (NoteAppConfig.sharedInstance(this).useEduConfig()) {
            menu.findItem(R.id.import_scribble).setVisible(false);
        } else {
            menu.findItem(R.id.import_scribble).setVisible(!NoteAppConfig.sharedInstance(this).disableImport());
        }
        if (chosenItemsList.size() <= 0 ||
                (Utils.getItemType((chosenItemsList.get(0))) == DataItemType.TYPE_CREATE)) {
            menu.findItem(R.id.delete).setEnabled(false);
            menu.findItem(R.id.move).setEnabled(false);
            menu.findItem(R.id.export).setEnabled(false);
            menu.findItem(R.id.rename).setEnabled(false);
        } else {
            menu.findItem(R.id.move).setEnabled(true);
            menu.findItem(R.id.delete).setEnabled(true);
            menu.findItem(R.id.export).setEnabled(Utils.getItemType((chosenItemsList.get(0))) == DataItemType.TYPE_DOCUMENT);
            menu.findItem(R.id.rename).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected boolean onItemLongClicked(ContentItemView view) {
        switch (currentSelectMode) {
            case SelectionMode.NORMAL_MODE:
                if (!(Utils.getItemType(view.getData()) == DataItemType.TYPE_CREATE)) {
                    chosenItemsList.clear();
                    chosenItemsList.add(view.getData());
                    getSupportActionBar().openOptionsMenu();
                }
                return true;
        }
        return false;
    }

    @Override
    public void updateUIWithNewNoteList(List<NoteModel> curLibSubContList) {
        contentView.setSubLayoutParameter(scribbleItemLayoutID, getItemViewDataMap(currentSelectMode));
        adapter = Utils.adapterFromNoteModelList(curLibSubContList, NoteAppConfig.sharedInstance(this).getFolderIconRes(),
                R.drawable.ic_student_note_pic_gray);
        adapter.addObject(0, Utils.createNewItem(getString(R.string.add_new_page), R.drawable.ic_business_write_add_box_gray_240dp));
        contentView.setupContent(getRows(), getColumns(), adapter, 0, true);
        contentView.updateCurrentPage();
        contentView.setCustomInfo(getItemCountString(curLibSubContList),true);
        updateButtonsStatusByMode();
        updateActivityTitleAndIcon();
    }

    private String getItemCountString(List<NoteModel>curLibSunContList) {
        int directoryItemCount = 0;
        int fileItemCount = 0;

        for (NoteModel noteModel : curLibSunContList) {
            if (noteModel.isDocument()) {
                fileItemCount++;
            } else if (noteModel.isLibrary()){
                directoryItemCount++;
            }
        }

        return directoryItemCount
                + "/" + fileItemCount;
    }

    @Override
    protected void updateActivityTitleAndIcon() {
        String titleResString;
        if (currentLibraryId != null) {
            titleResString = currentLibraryPath;
        } else {
            titleResString = getString(R.string.app_name);
        }
        toolBarTitle.setText(titleResString);
    }

    @Override
    protected void beforeSetupItemData(ContentItemView view, GObject object) {
        if (object.isDummyObject()) {
            return;
        }
        switch (Utils.getItemType(object)) {
            case DataItemType.TYPE_DOCUMENT:
                view.setThumbnailScaleType(GAdapterUtil.TAG_THUMBNAIL, ImageView.ScaleType.FIT_XY);
                view.setImageViewBackGround(GAdapterUtil.TAG_THUMBNAIL, R.drawable.shadow);
                break;
            //TODO:temp to use no shadow bg for create item.need update new add icon.
            case DataItemType.TYPE_CREATE:
            case DataItemType.TYPE_LIBRARY:
                view.setThumbnailScaleType(GAdapterUtil.TAG_THUMBNAIL, ImageView.ScaleType.FIT_CENTER);
                view.setImageViewBackGround(GAdapterUtil.TAG_THUMBNAIL, 0);
                break;
        }
    }

    @Override
    public void showMovableFolderDialog(List<NoteModel> curLibSubContList) {
        final DialogMoveFolder dialogMoveFolder = new DialogMoveFolder();
        dialogMoveFolder.setDataList(curLibSubContList);
        dialogMoveFolder.setCallback(new DialogMoveFolder.DialogMoveFolderCallback() {
            @Override
            public void onMove(String targetParentId) {
                NoteMoveAction<BaseManagerActivity> noteMoveAction = new NoteMoveAction<>(targetParentId,
                        targetMoveIDList, true, false, false);
                noteMoveAction.execute(ManagerActivity.this, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        loadNoteList();
                        dialogMoveFolder.dismiss();
                        switchMode(SelectionMode.NORMAL_MODE);
                    }
                });
            }

            @Override
            public void onDismiss() {
                switchMode(SelectionMode.NORMAL_MODE);
            }
        });
        dialogMoveFolder.show(getFragmentManager());
    }

    @Override
    protected ContentView.ContentViewCallback getContentViewCallBack() {
        return new ContentView.ContentViewCallback() {
            @Override
            public void afterPageChanged(ContentView contentView, int newPage, int oldPage) {
            }

            @Override
            public void beforePageChanging(ContentView contentView, int newPage, int oldPage) {
                ManageLoadPageAction<ManagerActivity> loadPageAction = new ManageLoadPageAction<>(getPreloadIDList(newPage, false));
                loadPageAction.execute(ManagerActivity.this);
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
        };    }
}
