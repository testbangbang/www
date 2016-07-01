package com.onyx.android.sdk.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joy on 2016/4/19.
 */
public abstract class ReaderMenuItem implements Comparable<ReaderMenuItem> {
    public enum ItemType { Group, Item }

    private ItemType itemType;
    private URI uri;
    private ReaderMenuItem parent;
    private List<ReaderMenuItem> children = new ArrayList<>();

    public ReaderMenuItem(ItemType itemType, URI uri, ReaderMenuItem parent) {
        this.itemType = itemType;
        this.uri = uri;
        this.parent = parent;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public URI getURI() {
        return uri;
    }

    public ReaderMenuItem getParent() {
        return parent;
    }

    public List<? extends ReaderMenuItem> getChildren() {
        return children;
    }

    @Override
    public int compareTo(ReaderMenuItem another) {
        return uri.getRawPath().compareTo(another.getURI().getRawPath());
    }
}
