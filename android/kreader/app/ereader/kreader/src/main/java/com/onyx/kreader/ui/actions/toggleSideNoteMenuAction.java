package com.onyx.kreader.ui.actions;

import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.ui.data.MenuItem;
import com.onyx.android.sdk.ui.data.MenuManager;
import com.onyx.kreader.BR;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;


public class toggleSideNoteMenuAction extends BaseAction {
    public static final String TAG = toggleSideNoteMenuAction.class.getSimpleName();
    private ReaderActivity readerActivity;
    private static List<Integer> menuIdList = new ArrayList<>();
    private MenuManager menuManager;

    public toggleSideNoteMenuAction(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerActivity = (ReaderActivity) readerDataHolder.getContext();
        if (menuManager.getMainMenu() != null && menuManager.getMainMenu().isShowing()) {
            menuManager.removeMainMenu(readerActivity.getExtraView());
        } else {
            initMenu(readerDataHolder);
        }
    }

    private List<Integer> getIDList() {
        if (menuIdList == null || menuIdList.size() == 0) {
            menuIdList = new ArrayList<>();
            menuIdList.add(ReaderMenuAction.ZOOM_IN.ordinal());
            menuIdList.add(ReaderMenuAction.ZOOM_OUT.ordinal());
            menuIdList.add(ReaderMenuAction.ZOOM_BY_CROP_PAGE.ordinal());
            menuIdList.add(ReaderMenuAction.GOTO_PAGE.ordinal());
        }
        return menuIdList;
    }

    private void initMenu(ReaderDataHolder readerDataHolder) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (readerDataHolder.getDisplayWidth() / 2) - 1, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        menuManager.addMainMenu(readerActivity.getExtraView(),
                readerDataHolder.getEventBus(),
                R.layout.side_note_menu,
                BR.item,
                params,
                MenuItem.createVisibleMenus(getIDList()));
    }

}
