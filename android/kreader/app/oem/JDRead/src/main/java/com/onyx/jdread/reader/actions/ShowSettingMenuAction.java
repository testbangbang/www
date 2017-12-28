package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.common.ToastMessage;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class ShowSettingMenuAction extends BaseAction {
    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        ToastMessage.showMessage(JDReadApplication.getInstance().getApplicationContext(),"showMenu");
    }

    public static Rect getRegionOne(){
        Rect rect = new Rect();
        rect.left = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_left);
        rect.top = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_top);
        rect.right = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_right);
        rect.bottom = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_one_region_bottom);
        return rect;
    }

    public static Rect getRegionTwo(){
        Rect rect = new Rect();
        rect.left = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_left);
        rect.top = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_top);
        rect.right = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_right);
        rect.bottom = JDReadApplication.getInstance().getResources().getInteger(R.integer.show_menu_touch_two_region_bottom);
        return rect;
    }
}
