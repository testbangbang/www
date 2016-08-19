package com.onyx.android.note.activity.onyx;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.manager.CreateLibraryAction;
import com.onyx.android.note.actions.manager.LoadNoteListAction;
import com.onyx.android.note.actions.manager.RenameNoteOrLibraryAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.data.DataItemType;
import com.onyx.android.note.dialog.DialogCreateNewFolder;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.dialog.DialogSortBy;
import com.onyx.android.note.utils.NotePreference;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.note.view.CheckableImageView;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.ContentItemView;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.List;

import static com.onyx.android.sdk.data.GAdapterUtil.getUniqueId;


public class ManagerActivity extends BaseManagerActivity {
    private CheckableImageView chooseModeButton;
    private ImageView addFolderButton, moveButton, deleteButton;
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
    }

    private void loadSortByAndAsc() {
        currentSortBy = SortBy.translate(NotePreference.getIntValue(this, NotePreference.KEY_NOTE_SORT_BY, SortBy.CREATED_AT));
        ascOrder = AscDescOrder.translate(NotePreference.getIntValue(this, NotePreference.KEY_NOTE_ASC_ORDER, AscDescOrder.DESC));
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
        chooseModeButton = (CheckableImageView) findViewById(R.id.multi_select_mode);
        addFolderButton = (ImageView) findViewById(R.id.add_folder_btn);
        toolBarIcon = (ImageView) findViewById(R.id.imageView_main_title);
        toolBarTitle = (TextView) findViewById(R.id.textView_main_title);
        moveButton = (ImageView) findViewById(R.id.move_btn);
        deleteButton = (ImageView) findViewById(R.id.delete_btn);
        ImageView nextPageBtn = (ImageView) findViewById(R.id.button_next_page);
        ImageView prevPageBtn = (ImageView) findViewById(R.id.button_previous_page);
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
                        final CheckNoteNameLegalityAction<ManagerActivity> action = new CheckNoteNameLegalityAction<>(title, currentLibraryId, false);
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
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemMove();
            }
        });
        contentView = (ContentView) findViewById(R.id.note_content_view);
        contentView.setBlankAreaAnswerLongClick(false);
        contentView.setupGridLayout(getRows(), getColumns());
        contentView.setShowPageInfoArea(false);
        contentView.setSyncLoad(false);
        contentView.setCallback(getContentViewCallBack());
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

    @Override
    protected void renameNoteOrLibrary(final GObject object) {
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.rename));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, object.getString(GAdapterUtil.TAG_TITLE_STRING));
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, false);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(final String input) {
                final CheckNoteNameLegalityAction<ManagerActivity> action = new CheckNoteNameLegalityAction<>(input, currentLibraryId, false);
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
                chooseModeButton.setChecked(false);
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
            case R.id.move:
                onItemMove();
                break;
            case R.id.rename:
                renameNoteOrLibrary(chosenItemsList.get(0));
                break;
            case R.id.sort:
                onSortBy();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        adapter = Utils.adapterFromNoteModelList(curLibSubContList, R.drawable.ic_student_note_folder_gray,
                R.drawable.ic_student_note_pic_gray);
        adapter.addObject(0, Utils.createNewItem(getString(R.string.add_new_page), R.drawable.ic_business_write_add_box_gray_240dp));
        contentView.setupContent(getRows(), getColumns(), adapter, 0, true);
        contentView.updateCurrentPage();
        updateButtonsStatusByMode();
        updateActivityTitleAndIcon();
    }

    @Override
    protected void updateActivityTitleAndIcon() {
        int iconRes = 0;
        String titleResString;
        ViewGroup.LayoutParams params = toolBarIcon.getLayoutParams();
        if (currentLibraryId != null) {
            iconRes = R.drawable.title_back;
            titleResString = currentLibraryName;
            params.height = getResources().getDimensionPixelSize(R.dimen.global_activities_back_btn_image_height);
            params.width = getResources().getDimensionPixelSize(R.dimen.global_activities_back_btn_image_width);
        } else {
            iconRes = R.drawable.ic_business_write_pen_gray_34dp;
            params.height = getResources().getDimensionPixelSize(R.dimen.global_activities_back_btn_category_image_height);
            params.width = getResources().getDimensionPixelSize(R.dimen.global_activities_back_btn_category_image_width);
            titleResString = getString(R.string.app_name);
        }
        toolBarIcon.setLayoutParams(params);
        toolBarIcon.setImageResource(iconRes);
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
}
