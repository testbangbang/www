package com.onyx.jdread.main.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.event.ChangeChildViewEvent;
import com.onyx.jdread.main.event.PopCurrentChildViewEvent;
import com.onyx.jdread.main.event.TabLongClickedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-12-11.
 */

public class FunctionBarItem extends BaseObservable {
    private StackList stackList;
    private ObservableBoolean isShow = new ObservableBoolean(true);
    public final ObservableInt drawableTop = new ObservableInt();
    public final ObservableField<ViewConfig.FunctionModule> functionModule = new ObservableField<>();
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableBoolean isSelected = new ObservableBoolean(false);
    public final ObservableBoolean backControl = new ObservableBoolean(true);

    public FunctionBarItem(ViewConfig.FunctionModule functionModule,String firstFragmentName, String itemName, int drawableRes, boolean backControl) {
        this.functionModule.set(functionModule);
        this.itemName.set(itemName);
        drawableTop.set(drawableRes);
        this.backControl.set(backControl);
        stackList = new StackList();
        ViewConfig.initStackByName(stackList, firstFragmentName);
    }

    public void tabClicked() {
        if (functionModule.get().equals(ViewConfig.FunctionModule.BACK)) {
            EventBus.getDefault().post(new PopCurrentChildViewEvent());
            return;
        }

        EventBus.getDefault().post(this);
    }

    public boolean tabLongClicked() {
        EventBus.getDefault().post(new TabLongClickedEvent(this));
        return true;
    }

    private void changeSelectedTab() {
        EventBus.getDefault().post(this);
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public StackList getStackList() {
        return stackList;
    }

    public void setSelected(boolean isSelected){
        this.isSelected.set(isSelected);
    }

    public boolean getSelected() {
        return isSelected.get();
    }

    public void setBackControl(boolean backControl) {
        this.backControl.set(backControl);
    }

    public boolean getBackControl() {
        return backControl.get();
    }

    public ViewConfig.FunctionModule getFunctionModule() {
        return functionModule.get();
    }
}
