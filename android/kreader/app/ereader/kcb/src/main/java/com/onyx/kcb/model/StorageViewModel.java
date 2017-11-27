package com.onyx.kcb.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.util.SparseArray;
import android.view.View;

import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.ui.utils.SelectionMode;
import com.onyx.kcb.event.ViewTypeEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/9.
 */
public class StorageViewModel extends BaseObservable {
    private static final String TAG = StorageViewModel.class.getSimpleName();

    public final ObservableList<DataModel> items = new ObservableArrayList<>();
    public final ObservableInt folderCount = new ObservableInt();
    public final ObservableInt fileCount = new ObservableInt();
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    public final ObservableField<String> currentFolderTitle = new ObservableField<>();
    public final ObservableField<ViewType> viewType = new ObservableField<>();
    public final ObservableBoolean showOperationFunc = new ObservableBoolean();
    public final SparseArray<OperationItem> operationItemArray = new SparseArray<>();

    private final EventBus eventBus;

    private Map<DataModel, Boolean> mapSelected = new HashMap<>();
    private int selectionMode = SelectionMode.NORMAL_MODE;

    public StorageViewModel(EventBus bus) {
        eventBus = bus;
        initViewType();
        initDataListChangeListener();
    }

    public StorageViewModel(EventBus bus, ViewType type) {
        this(bus);
        viewType.set(type);
    }

    private void initViewType() {
        viewType.set(ViewType.Thumbnail);
    }

    private void initDataListChangeListener() {
        items.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<DataModel>>() {
            @Override
            public void onChanged(ObservableList<DataModel> sender) {
            }

            @Override
            public void onItemRangeChanged(ObservableList<DataModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeInserted(ObservableList<DataModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeMoved(ObservableList<DataModel> sender, int fromPosition, int toPosition, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<DataModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }
        });
    }

    private void updateTypeCount(ObservableList<DataModel> sender) {
        int folderTotal = 0, fileTotal = 0;
        for (DataModel model : sender) {
            switch (model.getFileModel().getType()) {
                case TYPE_SHORT_CUT:
                case TYPE_DIRECTORY:
                    folderTotal++;
                    break;
                case TYPE_FILE:
                    fileTotal++;
                    break;
            }
        }
        folderCount.set(folderTotal);
        fileCount.set(fileTotal);
    }

    public void setPageStatus(int curPage, int allPage) {
        currentPage.set(curPage);
        totalPage.set(allPage);
    }

    public boolean canGoUp() {
        if (isTopLevelFolder()) {
            return false;
        }
        return true;
    }

    public void onViewChangeClick() {
        ViewType type = viewType.get();
        viewType.set(type == ViewType.Details ? ViewType.Thumbnail : ViewType.Details);
        getEventBus().post(ViewTypeEvent.create(viewType.get()));
    }

    private EventBus getEventBus() {
        return eventBus;
    }

    public ViewType getCurrentViewType() {
        return viewType.get();
    }

    public File getParentFile() {
        return getCurrentFile().getParentFile();
    }

    public File getCurrentFile() {
        return items.get(0).getFileModel().getFile();
    }

    private boolean isTopLevelFolder() {
        File file = getCurrentFile();
        return file == null || isStorageRoot(file);
    }

    public boolean isStorageRoot(File targetDirectory) {
        return EnvironmentUtil.getStorageRootDirectory().getAbsolutePath().contains(targetDirectory.getAbsolutePath());
    }

    public void toggleItemModelSelection(DataModel itemModel) {
        boolean isSelected = !itemModel.checked.get();
        itemModel.setChecked(isSelected);
        if (!isSelected) {
            getItemSelectedMap().remove(itemModel);
        }
        else {
            getItemSelectedMap().put(itemModel, true);
        }
        itemModel.notifyChange();
    }

    public boolean isInMultiSelectionMode() {
        return selectionMode == SelectionMode.MULTISELECT_MODE;
    }

    public void setSelectionMode(int mode) {
        selectionMode = mode;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    public Map<DataModel, Boolean> getItemSelectedMap() {
        return mapSelected;
    }

    public void clearItemSelectedMap() {
        getItemSelectedMap().clear();
    }

    public void addItemSelected(DataModel itemModel, boolean clearBeforeAdd) {
        if (clearBeforeAdd) {
            clearItemSelectedMap();
        }
        getItemSelectedMap().put(itemModel, true);
    }

    public List<File> getItemSelectedFileList() {
        List<File> fileList = new ArrayList<>();
        for (DataModel model : getItemSelectedMap().keySet()) {
            fileList.add(model.getFileModel().getFile());
        }
        return fileList;
    }

    public List<DataModel> getItemSelectedItemModelList() {
        return Arrays.asList(getItemSelectedMap().keySet().toArray(new DataModel[0]));
    }

    public boolean isItemSelected(DataModel key) {
        Boolean selected = getItemSelectedMap().get(key);
        if (selected == null) {
            return false;
        }
        return selected;
    }

    public void updateCurrentTitleName(String defaultName) {
        String name = getCurrentFile().getAbsolutePath();
        if (isStorageRoot(getCurrentFile())) {
            name = defaultName;
        }
        currentFolderTitle.set(name);
    }

    public void toggleShowOperationFunc() {
        showOperationFunc.set(!showOperationFunc.get());
    }

    public void setShowOperationFunc(boolean show) {
        showOperationFunc.set(show);
    }

    public void setOperationItemArray(List<OperationItem> list) {
        operationItemArray.clear();
        for (int i = 0; i < list.size(); i++) {
            operationItemArray.put(i, list.get(i));
        }
    }

    public SparseArray<OperationItem> getOperationItemArray() {
        return operationItemArray;
    }

    private void hideAllOperationItems() {
        for (int i = 0; i < getOperationItemArray().size(); i++) {
            getOperationItemArray().valueAt(i).setVisibility(View.GONE);
        }
    }

    private void showAllOperationItems() {
        for (int i = 0; i < getOperationItemArray().size(); i++) {
            getOperationItemArray().valueAt(i).setVisibility(View.VISIBLE);
        }
    }

    public void switchOperationPanel(boolean show, int... operations) {
        if (show) {
            hideAllOperationItems();
        }
        else {
            showAllOperationItems();
        }
        for (int operation : operations) {
            getOperationItemArray().valueAt(operation).setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}