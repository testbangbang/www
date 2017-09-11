package com.onyx.einfo.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.einfo.events.ViewTypeEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/9.
 */
public class StorageViewModel extends BaseObservable {
    private static final String TAG = "StorageViewModel";

    public final ObservableList<StorageItemViewModel> items = new ObservableArrayList<>();
    public final ObservableInt folderCount = new ObservableInt();
    public final ObservableInt fileCount = new ObservableInt();
    public final ObservableInt currentPage = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    public final ObservableField<String> currentFolderTitle = new ObservableField<>();
    public final ObservableField<ViewType> viewType = new ObservableField<>();

    private final EventBus eventBus;

    private Map<StorageItemViewModel, Boolean> mapSelected = new HashMap<>();
    private boolean multiSelectionMode = false;

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
        items.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<StorageItemViewModel>>() {
            @Override
            public void onChanged(ObservableList<StorageItemViewModel> sender) {
            }

            @Override
            public void onItemRangeChanged(ObservableList<StorageItemViewModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeInserted(ObservableList<StorageItemViewModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeMoved(ObservableList<StorageItemViewModel> sender, int fromPosition, int toPosition, int itemCount) {
                updateTypeCount(sender);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<StorageItemViewModel> sender, int positionStart, int itemCount) {
                updateTypeCount(sender);
            }
        });
    }

    private void updateTypeCount(ObservableList<StorageItemViewModel> sender) {
        int folderTotal = 0, fileTotal = 0;
        for (StorageItemViewModel model : sender) {
            switch (model.getFileModel().getType()) {
                case FileModel.TYPE_DIRECTORY:
                    folderTotal++;
                    break;
                case FileModel.TYPE_FILE:
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
        getEventBus().post(ViewTypeEvent.create(type));
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

    private boolean isStorageRoot(File targetDirectory) {
        return EnvironmentUtil.getStorageRootDirectory().getAbsolutePath().contains(targetDirectory.getAbsolutePath());
    }

    public void toggleItemModelSelection(StorageItemViewModel itemModel) {
        boolean isSelected = !itemModel.isSelected.get();
        itemModel.setSelected(isSelected);
        getItemSelectedMap().put(itemModel, isSelected);
        itemModel.notifyChange();
    }

    public boolean isInMultiSelectionMode() {
        return multiSelectionMode;
    }

    public void setMultiSelection(boolean beIn) {
        multiSelectionMode = beIn;
        if (!beIn) {
            mapSelected.clear();
        }
    }

    public Map<StorageItemViewModel, Boolean> getItemSelectedMap() {
        return mapSelected;
    }

    public void clearItemSelectedMap() {
        getItemSelectedMap().clear();
    }

    public boolean isItemSelected(StorageItemViewModel key) {
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
}
