package com.onyx.edu.note.scribble;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.edu.note.HandlerManager;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.R;
import com.onyx.edu.note.data.ScribbleAction;
import com.onyx.edu.note.data.ScribbleFunctionBarMenuID;
import com.onyx.edu.note.data.ScribbleFunctionMenuIDType;
import com.onyx.edu.note.databinding.ActivityScribbleBinding;
import com.onyx.edu.note.databinding.ScribbleFunctionItemBinding;
import com.onyx.edu.note.receiver.DeviceReceiver;
import com.onyx.edu.note.scribble.view.ScribbleSubMenu;
import com.onyx.edu.note.ui.PageAdapter;
import com.onyx.edu.note.util.Constant;

import java.lang.ref.WeakReference;
import java.util.List;

public class ScribbleActivity extends OnyxAppCompatActivity implements ScribbleNavigator, ScribbleItemNavigator {
    private static final String TAG = ScribbleActivity.class.getSimpleName();
    ActivityScribbleBinding mBinding;
    ScribbleViewModel mViewModel;
    ScribbleFunctionAdapter mFunctionBarAdapter, mToolBarAdapter;
    protected SurfaceHolder.Callback surfaceCallback;
    DeviceReceiver deviceReceiver = new DeviceReceiver();
    NoteManager mNoteManager;
    HandlerManager mHandlerManager;
    ScribbleSubMenu mSubMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scribble);


        //TODO:temp disable edit text.
        mBinding.spanTextView.setVisibility(View.GONE);


        initSupportActionBarWithCustomBackFunction();
        mNoteManager = NoteManager.sharedInstance(this);
        mViewModel = new ScribbleViewModel(this);
        mViewModel.setNavigator(this);
        // Link View and ViewModel
        mBinding.setViewModel(mViewModel);
        initRecyclerView();
        buildSubMenu();
        mHandlerManager = new HandlerManager(this, mViewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        deviceReceiver.registerReceiver(this);
    }

    @Override
    protected void onResume() {
        addSurfaceViewCallback();
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNoteManager.sync(false, false);
        DeviceUtils.setFullScreenOnResume(this, false);
        removeSurfaceViewCallback();
    }

    @Override
    protected void onStop() {
        deviceReceiver.unregisterReceiver(this);
        mNoteManager.quit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
    }

    private void buildSubMenu() {
        mSubMenu = new ScribbleSubMenu(this, mNoteManager.getShapeDataInfo(),
                mBinding.mainLayout, new ScribbleSubMenu.Callback() {
            @Override
            public void onLayoutStateChanged() {

            }

            @Override
            public void onCancel() {
                mNoteManager.sync(true, true);
            }
        }, R.id.divider);
    }

    private void handleIntent(Intent intent) {
        @ScribbleAction.ScribbleActionDef int scribbleAction =
                intent.getIntExtra(Constant.SCRIBBLE_ACTION_TAG, ScribbleAction.INVALID);
        if (!ScribbleAction.isValidAction(scribbleAction)) {
            //TODO:direct call finish here.because we don't want incorrect illegal call.
            finish();
            return;
        }
        String uniqueID = intent.getStringExtra(Constant.NOTE_ID_TAG);
        String parentID = intent.getStringExtra(Constant.NOTE_PARENT_ID_TAG);
        BaseCallback callback = new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
//                InputMethodUtils.hideInputKeyboard(ScribbleActivity.this);
                mNoteManager.sync(true, true);
            }
        };
        mViewModel.start(uniqueID, parentID, scribbleAction, callback);
    }

    private void addSurfaceViewCallback() {
        mBinding.noteView.getHolder().addCallback(getSurfaceCallback());
    }

    private void removeSurfaceViewCallback() {
        if (surfaceCallback != null) {
            mBinding.noteView.getHolder().removeCallback(surfaceCallback);
        }
    }

    protected SurfaceHolder.Callback getSurfaceCallback() {
        if (surfaceCallback == null) {
            surfaceCallback = new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    mNoteManager.clearSurfaceView(mBinding.noteView);
                    mNoteManager.setView(ScribbleActivity.this, mBinding.noteView);
                    handleIntent(getIntent());
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    surfaceHolder.removeCallback(surfaceCallback);
                    surfaceCallback = null;
                }
            };
        }
        return surfaceCallback;
    }

    @Override
    public void onBackPressed() {
        mNoteManager.syncWithCallback(true, false, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                mHandlerManager.saveDocument(true, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (!request.isAbort() && e == null) {
                            ScribbleActivity.super.onBackPressed();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void goToSetting() {

    }

    @Override
    public void switchScribbleMode() {

    }

    private void initRecyclerView() {
        mBinding.functionRecyclerView.setLayoutManager(new DisableScrollGridManager(this));
        mBinding.toolBarRecyclerView.setLayoutManager(new DisableScrollGridManager(this));
        mBinding.toolBarRecyclerView.setHasFixedSize(true);
        mBinding.functionRecyclerView.setHasFixedSize(true);
        buildBarIconAdapter();
        mBinding.functionRecyclerView.setAdapter(mFunctionBarAdapter);
        mBinding.toolBarRecyclerView.setAdapter(mToolBarAdapter);
    }

    private void buildBarIconAdapter() {
        mFunctionBarAdapter = new ScribbleFunctionAdapter(this, ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU);
        mToolBarAdapter = new ScribbleFunctionAdapter(this, ScribbleFunctionMenuIDType.TOOL_BAR_MENU);
    }

    @Override
    public void onFunctionBarMenuFunctionItem(final int mainMenuID) {
        Log.e(TAG, "onFunctionBarMenuFunctionItem: " + mainMenuID);
        switch (mainMenuID) {
            case ScribbleFunctionBarMenuID.ADD_PAGE:
                mHandlerManager.addPage();
                break;
            case ScribbleFunctionBarMenuID.DELETE_PAGE:
                mHandlerManager.deletePage();
                break;
            case ScribbleFunctionBarMenuID.NEXT_PAGE:
                mHandlerManager.nextPage();
                break;
            case ScribbleFunctionBarMenuID.PREV_PAGE:
                mHandlerManager.prevPage();
                break;
            default:
                mNoteManager.syncWithCallback(true, false, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        showSubMenu(mainMenuID);
                    }
                });
                break;
        }
    }

    @Override
    public void onSubMenuFunctionItem(int subMenuID) {
        Log.e(TAG, "onSubMenuFunctionItem: " + subMenuID);
        mHandlerManager.handleSubMenuFunction(subMenuID);
        mSubMenu.dismiss();
    }

    @Override
    public void onToolBarMenuFunctionItem(int toolBarMenuID) {
        Log.e(TAG, "onToolBarMenuFunctionItem: " + toolBarMenuID);
        mHandlerManager.handleToolBarMenuFunction(toolBarMenuID);
    }

    private void showSubMenu(@ScribbleFunctionBarMenuID.ScribbleFunctionBarMenuDef int mainMenuID) {
        mSubMenu.show(mainMenuID, false);
    }

    public static class ScribbleFunctionAdapter extends PageAdapter<ScribbleFunctionItemViewHolder, Integer, ScribbleFunctionItemViewModel> {
        private ScribbleActivity mItemNavigator;
        private LayoutInflater mLayoutInflater;
        private final @ScribbleFunctionMenuIDType.ScribbleMenuIDTypeDef
        int mMenuType;
        /*
        * TODO:Because PageRecyclerView need it's own notifyDataSetChanged() (not the adapter one)to update page status.
        * so we had to obtain a weakReference (avoid leak)to update page info text when first load.
        * Maybe OnPagingListener should always trigger when data load into view,which we didn't
        * need to update some page info text manually for first time loading.
        */
        private WeakReference<ScribbleActivity> activityWeakReference;

        ScribbleFunctionAdapter(ScribbleActivity itemNavigator, int menuType) {
            mItemNavigator = itemNavigator;
            mLayoutInflater = itemNavigator.getLayoutInflater();
            activityWeakReference = new WeakReference<>(itemNavigator);
            mMenuType = menuType;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return mMenuType == ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU ? 4 : 6;
        }

        @Override
        public ScribbleFunctionItemViewHolder onPageCreateViewHolder(ViewGroup parent, int viewType) {
            return new ScribbleFunctionItemViewHolder(ScribbleFunctionItemBinding.inflate(mLayoutInflater, parent, false));
        }

        @Override
        public void onPageBindViewHolder(ScribbleFunctionItemViewHolder holder, int position) {
            holder.bindTo(getItemVMList().get(position));
        }

        @Override
        public void setRawData(List<Integer> rawData, Context context) {
            super.setRawData(rawData, context);
            for (Integer mainMenuID : rawData) {
                ScribbleFunctionItemViewModel viewModel = new ScribbleFunctionItemViewModel(mainMenuID, mMenuType);
                viewModel.setNavigator(mItemNavigator);
                getItemVMList().add(viewModel);
            }
            if (activityWeakReference.get() != null) {
                switch (mMenuType) {
                    case ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU:
                        activityWeakReference.get().mBinding.functionRecyclerView.notifyDataSetChanged();
                        break;
                    case ScribbleFunctionMenuIDType.TOOL_BAR_MENU:
                        activityWeakReference.get().mBinding.toolBarRecyclerView.notifyDataSetChanged();
                        break;
                }
            }
        }
    }
}
