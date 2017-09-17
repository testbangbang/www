package com.onyx.edu.note.manager;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteLoadMovableLibraryRequest;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.edu.note.NoteApplication;
import com.onyx.edu.note.R;
import com.onyx.edu.note.actions.common.CheckNoteNameLegalityAction;
import com.onyx.edu.note.actions.manager.ImportScribbleAction;
import com.onyx.edu.note.actions.manager.NoteLoadMovableLibraryAction;
import com.onyx.edu.note.actions.manager.NoteMoveAction;
import com.onyx.edu.note.actions.manager.RenameNoteOrLibraryAction;
import com.onyx.edu.note.data.ScribbleAction;
import com.onyx.edu.note.databinding.ActivityManagerBinding;
import com.onyx.edu.note.scribble.ScribbleActivity;
import com.onyx.edu.note.ui.ViewModelHolder;
import com.onyx.edu.note.ui.dialog.DialogMoveFolder;
import com.onyx.edu.note.ui.dialog.DialogNoteNameInput;
import com.onyx.edu.note.ui.dialog.DialogSortBy;
import com.onyx.edu.note.util.Constant;
import com.onyx.edu.note.util.NotePreference;
import com.onyx.edu.note.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * activity and ui-fragment should only handle ui logic.
 * should not do database logic in these class.
 * just send all work logic to view model class.
 */
public class ManagerActivity extends OnyxAppCompatActivity implements ManagerNavigator, ManagerItemNavigator {
    static final String TAG = ManagerActivity.class.getSimpleName();
    public static final String MANAGER_VIEW_MODEL_TAG = "MANAGER_VIEW_MODEL_TAG";
    ActivityManagerBinding mBinding;
    private ManagerViewModel mViewModel;
    DialogCreateNewFolder dlgCreateFolder;
    List<String> mPendingOperationIDList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        resetEpd();
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        cleanUpAllDialog();
        resetEpd();
        super.onDestroy();
    }

    private void resetEpd() {
        EpdController.resetEpdPost();
    }

    private void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manager);
        initSupportActionBarWithCustomBackFunction();
        actionBar.addOnMenuVisibilityListener(new ActionBar.OnMenuVisibilityListener() {
            //AppCompat would not called onOptionMenuClosed();
            // Use this listener to obtain menu visibility.
            @Override
            public void onMenuVisibilityChanged(boolean isVisible) {
                if (!isVisible) {
                    mPendingOperationIDList.clear();
                }
            }
        });
        ManagerFragment noteFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();
        mViewModel.setNavigator(this);
        mBinding.setViewModel(mViewModel);

        // Link View and ViewModel
        noteFragment.setViewModel(mViewModel);
        backFunctionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoUp();
            }
        });
        mBinding.addFolderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFolderDialog();
            }
        });
        mBinding.buttonSortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortByDialog();
            }
        });
    }

    private void showAddFolderDialog() {
        dlgCreateFolder = new DialogCreateNewFolder();
        dlgCreateFolder.setOnCreatedListener(new DialogCreateNewFolder.OnCreateListener() {
            @Override
            public void onCreated(final String title) {
                mViewModel.addFolder(title);
            }
        });
        dlgCreateFolder.show(getFragmentManager());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                onGoUp();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                mViewModel.deleteNote(mPendingOperationIDList);
                break;
            case R.id.move:
                final ArrayList<String> targetList = new ArrayList<>();
                targetList.addAll(mPendingOperationIDList);
                NoteLoadMovableLibraryAction action = new NoteLoadMovableLibraryAction(mViewModel.getCurrentNoteModelUniqueID(),
                        targetList);
                action.execute(NoteApplication.getInstance().getNoteManager(), new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        NoteLoadMovableLibraryRequest req = (NoteLoadMovableLibraryRequest) request;
                        showMovableFolderDialog(req.getNoteList(), targetList);
                    }
                });
                break;
            case R.id.rename:
                renameNoteOrLibrary(mPendingOperationIDList.get(0));
                break;
            case R.id.export:
                break;
            case R.id.import_scribble:
                importScribbleData();
                break;
            case R.id.setting:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mPendingOperationIDList.size() <= 0 ||
                mPendingOperationIDList.get(0).equalsIgnoreCase(Long.toString(Long.MIN_VALUE))) {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.move).setVisible(false);
            menu.findItem(R.id.export).setVisible(false);
            menu.findItem(R.id.rename).setVisible(false);
        } else {
            menu.findItem(R.id.move).setVisible(true);
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.export).setVisible(isIDDocument(mPendingOperationIDList.get(0)));
            menu.findItem(R.id.rename).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isIDDocument(String uniqueID) {
        return NoteDataProvider.load(uniqueID).isDocument();
    }

    private boolean isIDFolder(String uniqueID) {
        return NoteDataProvider.load(uniqueID).isLibrary();
    }

    private ManagerViewModel findOrCreateViewModel() {
        // In a configuration change we might have a ViewModel present. It's retained using the
        // Fragment Manager.
        @SuppressWarnings("unchecked")
        ViewModelHolder<ManagerViewModel> retainedViewModel =
                (ViewModelHolder<ManagerViewModel>) getSupportFragmentManager()
                        .findFragmentByTag(MANAGER_VIEW_MODEL_TAG);
        if (retainedViewModel != null && retainedViewModel.getViewModel() != null) {
            // If the model was retained, return it.
            return retainedViewModel.getViewModel();
        } else {
            // There is no ViewModel yet, create it.
            ManagerViewModel viewModel = new ManagerViewModel(this);
            // and bind it to this Activity's lifecycle using the Fragment Manager.
            Utils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    ViewModelHolder.createContainer(viewModel),
                    MANAGER_VIEW_MODEL_TAG);
            return viewModel;
        }
    }

    @NonNull
    private ManagerFragment findOrCreateViewFragment() {
        ManagerFragment noteFragment =
                (ManagerFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (noteFragment == null) {
            // Create the fragment
            noteFragment = ManagerFragment.newInstance();
            Utils.addFragmentToActivity(
                    getSupportFragmentManager(), noteFragment, R.id.contentFrame);
        }
        return noteFragment;
    }

    private void importScribbleData() {
        NotePreference.setBooleanValue(NotePreference.KEY_HAS_IMPORT_OLD_SCRIBBLE, true);
        final ImportScribbleAction scribbleAction = new ImportScribbleAction(this);
        scribbleAction.execute(NoteApplication.getInstance().getNoteManager(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    mViewModel.loadData();
                }
            }
        });
    }

    private void showMovableFolderDialog(List<NoteModel> curLibSubContList, final List<String> targetList) {
        final DialogMoveFolder dialogMoveFolder = new DialogMoveFolder();
        dialogMoveFolder.setDataSet(curLibSubContList);
        dialogMoveFolder.setCallback(new DialogMoveFolder.DialogMoveFolderCallback() {
            @Override
            public void onMove(String targetParentId) {
                NoteMoveAction noteMoveAction = new NoteMoveAction(targetParentId,
                        targetList, false, false, false);
                noteMoveAction.execute(NoteApplication.getInstance().getNoteManager(), new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e == null) {
                            mViewModel.loadData();
                            dialogMoveFolder.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onDismiss() {
            }
        });
        dialogMoveFolder.show(getFragmentManager());
    }

    protected void renameNoteOrLibrary(final String operationID) {
        final NoteModel targetModel = NoteDataProvider.load(operationID);
        final DialogNoteNameInput dialogNoteNameInput = new DialogNoteNameInput();
        Bundle bundle = new Bundle();
        bundle.putString(DialogNoteNameInput.ARGS_TITTLE, getString(R.string.rename));
        bundle.putString(DialogNoteNameInput.ARGS_HINT, targetModel.getTitle());
        bundle.putBoolean(DialogNoteNameInput.ARGS_ENABLE_NEUTRAL_OPTION, false);
        dialogNoteNameInput.setArguments(bundle);
        dialogNoteNameInput.setCallBack(new DialogNoteNameInput.ActionCallBack() {
            @Override
            public boolean onConfirmAction(final String input) {
                //TODO:we trust upper method would only pass doc or lib to here.
                int itemType = targetModel.isDocument() ? NoteModel.TYPE_DOCUMENT : NoteModel.TYPE_LIBRARY;
                final CheckNoteNameLegalityAction action =
                        new CheckNoteNameLegalityAction(input, mViewModel.getCurrentNoteModelUniqueID(), itemType,
                                true, false);
                action.execute(NoteApplication.getInstance().getNoteManager(), new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (action.isLegal()) {
                            RenameNoteOrLibraryAction reNameAction = new RenameNoteOrLibraryAction(operationID, input);
                            reNameAction.execute(NoteApplication.getInstance().getNoteManager(), new BaseCallback() {
                                @Override
                                public void done(BaseRequest request, Throwable e) {
                                    dialogNoteNameInput.dismiss();
                                    mViewModel.loadData();
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

    private void showSortByDialog() {
        DialogSortBy dialogSortBy = new DialogSortBy();
        Bundle bundle = new Bundle();
        bundle.putInt(DialogSortBy.ARGS_SORT_BY, mViewModel.getCurrentSortBy());
        dialogSortBy.setArguments(bundle);
        dialogSortBy.setCallBack(new DialogSortBy.Callback() {
            @Override
            public void onSortBy(@SortBy.SortByDef int sortBy, @AscDescOrder.AscDescOrderDef int ascOrder) {
                mViewModel.saveSortByAscArgs(sortBy, ascOrder);
                mViewModel.loadData();
            }
        });
        dialogSortBy.show(getFragmentManager());
    }

    private void showNoteNameIllegal() {
        OnyxAlertDialog illegalDialog = new OnyxAlertDialog();
        OnyxAlertDialog.Params params = new OnyxAlertDialog.Params().setTittleString(getString(R.string.noti))
                .setAlertMsgString(getString(R.string.note_name_already_exist))
                .setEnableNegativeButton(false).setCanceledOnTouchOutside(false);
        illegalDialog.setParams(params);
        illegalDialog.show(getFragmentManager(), "illegalDialog");
    }

    private void cleanUpAllDialog() {
        if (dlgCreateFolder != null && dlgCreateFolder.isVisible()) {
            dlgCreateFolder.dismiss();
        }
    }

    @Override
    public void showNewFolderTitleIllegal() {
        final OnyxAlertDialog illegalDialog = new OnyxAlertDialog();
        OnyxAlertDialog.Params params = new OnyxAlertDialog.Params().setTittleString(getString(R.string.noti))
                .setAlertMsgString(getString(R.string.note_name_already_exist))
                .setEnableNegativeButton(false).setCanceledOnTouchOutside(false);
        illegalDialog.setParams(params);
        illegalDialog.show(getFragmentManager(), "illegalDialog");
    }

    @Override
    public void updateFolderCreateStatus(boolean succeed) {
        dlgCreateFolder.dismiss();
        if (succeed) {
            mViewModel.loadData();
        } else {
            Toast.makeText(this, R.string.create_folder_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateNoteRemoveStatus(boolean succeed) {
        if (succeed) {
            mViewModel.loadData();
        } else {
            Toast.makeText(this, R.string.remove_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void addNewNote() {
        Log.e(TAG, "addNewNote: ");
        Intent intent = new Intent(this, ScribbleActivity.class);
        intent.putExtra(Constant.SCRIBBLE_ACTION_TAG, ScribbleAction.CREATE);
        intent.putExtra(Constant.NOTE_ID_TAG, ShapeUtils.generateUniqueId());
        intent.putExtra(Constant.NOTE_PARENT_ID_TAG, mViewModel.getCurrentNoteModelUniqueID());
        startActivity(intent);
    }

    @Override
    public void editNote(String uniqueID) {
        Log.e(TAG, "editNote: " + uniqueID);
        Intent intent = new Intent(this, ScribbleActivity.class);
        intent.putExtra(Constant.SCRIBBLE_ACTION_TAG, ScribbleAction.EDIT);
        intent.putExtra(Constant.NOTE_ID_TAG, uniqueID);
        intent.putExtra(Constant.NOTE_PARENT_ID_TAG, mViewModel.getCurrentNoteModelUniqueID());
        startActivity(intent);
    }

    @Override
    public void enterFolder(String uniqueID) {
        Log.e(TAG, "enterFolder: " + uniqueID);
        mViewModel.loadData(true, uniqueID);
    }

    @Override
    public void onGoUp() {
        Log.e(TAG, "onGoUp: ");
        if (!mViewModel.goUp()) {
            onBackPressed();
        }
    }

    @Override
    public void onPendingItem(String uniqueID) {
        Log.e(TAG, "onPendingItem:" + uniqueID);
        mPendingOperationIDList.clear();
        mPendingOperationIDList.add(uniqueID);
        actionBar.openOptionsMenu();
    }
}
