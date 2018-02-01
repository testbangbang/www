package com.onyx.jdread.main.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import com.onyx.jdread.main.common.ViewConfig;

import java.util.Observable;

/**
 * Created by huxiaomao on 2017/12/9.
 */

public class FunctionBarModel extends Observable {
    private static final String TAG = FunctionBarModel.class.getSimpleName();
    private ObservableBoolean isShow = new ObservableBoolean(true);
    public ObservableList<FunctionBarItem> itemModels = new ObservableArrayList<>();

    public FunctionBarModel() {

    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public void changeTabSelection(ViewConfig.FunctionModule selectedTab) {
        for (FunctionBarItem itemModel : itemModels) {
            itemModel.setSelected(selectedTab.equals(itemModel.functionModule.get()));
        }
    }

    public FunctionBarItem findFunctionGroup(ViewConfig.FunctionModule functionModule){
        for (FunctionBarItem itemModel : itemModels) {
            if(functionModule.equals(itemModel.functionModule.get())){
                return itemModel;
            }
        }
        return null;
    }

    public FunctionBarItem getSelectedFunctionItem() {
        for (FunctionBarItem itemModel : itemModels) {
            if (itemModel.getSelected()) {
                return itemModel;
            }
        }
        return null;
    }
}
