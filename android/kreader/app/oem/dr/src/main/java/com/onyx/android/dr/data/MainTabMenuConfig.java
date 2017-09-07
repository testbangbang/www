package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.event.ApplicationEvent;
import com.onyx.android.dr.event.ArticlePushMenuEvent;
import com.onyx.android.dr.event.BookshelfMenuEvent;
import com.onyx.android.dr.event.DictMenuEvent;
import com.onyx.android.dr.event.ListenAndSayMenuEvent;
import com.onyx.android.dr.event.NotesMenuEvent;
import com.onyx.android.dr.event.SettingsMenuEvent;
import com.onyx.android.dr.util.DRPreferenceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class MainTabMenuConfig {
    private static List<MenuBean> highSchoolMenuData = new ArrayList<>();
    private static List<MenuBean> universityMenuData = new ArrayList<>();
    private static List<MenuBean> teacherMenuData = new ArrayList<>();

    public static void loadMenuInfo(Context context) {
        highSchoolMenuData.clear();
        universityMenuData.clear();
        teacherMenuData.clear();

        MenuBean menuData = new MenuBean(DeviceConfig.MainMenuInfo.MENU_BOOKSHELF, getBookshelfName(context), R.drawable.ic_bookshelf, new BookshelfMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuBean(DeviceConfig.MainMenuInfo.MENU_DICT, context.getResources().getString(R.string.menu_dict), R.drawable.ic_dict, new DictMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuBean(DeviceConfig.MainMenuInfo.MENU_NOTES, context.getResources().getString(R.string.menu_notes), R.drawable.ic_note, new NotesMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuBean(DeviceConfig.MainMenuInfo.MENU_LISTEN_AND_SAY, context.getResources().getString(R.string.menu_listen_and_say), R.drawable.ic_listen, new ListenAndSayMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuBean(DeviceConfig.MainMenuInfo.MENU_APPLICATION, context.getResources().getString(R.string.menu_application), R.drawable.ic_application, new ApplicationEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuBean(DeviceConfig.MainMenuInfo.MENU_SETTINGS, context.getResources().getString(R.string.menu_settings), R.drawable.ic_settings, new SettingsMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuBean(DeviceConfig.MainMenuInfo.MENU_ARTICLE_PUSH, context.getResources().getString(R.string.menu_article_push), R.drawable.ic_push, new ArticlePushMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);
    }

    private static String getBookshelfName(Context context) {
        String bookshelfType = DRPreferenceManager.getBookshelfType(context, context.getResources().getString(R.string.language_bookshelf));
        String bookshelf = context.getResources().getString(R.string.language_bookshelf);
        switch (bookshelfType) {
            case Constants.LANGUAGE_BOOKSHELF:
                bookshelf = context.getResources().getString(R.string.language_bookshelf);
                break;
            case Constants.GRADED_BOOKSHELF:
                bookshelf = context.getResources().getString(R.string.grade_bookshelf);
                break;
        }
        return bookshelf;
    }

    public static List<MenuBean> getMenuData(String userType) {
        List<MenuBean> menuData = highSchoolMenuData;
        switch (userType) {
            case Constants.ACCOUNT_TYPE_HIGH_SCHOOL:
                menuData = highSchoolMenuData;
                break;
            case Constants.ACCOUNT_TYPE_UNIVERSITY:
                menuData = universityMenuData;
                break;
            case Constants.ACCOUNT_TYPE_TEACHER:
                menuData = teacherMenuData;
                break;
        }
        getJsonConfig(userType, menuData);
        return menuData;
    }

    private static void getJsonConfig(String userType, List<MenuBean> menuData) {
        Iterator<MenuBean> it = menuData.iterator();
        while (it.hasNext()) {
            MenuBean next = it.next();
            if (!DeviceConfig.sharedInstance(DRApplication.getInstance()).getMainMenuItem(userType, next.getTabKey())) {
                it.remove();
            }
        }
    }
}
