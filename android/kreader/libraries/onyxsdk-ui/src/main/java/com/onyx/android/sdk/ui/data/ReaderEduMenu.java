package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.view.View;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.dialog.DialogReaderEduMenu;
import com.onyx.android.sdk.ui.dialog.DialogReaderMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2016/11/21.
 */

public class ReaderEduMenu extends ReaderMenu {

    private Context context;
    private DialogReaderEduMenu dialog;
    private ReaderMenuState state;
    private List<ReaderLayerMenuItem> menuItems = new ArrayList<>();
    private ReaderLayerMenuItem currentParentMenuItem;
    private View mainMenuContainerView;
    private View subMenuContainerView;

    private ReaderMenuCallback readerMenuCallback = new ReaderMenuCallback() {
        @Override
        public void onMenuItemClicked(ReaderMenuItem menuItem) {
            handleMenuItemClicked(menuItem);
        }

        @Override
        public void onMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue) {
        }

        @Override
        public void onHideMenu() {
            handleHideMenu();
        }
    };

    public ReaderEduMenu(Context context) {
        this.context = context;
    }

    private void handleHideMenu() {
        hide();
    }

    @Override
    public boolean isShown() {
        return false;
    }

    @Override
    public void show(ReaderMenuState state) {
        this.state = state;
        updateMenuContent();
        getDialog().show(state);
    }

    @Override
    public void hide() {
        getDialog().dismiss();
    }

    @Override
    public void updateReaderMenuState(ReaderMenuState state) {

    }

    @Override
    public void fillItems(List<? extends ReaderMenuItem> items) {
        menuItems = (List<ReaderLayerMenuItem>)items;
    }

    private View createMainMenuContainerView(List<ReaderLayerMenuItem> items, ReaderMenuState state) {
        return ReaderLayerMenuViewFactory.createMainMenuContainerView(context, items, state, readerMenuCallback);
    }

    private void handleMenuItemClicked(ReaderMenuItem item) {
        notifyMenuItemClicked(item);
    }

    private DialogReaderEduMenu getDialog() {
        if (dialog == null) {
            dialog = new DialogReaderEduMenu(context, readerMenuCallback);
        }
        return dialog;
    }

    private void updateMenuContent() {
        mainMenuContainerView = createMainMenuContainerView(menuItems, state);
        getDialog().updateMenuView(mainMenuContainerView);
    }

    private boolean isColorMenus(ReaderMenuItem item) {
        return item.getAction() == ReaderMenuAction.DIRECTORY_TOC ||
                item.getAction() == ReaderMenuAction.FRONT_LIGHT;
    }
}
