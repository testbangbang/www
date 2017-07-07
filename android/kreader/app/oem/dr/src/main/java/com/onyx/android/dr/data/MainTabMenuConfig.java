package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.event.ApplicationEvent;
import com.onyx.android.dr.event.ArticlePushMenuEvent;
import com.onyx.android.dr.event.DictMenuEvent;
import com.onyx.android.dr.event.GradedBooksEvent;
import com.onyx.android.dr.event.ListenAndSayMenuEvent;
import com.onyx.android.dr.event.MyBooksMenuEvent;
import com.onyx.android.dr.event.NotesMenuEvent;
import com.onyx.android.dr.event.ProfessionalMaterialsMenuEvent;
import com.onyx.android.dr.event.RealTimeBooksMenuEvent;
import com.onyx.android.dr.event.SchoolBasedMaterialsMenuEvent;
import com.onyx.android.dr.event.SettingsMenuEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hehai on 17-6-28.
 */

public class MainTabMenuConfig {
    private static List<MenuData> highSchoolMenuData = new ArrayList<>();
    private static List<MenuData> universityMenuData = new ArrayList<>();
    private static List<MenuData> teacherMenuData = new ArrayList<>();

    public static void loadMenuInfo(Context context) {
        MenuData menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_GRADED_BOOKS, context.getResources().getString(R.string.menu_graded_books), R.drawable.ic_books, new GradedBooksEvent());
        highSchoolMenuData.add(menuData);
        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_MY_BOOKS, context.getResources().getString(R.string.menu_my_books), R.drawable.ic_books, new MyBooksMenuEvent());
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_REAL_TIME_ARTICLES, context.getResources().getString(R.string.menu_real_time_articles), R.drawable.ic_real_time_books, new RealTimeBooksMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_SCHOOL_BASED_MATERIALS, context.getResources().getString(R.string.menu_school_based_materials), R.drawable.ic_professional_materials, new SchoolBasedMaterialsMenuEvent());
        highSchoolMenuData.add(menuData);
        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_PROFESSIONAL_MATERIALS, context.getResources().getString(R.string.menu_professional_materials), R.drawable.ic_professional_materials, new ProfessionalMaterialsMenuEvent());
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_DICT, context.getResources().getString(R.string.menu_dict), R.drawable.ic_dict, new DictMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_NOTES, context.getResources().getString(R.string.menu_notes), R.drawable.ic_note, new NotesMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_LISTEN_AND_SAY, context.getResources().getString(R.string.menu_listen_and_say), R.drawable.ic_listen, new ListenAndSayMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_APPLICATION, context.getResources().getString(R.string.menu_application), R.drawable.ic_application, new ApplicationEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_SETTINGS, context.getResources().getString(R.string.menu_settings), R.drawable.ic_settings, new SettingsMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);

        menuData = new MenuData(DeviceConfig.MainMenuInfo.MENU_ARTICLE_PUSH, context.getResources().getString(R.string.menu_article_push), R.drawable.ic_add, new ArticlePushMenuEvent());
        highSchoolMenuData.add(menuData);
        universityMenuData.add(menuData);
        teacherMenuData.add(menuData);
    }

    public static List<MenuData> getMenuData(String userType) {
        List<MenuData> menuData = highSchoolMenuData;
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

    private static void getJsonConfig(String userType, List<MenuData> menuData) {
        Iterator<MenuData> it = menuData.iterator();
        while (it.hasNext()) {
            MenuData next = it.next();
            if (!DeviceConfig.sharedInstance(DRApplication.getInstance()).getMainMenuItem(userType, next.getTabKey())) {
                it.remove();
            }
        }
    }
}
