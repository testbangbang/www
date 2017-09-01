package com.onyx.edu.note.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.onyx.android.sdk.ui.data.MenuAction;

/**
 * Created by lxm on 2017/8/31.
 */

public class MenuManager<B extends ViewDataBinding, V extends BaseMenuViewModel> {

    private B binding;
    private V viewModel;

    public MenuManager(Context context, V viewModel, int layoutId) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false);
        this.viewModel = viewModel;
        binding.setVariable(BR.viewModel, viewModel);
    }

    public V getViewModel() {
        return viewModel;
    }

    public B getBinding() {
        return binding;
    }

    public View getRootView() {
        return binding.getRoot();
    }

    public View getMenuView(@MenuAction.ActionDef int menuAction) {
        return viewModel.getMenuView(menuAction);
    }

    public @MenuAction.ActionDef
    int getMenuAction(View view) {
        return viewModel.getMenuAction(view);
    }

    public void onDestroy() {
        viewModel.onDestroy();
    }
}
