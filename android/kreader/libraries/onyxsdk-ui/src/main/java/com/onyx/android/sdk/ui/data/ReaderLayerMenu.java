package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.view.View;

import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.ReaderMenu;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;
import com.onyx.android.sdk.data.ReaderMenuState;
import com.onyx.android.sdk.ui.R;
import com.onyx.android.sdk.ui.dialog.DialogReaderMenu;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.RunnableWithArgument;

import java.util.List;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenu extends ReaderMenu {
    public static final String TAG = ReaderLayerMenu.class.getSimpleName();

    private Context context;
    private DialogReaderMenu dialog;
    private ReaderMenuState state;
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
        this.state = state;
        updateMenuItemsWithState(state);
        updateMenuContent();
        getDialog().show(state);
    }

    @Override
    public void fullScreenStateChange(boolean fullscreen) {
        DeviceUtils.adjustDialogFullScreenStatusForAPIAbove19(getDialog(), fullscreen);
    }

    @Override
    public void hide() {
        getDialog().dismiss();
    }

    @Override
    public void updateReaderMenuState(ReaderMenuState state) {
        this.state = state;
        updateMenuItemsWithState(state);
        getDialog().updateReaderState(state);
    }

    @Override
    public void fillItems(List<? extends ReaderMenuItem> items) {
        menuItems = (List<ReaderLayerMenuItem>)items;
        assert (items.get(0).getItemType() == ReaderMenuItem.ItemType.Group);
        currentParentMenuItem = (ReaderLayerMenuItem)items.get(0);
    }

    private DialogReaderMenu getDialog() {
        if (dialog == null) {
            dialog = new DialogReaderMenu(context, readerMenuCallback, isFullscreen());
        }
        return dialog;
    }

    private View createMainMenuContainerView(List<ReaderLayerMenuItem> items, ReaderMenuState state) {
        return ReaderLayerMenuViewFactory.createMainMenuContainerView(context, items, state, readerMenuCallback, true);
    }

    private View createSubMenuContainerView(ReaderLayerMenuItem parent, List<ReaderLayerMenuItem> items, int menuLayoutId, ReaderMenuState state) {
        return ReaderLayerMenuViewFactory.createSubMenuContainerView(context, parent, menuLayoutId,items, state, true, readerMenuCallback);
    }

    private void handleMenuItemClicked(ReaderMenuItem item) {
        if (item.getItemType() == ReaderMenuItem.ItemType.Group
                && item.getChildren().size() > 0) {
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
        mainMenuContainerView = createMainMenuContainerView(menuItems, state);
        subMenuContainerView = createSubMenuContainerView(currentParentMenuItem, currentParentMenuItem.getChildren(), R.layout.reader_layer_menu_button_item, state);
        getDialog().getReaderMenuLayout().updateMenuContent(mainMenuContainerView, subMenuContainerView);
    }

    private void updateMenuItemsWithState(final ReaderMenuState state) {
        traverseMenuItems(menuItems, new RunnableWithArgument<ReaderLayerMenuItem>() {
            @Override
            public void run(ReaderLayerMenuItem value) {
                updateShowNoteMenuItem(value, state);
                updateScribbleMenuItems(value, state);
                updateFontMenuItems(value);
            }
        });
    }

    private void traverseMenuItems(List<ReaderLayerMenuItem> items, RunnableWithArgument<ReaderLayerMenuItem> callback) {
        for (ReaderLayerMenuItem item : items) {
            callback.run(item);
            traverseMenuItems(item.getChildren(), callback);
        }
    }

    private void updateShowNoteMenuItem(final ReaderLayerMenuItem item, final ReaderMenuState state) {
        if (item.getAction() != ReaderMenuAction.SHOW_NOTE) {
            return;
        }
        item.setDrawableResourceId(state.isShowingNotes()
                ? R.drawable.ic_dialog_reader_menu_note_show
                : R.drawable.ic_dialog_reader_menu_note_hide);
        item.setTitleResourceId(state.isShowingNotes()
                ? R.string.reader_layer_menu_hide_scribble
                : R.string.reader_layer_menu_show_scribble);
    }

    private void updateScribbleMenuItems(final ReaderLayerMenuItem item, final ReaderMenuState state) {
        if (item.getAction() != ReaderMenuAction.DIRECTORY_SCRIBBLE &&
                item.getAction() != ReaderMenuAction.SHOW_NOTE) {
            return;
        }
        item.setVisible(state.isFixedPagingMode());
    }

    private void updateFontMenuItems(final ReaderLayerMenuItem item) {
        if (item.getAction() != ReaderMenuAction.FONT_STYLE) {
            return;
        }
        updateMenuContent();
    }

    public List<ReaderLayerMenuItem> getMenuItems() {
        return menuItems;
    }
}
