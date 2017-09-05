package com.onyx.edu.note.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
                                   ViewGroup.LayoutParams params,
                                   Map<Integer, MenuItem> menuItemMap) {
        mainMenu = Menu.create(parent.getContext(), eventBus, layoutId, menuItemMap);
        mainMenu.show(parent, params);
        return this;
    }

    public Menu getMainMenu() {
        return mainMenu;
    }

    public Menu getSubMenu() {
        return subMenu;
    }

    public MenuManager addSubMenu(ViewGroup parent,
                                  EventBus eventBus,
                                  int layoutId,
                                  ViewGroup.LayoutParams params,
                                  Map<Integer, MenuItem> menuItemMap) {
        subMenu = Menu.create(parent.getContext(), eventBus, layoutId, menuItemMap);
        subMenu.show(parent, params);
        return this;
    }

    public MenuManager addToolbarMenu(ViewGroup parent,
                                      EventBus eventBus,
                                      int layoutId,
                                      ViewGroup.LayoutParams params,
                                      Map<Integer, MenuItem> menuItemMap) {
        toolbarMenu = Menu.create(parent.getContext(), eventBus, layoutId, menuItemMap);
        toolbarMenu.show(parent, params);
        return this;
    }

}
