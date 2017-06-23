package com.onyx.android.sdk.ui.data;

import com.onyx.android.sdk.data.ReaderMenuAction;
import com.onyx.android.sdk.data.ReaderMenuItem;

import java.util.List;

/**
 * Created by joy on 6/28/16.
 */
public class ReaderLayerMenuItem extends ReaderMenuItem {
    private int titleResourceId;
    private String title;
    private int drawableResourceId;
    private boolean visible = true;

    public ReaderLayerMenuItem(ItemType itemType, ReaderMenuAction action, ReaderLayerMenuItem parent, int titleResourceId, String title, int drawableResourceId) {
        super(itemType, action, parent);
        this.titleResourceId = titleResourceId;
        this.title = title;
        this.drawableResourceId = drawableResourceId;
    }

    public ReaderLayerMenuItem(ItemType itemType, ReaderMenuAction action, ReaderLayerMenuItem parent, int titleResourceId, String title, int drawableResourceId, int itemId) {
        super(itemType, action, parent);
        this.titleResourceId = titleResourceId;
        this.title = title;
        this.drawableResourceId = drawableResourceId;
        setItemId(itemId);
    }

    public ReaderLayerMenuItem(final ReaderLayerMenuItem menu) {
        super(menu.getItemType(), menu.getAction(), menu.getParent());
        titleResourceId = menu.getTitleResourceId();
        title = menu.getTitle();
        drawableResourceId = menu.getDrawableResourceId();
    }

    public static ReaderMenuItem createSimpleMenuItem(ReaderMenuAction action) {
        return new ReaderLayerMenuItem(ReaderMenuItem.ItemType.Item, action, null, -1, null, -1);
    }

    public static ReaderMenuItem createSimpleMenuItem(ReaderMenuAction action, int drawableResourceId) {
        return new ReaderLayerMenuItem(ItemType.Group, action, null, -1, null, drawableResourceId);
    }

    public static ReaderMenuItem createSimpleMenuItem(ReaderMenuAction action, int drawableResourceId, int titleResourceId) {
        return new ReaderLayerMenuItem(ItemType.Group, action, null, titleResourceId, null, drawableResourceId);
    }

    /**
     * get resource name of resource id in the form "R.drawable.xxx"
     * @param resourceId
     * @return
     */
    private static String getNameOfResource(String resourceId) {
        int idx = resourceId.lastIndexOf('.');
        return resourceId.substring(idx + 1);
    }

    @Override
    public List<ReaderLayerMenuItem> getChildren() {
        return (List<ReaderLayerMenuItem>)super.getChildren();
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public String getTitle() {
        return title;
    }

    public int getDrawableResourceId() {
        return drawableResourceId;
    }

    public void setDrawableResourceId(int drawableResourceId) {
        this.drawableResourceId = drawableResourceId;
    }

    public void setTitleResourceId(int titleResourceId) {
        this.titleResourceId = titleResourceId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
