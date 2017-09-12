package com.onyx.einfo.model;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.einfo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/9/2.
 */
public class MenuCustomItem {

    private static Map<String, Integer> menuMap = new HashMap<>();

    static {
        menuMap.put(MenuAction.ViewType.name(), R.string.menu_view_switch);
        menuMap.put(MenuAction.SortBy.name(), R.string.menu_sort);
        menuMap.put(MenuAction.Group.name(), R.string.menu_group_select);
    }

    public MenuAction action;
    public int titleRes;

    public static MenuCustomItem create(MenuAction action, int title) {
        MenuCustomItem item = new MenuCustomItem();
        item.action = action;
        item.titleRes = title;
        return item;
    }

    public static List<MenuCustomItem> getMenuItemList(String... actionNames) {
        List<MenuCustomItem> list = new ArrayList<>();
        if (actionNames == null || actionNames.length <= 0) {
            return list;
        }
        for (String actionName : actionNames) {
            MenuAction action = MenuAction.valueOf(actionName);
            list.add(create(action, menuMap.get(actionName)));
        }
        return list;
    }

    public static List<MenuCustomItem> getMenuItemList(List<String> actionNameList) {
        if (CollectionUtils.isNullOrEmpty(actionNameList)) {
            return new ArrayList<>();
        }
        return getMenuItemList(actionNameList.toArray(new String[0]));
    }
}
