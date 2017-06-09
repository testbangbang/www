package com.onyx.edu.note.manager;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.edu.note.R;
import com.onyx.edu.note.ui.ViewModelHolder;
import com.onyx.edu.note.databinding.ActivityManagerBinding;
import com.onyx.edu.note.util.Utils;

/**
 * activity and ui-fragment should only handle ui logic.
 * should not do database logic in these class.
 * just send all work logic to view model class.
 */
public class ManagerActivity extends OnyxAppCompatActivity implements ManagerNavigator, ManagerItemNavigator {
    static final String TAG = ManagerActivity.class.getSimpleName();
    public static final String MANAGER_VIEW_MODEL_TAG = "MANAGER_VIEW_MODEL_TAG";
    ActivityManagerBinding binding;
    private ManagerViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager);
        initSupportActionBarWithCustomBackFunction();
        ManagerFragment noteFragment = findOrCreateViewFragment();

        mViewModel = findOrCreateViewModel();
        mViewModel.setNavigator(this);

        // Link View and ViewModel
        noteFragment.setViewModel(mViewModel);
    }

    @Override
    protected void onDestroy() {
        mViewModel.onActivityDestroyed();
        super.onDestroy();
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
        ManagerFragment tasksFragment =
                (ManagerFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = ManagerFragment.newInstance();
            Utils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
        return tasksFragment;
    }

    @Override
    public void deleteNote() {
        Log.e(TAG, "deleteNote: ");
    }

    @Override
    public void addFolder() {
        Log.e(TAG, "addFolder: ");
    }

    @Override
    public void editNote(String uniqueID) {
        Log.e(TAG, "editNote: " + uniqueID);
    }

    @Override
    public void enterFolder(String uniqueID) {
        Log.e(TAG, "enterFolder: " + uniqueID);
    }

    @Override
    public void addNewNote() {
        Log.e(TAG, "addNewNote: ");
    }
}
