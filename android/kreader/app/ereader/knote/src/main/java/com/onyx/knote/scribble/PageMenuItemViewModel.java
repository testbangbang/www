package com.onyx.knote.scribble;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.view.View;

import com.onyx.knote.data.ScribbleFunctionMenuIDType;
import com.onyx.knote.ui.FunctionMenuClickEvent;
import com.onyx.knote.ui.SubMenuClickEvent;
import com.onyx.knote.ui.ToolbarMenuClickEvent;
import com.onyx.knote.util.ScribbleFunctionItemUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/9/1.
 */

public class PageMenuItemViewModel extends BaseObservable {

    private static final String TAG = PageMenuItemViewModel.class.getSimpleName();

    public final ObservableInt iconRes = new ObservableInt();
    public final ObservableBoolean isChecked = new ObservableBoolean();
    public final ObservableBoolean showIndicator = new ObservableBoolean();

    public int getItemID() {
        return itemID;
    }

    private int itemID;
    private EventBus eventBus;
    private int itemMenuIDType;

    public PageMenuItemViewModel(EventBus eventBus,
                                 int itemID,
                                 int itemMenuIDType) {
        this.eventBus = eventBus;
        this.itemMenuIDType = itemMenuIDType;
        this.itemID = itemID;
        switch (this.itemMenuIDType) {
            case ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU:
                iconRes.set(ScribbleFunctionItemUtils.getFunctionBarItemIDIconRes(itemID));
                isChecked.set(false);
                showIndicator.set(false);
                break;
            case ScribbleFunctionMenuIDType.SUB_MENU:
                iconRes.set(ScribbleFunctionItemUtils.getSubItemIDIconRes(itemID));
                showIndicator.set(true);
                break;
            case ScribbleFunctionMenuIDType.TOOL_BAR_MENU:
                iconRes.set(ScribbleFunctionItemUtils.getToolBarItemIDIconRes(itemID));
                showIndicator.set(false);
                break;
        }
    }

    public void itemClicked(View view) {
        switch (itemMenuIDType) {
            case ScribbleFunctionMenuIDType.FUNCTION_BAR_MENU:
                eventBus.post(new FunctionMenuClickEvent(itemID));
                break;
            case ScribbleFunctionMenuIDType.SUB_MENU:
                eventBus.post(new SubMenuClickEvent(itemID));
                break;
            case ScribbleFunctionMenuIDType.TOOL_BAR_MENU:
                eventBus.post(new ToolbarMenuClickEvent(itemID));
                break;
        }
    }


}
