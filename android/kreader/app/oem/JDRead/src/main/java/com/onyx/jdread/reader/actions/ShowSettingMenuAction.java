package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ShowReaderSettingMenuEvent;
import com.onyx.jdread.reader.menu.actions.ImageReflowAction;
import com.onyx.jdread.reader.menu.actions.ResetNavigationAction;
import com.onyx.jdread.reader.menu.actions.SwitchNavigationToArticleAction;
import com.onyx.jdread.reader.menu.actions.SwitchNavigationToComicModeAction;
import com.onyx.jdread.reader.menu.dialog.ReaderSettingMenuDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class ShowSettingMenuAction extends BaseReaderAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        EventBus.getDefault().post(new ShowReaderSettingMenuEvent());
    }

    public static Rect getRegionOne() {
        Rect rect = new Rect();
        rect.left = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_left);
        rect.top = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_top);
        rect.right = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_right);
        rect.bottom = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_bottom);
        return rect;
    }

    public static Rect getRegionTwo() {
        Rect rect = new Rect();
        rect.left = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_left);
        rect.top = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_top);
        rect.right = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_right);
        rect.bottom = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_bottom);
        return rect;
    }
}
