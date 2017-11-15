package com.onyx.android.plato.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by li on 2017/11/2.
 */

public class NodeModel<T> implements Serializable {
    private T value;
    private LinkedList<NodeModel<T>> childNodes;
    private boolean focus = false;
    private NodeModel<T> parentNode = null;
    private int floor;

    public NodeModel(T value) {
        this.value = value;
        childNodes = new LinkedList<>();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public LinkedList<NodeModel<T>> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(LinkedList<NodeModel<T>> childNodes) {
        this.childNodes = childNodes;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public NodeModel<T> getParentNode() {
        return parentNode;
    }

    public void setParentNode(NodeModel<T> parentNode) {
        this.parentNode = parentNode;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
