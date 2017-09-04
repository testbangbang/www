package com.onyx.edu.note.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;
import java.util.Set;

/**
 * Created by lxm on 2017/8/31.
 */

public class MenuManager {

    private ViewDataBinding mainMenuBinding;
    private BaseMenuViewModel mainMenuViewModel;

    private ViewDataBinding toolbarBinding;
    private BaseMenuViewModel toolbarViewModel;

    private ViewDataBinding subMenuBinding;
    private BaseMenuViewModel subMenuViewModel;

    public MenuManager addMainMenu(ViewGroup parent,
                                   BaseMenuViewModel viewModel,
                                   int layoutId,
                                   ViewGroup.LayoutParams params) {
        mainMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, null, false);
        this.mainMenuViewModel = viewModel;
        mainMenuBinding.setVariable(BR.viewModel, viewModel);
        parent.addView(getMainMenuView(), params);
        return this;
    }

    public MenuManager addToolbar(ViewGroup parent,
                                  BaseMenuViewModel viewModel,
                                  int layoutId,
                                  ViewGroup.LayoutParams params) {
        toolbarBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, null, false);
        this.toolbarViewModel = viewModel;
        toolbarBinding.setVariable(BR.viewModel, viewModel);
        parent.addView(getToolbarView(), params);
        return this;
    }

    public MenuManager setMainMenuIds(List<Integer> menuIds) {
        if (getMainMenuViewModel() == null) {
            return this;
        }
        getMainMenuViewModel().show(menuIds);
        return this;
    }

    public MenuManager setToolbarMenuIds(List<Integer> menuIds) {
        if (getToolbarViewModel() == null) {
            return this;
        }
        getToolbarViewModel().show(menuIds);
        return this;
    }

    public MenuManager showSubMenuView(ViewGroup parent,
                                       BaseMenuViewModel viewModel,
                                       int layoutId,
                                       ViewGroup.LayoutParams params) {
        subMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, null, false);
        this.subMenuViewModel = viewModel;
        subMenuBinding.setVariable(BR.viewModel, viewModel);
        parent.addView(getSubMenuView(), params);
        return this;
    }

    public MenuManager checkSubMenu(final List<Integer> menuIds) {
        if (getSubMenuViewModel() == null) {
            return this;
        }
        getSubMenuView().post(new Runnable() {
            @Override
            public void run() {
                getSubMenuViewModel().check(menuIds);
            }
        });
        return this;
    }

    public View getMainMenuView() {
        return mainMenuBinding.getRoot();
    }

    public View getToolbarView() {
        return toolbarBinding.getRoot();
    }

    public View getSubMenuView() {
        return subMenuBinding.getRoot();
    }

    public BaseMenuViewModel getMainMenuViewModel() {
        return mainMenuViewModel;
    }

    public BaseMenuViewModel getToolbarViewModel() {
        return toolbarViewModel;
    }

    public BaseMenuViewModel getSubMenuViewModel() {
        return subMenuViewModel;
    }

    public void onDestroy() {
        if (getMainMenuViewModel() != null) {
            getMainMenuViewModel().onDestroy();
        }
        if (getToolbarViewModel() != null) {
            getToolbarViewModel().onDestroy();
        }
    }
}
