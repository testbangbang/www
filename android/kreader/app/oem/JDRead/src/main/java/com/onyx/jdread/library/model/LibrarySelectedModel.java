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

    public LibrarySelectedModel() {
        this.selectedAll = false;
        this.selectedList = new ArrayList<>();
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
}
