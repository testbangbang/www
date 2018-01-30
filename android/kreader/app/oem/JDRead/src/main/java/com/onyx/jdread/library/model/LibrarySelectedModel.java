package com.onyx.jdread.library.model;

import com.onyx.android.sdk.data.model.DataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-12-20.
 */

public class LibrarySelectedModel {
    private boolean selectedAll;
    private List<DataModel> selectedList;
    private int count;

    public LibrarySelectedModel(int count) {
        this.selectedAll = false;
        this.selectedList = new ArrayList<>();
        this.count = count;
    }

    public boolean isSelectedAll() {
        return selectedAll;
    }

    public List<DataModel> getSelectedList() {
        return selectedList;
    }

    public void setSelectedAll(boolean selectedAll) {
        this.selectedAll = selectedAll;
    }

    public void setSelectedList(List<DataModel> selectedList) {
        this.selectedList = selectedList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean haveSelected() {
        return (selectedAll && count > selectedList.size()) || (!selectedAll && selectedList.size() != 0);
    }
}
