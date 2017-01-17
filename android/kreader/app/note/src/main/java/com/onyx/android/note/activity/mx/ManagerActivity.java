package com.onyx.android.note.activity.mx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onyx.android.note.NoteApplication;
import com.onyx.android.note.R;
import com.onyx.android.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.android.note.actions.manager.CreateLibraryAction;
import com.onyx.android.note.actions.manager.NoteMoveAction;
import com.onyx.android.note.actions.manager.RenameNoteOrLibraryAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.activity.onyx.SpanScribbleActivity;
import com.onyx.android.note.data.ScribbleMode;
import com.onyx.android.note.dialog.DialogChooseScribbleMode;
import com.onyx.android.note.dialog.DialogCreateNewFolder;
import com.onyx.android.note.dialog.DialogMoveFolder;
import com.onyx.android.note.dialog.DialogNoteNameInput;
import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.Utils;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.GAdapterUtil;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.android.sdk.ui.view.ContentView;

import java.util.List;

import static com.onyx.android.sdk.data.GAdapterUtil.getUniqueId;


public class ManagerActivity extends BaseManagerActivity {

    final static boolean ENABLE_BETA_SPANSCRIBBLE = false;
    private CheckedTextView chooseModeButton;
    private TextView addFolderButton, moveButton, deleteButton;
    private ImageView nextPageBtn, prevPageBtn;
    private LinearLayout controlPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoteApplication.initWithAppConfig(this);
        setContentView(R.layout.mx_activity_manager);
        initView();
        initNoteViewHelper();
    }

    private void initNoteViewHelper() {
        getNoteViewHelper().reset(contentView);
    }

    private void initView() {
        initSupportActionBarWithCustomBackFunction();
        getSupportActionBar().setTitle(ManagerActivity.class.getSimpleName());
        scribbleItemLayoutID = R.layout.mx_scribble_item;
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
              onItemDelete();
            }
        });
        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCreateNewFolder dlgCreateFolder = new DialogCreateNewFolder();
                dlgCreateFolder.setOnCreatedListener(new DialogCreateNewFolder.OnCreateListener() {
                    @Override
                    public boolean onCreated(final String title) {
                        final CheckNoteNameLegalityAction<ManagerActivity> action = new
                                CheckNoteNameLegalityAction<>(title, currentLibraryId, NoteModel.TYPE_LIBRARY, true, true);
                        action.execute(ManagerActivity.this, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                if (action.isLegal()) {
                                    final CreateLibraryAction<ManagerActivity> action =
                                            new CreateLibraryAction<>(getCurrentLibraryId(), title);
                                    action.execute(ManagerActivity.this);
                                } else {
                                    showNoteNameIllegal();
                                }
                            }
                        });
                        return true;
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
                            reNameAction.execute(ManagerActivity.this);
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

    @Override
    protected void startScribbleActivity(GObject object, final String parentId, final String action) {
        if (!isAlreadyToNewActivity) {
            startActivity(buildScribbleIntent(object, parentId, action, false));
            isAlreadyToNewActivity = true;
        }
    }

    @Override
    protected void createDocument(final GObject object) {
        if (!ENABLE_BETA_SPANSCRIBBLE) {
            super.createDocument(object);
        } else {
            DialogChooseScribbleMode dlgMode = new DialogChooseScribbleMode();
            dlgMode.setCallBack(new DialogChooseScribbleMode.Callback() {
                @Override
                public void onModeChosen(@ScribbleMode.ScribbleModeDef int mode) {
                    switch (mode) {
                        case ScribbleMode.MODE_NORMAL_SCRIBBLE:
                            startScribbleActivity(object, getCurrentLibraryId(), Utils.ACTION_CREATE);
                            break;
                        case ScribbleMode.MODE_SPAN_SCRIBBLE:
                            startActivity(new Intent(ManagerActivity.this, SpanScribbleActivity.class));
                            break;
                    }
                }
            });
            dlgMode.show(getFragmentManager());
        }
    }

    @Override
    protected void updateButtonsStatusByMode() {
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
                addFolderButton.setEnabled(false);
                chooseModeButton.setText(R.string.disselect);
                break;
            case SelectionMode.NORMAL_MODE:
                controlPanel.setVisibility(View.GONE);
                chooseModeButton.setText(R.string.select_mode);
                chooseModeButton.setChecked(false);
                addFolderButton.setEnabled(true);
                break;
        }
    }

    @Override
    public void updateUIWithNewNoteList(List<NoteModel> curLibSubContList) {
        contentView.setSubLayoutParameter(scribbleItemLayoutID, getItemViewDataMap(currentSelectMode));
        adapter = Utils.adapterFromNoteModelList(curLibSubContList, NoteAppConfig.sharedInstance(this).getFolderIconRes(),
                R.drawable.ic_student_note_pic_gray);
        adapter.addObject(0, Utils.createNewItem(getString(R.string.add_new_page), R.drawable.ic_business_write_add_box_gray_240dp));
        contentView.setupContent(getRows(), getColumns(), adapter, 0, true);
        contentView.updateCurrentPage();
        updateButtonsStatusByMode();
        updateActivityTitleAndIcon();
    }

    @Override
    protected void onResume() {
        // TODO:as mx request,we throw all state after leave this activity,and force to normal mode when we came back.
        currentSelectMode = SelectionMode.NORMAL_MODE;
        EpdController.invalidate(getWindow().getDecorView(), UpdateMode.GC);
        super.onResume();
    }

    @Override
    protected void updateActivityTitleAndIcon() {
        int iconRes = 0;
        String titleResString;
        if (currentLibraryId != null) {
            iconRes = R.drawable.title_back;
            titleResString = currentLibraryName;
            backFunctionLayout.setEnabled(true);
            backFunctionLayout.setClickable(true);
            backFunctionLayout.setFocusable(true);
        } else {
            iconRes = R.drawable.ic_business_write_pen_gray_34dp;
            titleResString = getString(R.string.app_name);
            backFunctionLayout.setEnabled(false);
            backFunctionLayout.setClickable(false);
            backFunctionLayout.setFocusable(false);
        }
        toolBarIcon.setImageResource(iconRes);
        toolBarTitle.setText(titleResString);
    }

    @Override
    public void showMovableFolderDialog(List<NoteModel> curLibSubContList) {
        final DialogMoveFolder dialogMoveFolder = new DialogMoveFolder();
        dialogMoveFolder.setDataList(curLibSubContList);
        dialogMoveFolder.setCallback(new DialogMoveFolder.DialogMoveFolderCallback() {
            @Override
            public void onMove(String targetParentId) {
                NoteMoveAction<BaseManagerActivity> noteMoveAction = new NoteMoveAction<>(targetParentId,
                        targetMoveIDList, true, true, true);
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
}
