package com.onyx.kreader.ui.menu;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import com.onyx.kreader.R;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joy on 2016/4/19.
 */
public class ReaderSideMenu extends ReaderMenu {

    private static final Object TAG_BACK_ITEM = new Object();
    private static final Object TAG_CLOSE_ITEM = new Object();

    private LinearLayout readerMenuLayout;
    private ViewAnimator readerMenuViewAnimator;
    private List<SlideMenuItem> readerMenuItems = new ArrayList<>();

    private List<ReaderSideMenuItem> readerMenuTopItems = new ArrayList<>();
    private ReaderSideMenuItem currentReaderMenuItemParent = null;

    public ReaderSideMenu(Activity activity, LinearLayout drawerLayout) {
        readerMenuLayout = drawerLayout;
        readerMenuViewAnimator = new ViewAnimator(activity, readerMenuItems, null, null, new ViewAnimator.ViewAnimatorListener() {
            @Override
            public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
                handleMenuItemClicked(slideMenuItem);
                return null;
            }

            @Override
            public void disableHomeButton() {

            }

            @Override
            public void enableHomeButton() {

            }

            @Override
            public void clearContainer() {
                readerMenuLayout.removeAllViews();
                readerMenuLayout.clearDisappearingChildren();
            }

            @Override
            public void addViewToContainer(View view) {
                readerMenuLayout.addView(view);
            }
        });
    }

    @Override
    public boolean isShown() {
        return readerMenuLayout.getVisibility() == View.VISIBLE;
    }

    @Override
    public void show() {
        if (!isShown()) {
            readerMenuLayout.setVisibility(View.VISIBLE);
            readerMenuViewAnimator.showMenuContent();
        }
    }

    @Override
    public void hide() {
        if (isShown()) {
            readerMenuLayout.setVisibility(View.INVISIBLE);
            readerMenuViewAnimator.hideMenuContent();
        }
    }

    @Override
    public void fillItems(List<? extends ReaderMenuItem> items) {
        readerMenuTopItems.addAll((List<ReaderSideMenuItem>)items);
        switchToTopMenuList();
    }

    private void switchToTopMenuList() {
        switchMenuList(null, readerMenuTopItems);
    }

    private void switchMenuList(ReaderSideMenuItem parent, List<? extends ReaderMenuItem> items) {
        createMenuList(parent, (List<ReaderSideMenuItem>)items);
        readerMenuViewAnimator.showMenuContent();
    }

    private void createMenuList(ReaderSideMenuItem parent, List<ReaderSideMenuItem> items) {
        readerMenuItems.clear();
        if (parent == null) {
            readerMenuItems.add(new SlideMenuItem("Close", R.drawable.menu_close, TAG_CLOSE_ITEM));
        } else {
            readerMenuItems.add(new SlideMenuItem("Back", R.drawable.menu_back, TAG_BACK_ITEM));
        }
        for (ReaderSideMenuItem item : items) {
            readerMenuItems.add(new SlideMenuItem(item.getTitle(), item.getDrawableResourceId(), item));
        }
    }

    private void handleMenuItemClicked(Resourceble slideMenuItem) {
        if (slideMenuItem.getTag() == TAG_CLOSE_ITEM) {
            hide();
        } else if (slideMenuItem.getTag() == TAG_BACK_ITEM) {
            if (currentReaderMenuItemParent == null) {
                return;
            }
            currentReaderMenuItemParent = (ReaderSideMenuItem)currentReaderMenuItemParent.getParent();
            if (currentReaderMenuItemParent == null) {
                switchToTopMenuList();
            } else {
                switchMenuList(currentReaderMenuItemParent, currentReaderMenuItemParent.getChildren());
            }
        } else {
            ReaderSideMenuItem item = (ReaderSideMenuItem)slideMenuItem.getTag();
            if (item.getItemType() == ReaderMenuItem.ItemType.Group) {
                switchMenuList(item, item.getChildren());
                currentReaderMenuItemParent = item;
            }
            notifyMenuItemClicked(item);
        }
    }
}
