package com.onyx.jdread.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.jdread.common.ViewConfig;
import com.onyx.jdread.event.ChangeChildViewEvent;
import com.onyx.jdread.event.PopCurrentChildViewEvent;

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

    public FunctionBarItem(ViewConfig.FunctionModule functionModule,String firstFragmentName, String itemName, int drawableRes) {
        this.functionModule.set(functionModule);
        this.itemName.set(itemName);
        drawableTop.set(drawableRes);
        stackList = new StackList();
        ViewConfig.initStackByName(stackList, firstFragmentName);
    }

    public void tabClicked() {
        if (functionModule.get().equals(ViewConfig.FunctionModule.BACK)) {
            EventBus.getDefault().post(new PopCurrentChildViewEvent());
            return;
        }

        ChangeChildViewEvent event = new ChangeChildViewEvent();
        event.childViewName = stackList.peek();
        EventBus.getDefault().post(event);
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
}
