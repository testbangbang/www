package com.onyx.jdread.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import java.util.Observable;

/**
 * Created by huxiaomao on 2017/12/9.
 */

public class FunctionBarModel extends Observable {
    private static final String TAG = FunctionBarModel.class.getSimpleName();
    private ObservableBoolean isShow = new ObservableBoolean(true);
    public ObservableList<FunctionBarTabModel> itemModels = new ObservableArrayList<>();

    public FunctionBarModel() {

    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {

    }

    public void changeTabSelection(FunctionBarTabModel selectedTab) {
        for (FunctionBarTabModel itemModel : itemModels) {
            itemModel.setSelected(selectedTab == itemModel);
        }
    }
}
