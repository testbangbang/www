package com.onyx.android.sdk.ui.utils;

import com.onyx.android.sdk.ui.data.MenuNode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/8/25 14:30.
 */

public class MenuManager {
    private HashMap<String, List<MenuNode>> tagToMenuNodeHashMap = new HashMap<>();

    public void updateMenuNodeList(String tag, List<MenuNode> menuNodeList) {
        tagToMenuNodeHashMap.put(tag, menuNodeList);
    }

    public void updateMenuNode(String tag, MenuNode menuNode) {
        List<MenuNode> menuNodesList = tagToMenuNodeHashMap.get(tag);
        for (MenuNode node : menuNodesList) {
            if (node.getId().equalsIgnoreCase(menuNode.getId())) {
                node.cloneData(menuNode);
                break;
            }
        }
    }

    public List<MenuNode> getMenuNodeListByTag(String tag) {
        return tagToMenuNodeHashMap.get(tag);
    }
}
