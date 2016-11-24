package com.onyx.android.sdk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joy on 2016/4/19.
 */
public abstract class ReaderMenuItem {
    public enum ItemType { Group, Item }

    private ItemType itemType;
    private ReaderMenuAction action;
    private ReaderMenuItem parent;
    private int itemId;
    private List<ReaderMenuItem> children = new ArrayList<>();

    public ReaderMenuItem(ItemType itemType, ReaderMenuAction action, ReaderMenuItem parent) {
        this.itemType = itemType;
        this.action = action;
        this.parent = parent;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public ReaderMenuAction getAction() {
        return action;
    }

    public ReaderMenuItem getParent() {
        return parent;
    }

    public List<? extends ReaderMenuItem> getChildren() {
        return children;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
