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
        mainMenu = Menu.addMenu(parent, eventBus, layoutId, menuVariable, params, menuItemMap);
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
        if (subMenu != null) {
            subMenu.remove(parent);
            subMenu = null;
        }
        subMenu = Menu.addMenu(parent, eventBus, layoutId, menuVariable, params, menuItemMap);
        return this;
    }

    public MenuManager addToolbarMenu(ViewGroup parent,
                                      EventBus eventBus,
                                      int layoutId,
                                      int menuVariable,
                                      ViewGroup.LayoutParams params,
                                      SparseArray<MenuItem> menuItemMap) {
        toolbarMenu = Menu.addMenu(parent, eventBus, layoutId, menuVariable, params, menuItemMap);
        return this;
    }

    public MenuManager updateMainMenuDataSet(int menuVariable, EventBus eventBus, SparseArray<MenuItem> menuItemSparseArray) {
        return updateMenuDataSet(mainMenu, menuVariable, eventBus, menuItemSparseArray);
    }

    public MenuManager updateSubMenuDataSet(int menuVariable, EventBus eventBus, SparseArray<MenuItem> menuItemSparseArray) {
        return updateMenuDataSet(subMenu, menuVariable, eventBus, menuItemSparseArray);
    }

    public MenuManager updateToolbarMenuDataSet(int menuVariable, EventBus eventBus, SparseArray<MenuItem> menuItemSparseArray) {
        return updateMenuDataSet(toolbarMenu, menuVariable, eventBus, menuItemSparseArray);
    }

    private MenuManager updateMenuDataSet(Menu menu, int menuVariable, EventBus eventBus, SparseArray<MenuItem> menuItemSparseArray) {
        if (menu != null) {
            menu.updateItemMap(menuVariable, eventBus, menuItemSparseArray);
        }
        return this;
    }

    public void removeSubMenu(ViewGroup parent) {
        if (subMenu != null) {
            subMenu.remove(parent);
            subMenu = null;
        }
    }

    public void removeMainMenu(ViewGroup parent) {
        if (mainMenu != null) {
            mainMenu.remove(parent);
            mainMenu = null;
        }
    }

    public void removeToolbarMenu(ViewGroup parent) {
        if (toolbarMenu != null) {
            toolbarMenu.remove(parent);
            toolbarMenu = null;
        }
    }

    public boolean isMainMenuShown() {
        return getMainMenu() != null && getMainMenu().isShowing();
    }
}
