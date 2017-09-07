package com.onyx.android.sdk.ui.data;

import android.util.SparseArray;
import android.view.ViewGroup;


import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/31.
 */

public class MenuManager {

    private Menu mainMenu;
    private Menu subMenu;
    private Menu toolbarMenu;

    public MenuManager addMainMenu(ViewGroup parent,
                                   EventBus eventBus,
                                   int layoutId,
                                   int menuVariable,
                                   ViewGroup.LayoutParams params,
                                   SparseArray<MenuItem> menuItemMap) {
        mainMenu = Menu.create(parent.getContext(), eventBus, layoutId, menuVariable, menuItemMap);
        mainMenu.show(parent, params);
        return this;
    }

    public Menu getMainMenu() {
        return mainMenu;
    }

    public Menu getSubMenu() {
        return subMenu;
    }

    public Menu getToolbarMenu() {
        return toolbarMenu;
    }

    public MenuManager addSubMenu(ViewGroup parent,
                                  EventBus eventBus,
                                  int layoutId,
                                  int menuVariable,
                                  ViewGroup.LayoutParams params,
                                  SparseArray<MenuItem> menuItemMap) {
        subMenu = Menu.create(parent.getContext(), eventBus, layoutId, menuVariable, menuItemMap);
        subMenu.show(parent, params);
        return this;
    }

    public MenuManager addToolbarMenu(ViewGroup parent,
                                      EventBus eventBus,
                                      int layoutId,
                                      int menuVariable,
                                      ViewGroup.LayoutParams params,
                                      SparseArray<MenuItem> menuItemMap) {
        toolbarMenu = Menu.create(parent.getContext(), eventBus, layoutId, menuVariable, menuItemMap);
        toolbarMenu.show(parent, params);
        return this;
    }

}
