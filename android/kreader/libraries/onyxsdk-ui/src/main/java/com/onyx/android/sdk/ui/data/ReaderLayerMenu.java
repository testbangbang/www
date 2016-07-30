package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.view.View;

import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.dialog.DialogReaderMenu;

import java.util.List;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenu extends ReaderMenu {
    public static final String TAG = ReaderLayerMenu.class.getSimpleName();

    private Context context;
    private DialogReaderMenu dialog;
    private ReaderLayerMenuState state;
    private List<ReaderLayerMenuItem> menuItems;
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
            handleMenuItemValueChanged(menuItem, oldValue, newValue);
        }

        @Override
        public void onHideMenu() {
            handleHideMenu();
        }
    };

    public ReaderLayerMenu(Context context) {
        this.context = context;
    }

    @Override
    public boolean isShown() {
        return getDialog().isShowing();
    }

    @Override
    public void show(ReaderMenuState state) {
        this.state = (ReaderLayerMenuState)state;
        updateMenuContent();
        getDialog().show(this.state);
    }

    @Override
    public void hide() {
        getDialog().dismiss();
    }

    @Override
    public void fillItems(List<? extends ReaderMenuItem> items) {
        menuItems = (List<ReaderLayerMenuItem>)items;
        assert (items.get(0).getItemType() == ReaderMenuItem.ItemType.Group);
        currentParentMenuItem = (ReaderLayerMenuItem)items.get(0);
    }

    private DialogReaderMenu getDialog() {
        if (dialog == null) {
            dialog = new DialogReaderMenu(context, readerMenuCallback);
        }
        return dialog;
    }

    private View createMainMenuContainerView(List<ReaderLayerMenuItem> items, ReaderLayerMenuState state) {
        return ReaderLayerMenuViewFactory.createMainMenuContainerView(context, items, state, readerMenuCallback);
    }

    private View createSubMenuContainerView(ReaderLayerMenuItem parent, List<ReaderLayerMenuItem> items, ReaderLayerMenuState state) {
        return ReaderLayerMenuViewFactory.createSubMenuContainerView(context, parent, items, state, readerMenuCallback);
    }

    private void handleMenuItemClicked(ReaderMenuItem item) {
        if (item.getItemType() == ReaderMenuItem.ItemType.Group) {
            currentParentMenuItem = (ReaderLayerMenuItem)item;
            updateMenuContent();
        } else {
            notifyMenuItemClicked(item);
        }
    }

    private void handleMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue) {
        notifyMenuItemValueChanged(menuItem, oldValue, newValue);
    }

    private void handleHideMenu() {
        hide();
    }

    private void updateMenuContent() {
        if (mainMenuContainerView == null) {
            mainMenuContainerView = createMainMenuContainerView(menuItems, state);
        }
        subMenuContainerView = createSubMenuContainerView(currentParentMenuItem, (List<ReaderLayerMenuItem>)currentParentMenuItem.getChildren(), state);
        getDialog().getReaderMenuLayout().updateMenuContent(mainMenuContainerView, subMenuContainerView);
    }
}
