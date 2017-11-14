package com.onyx.android.plato.model;

import com.onyx.android.plato.interfaces.ForFreeItem;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by li on 2017/11/2.
 */

public class TreeModel<T> implements Serializable {
    private NodeModel<T> rootNode;
    private ForFreeItem<NodeModel<T>> forFreeItem;

    public void addForFreeItem(ForFreeItem<NodeModel<T>> forFreeItem) {
        this.forFreeItem = forFreeItem;
    }

    public TreeModel(NodeModel<T> root) {
        this.rootNode = root;
    }

    public void addNode(NodeModel<T> parent, NodeModel<T>... nodes) {
        int index = 1;
        if (parent.getParentNode() != null) {
            index = parent.getParentNode().getFloor();
        }

        LinkedList<NodeModel<T>> childNodes = parent.getChildNodes();
        for (NodeModel<T> node : nodes) {
            node.setFloor(index);
            node.setParentNode(parent);

            boolean exist = false;
            for (NodeModel<T> t : childNodes) {
                if (node == t) {
                    exist = true;
                    continue;
                }
            }

            if (!exist) {
                childNodes.add(node);
            }
        }
    }

    public boolean removeNode(NodeModel<T> parent, NodeModel<T> deleteNode) {
        boolean rm;
        LinkedList<NodeModel<T>> childNodes = parent.getChildNodes();
        if (childNodes == null || childNodes.size() == 0) {
            rm = false;
        }
        rm = childNodes.remove(deleteNode);
        return rm;
    }

    public NodeModel<T> getRootNode() {
        return rootNode;
    }

    public void setRootNode(NodeModel<T> rootNode) {
        this.rootNode = rootNode;
    }

    private NodeModel<T> getLowNode(NodeModel<T> nodeModel) {
        NodeModel<T> find = null;
        NodeModel<T> parentNode = nodeModel.getParentNode();
        if(parentNode != null && parentNode.getChildNodes().size() > 1) {
            ArrayDeque<NodeModel<T>> queue = new ArrayDeque<>();
            LinkedList<NodeModel<T>> childNodes = parentNode.getChildNodes();
            queue.add(parentNode);
            boolean low = false;
            while (!queue.isEmpty()) {
                NodeModel<T> rootNode = queue.poll();
                if(low) {
                    if(nodeModel.getFloor() == rootNode.getFloor()) {
                        find = rootNode;
                    }
                    break;
                }

                if (nodeModel == rootNode) {
                    low = true;
                }
                if(childNodes.size() > 0) {
                    for (NodeModel<T> node : childNodes){
                        queue.add(node);
                    }
                }
            }
        }
        return find;
    }

    private NodeModel<T> getUpNode(NodeModel<T> nodeModel) {
        NodeModel<T> find = null;
        NodeModel<T> parentNode = nodeModel.getParentNode();
        if(parentNode != null && parentNode.getChildNodes().size() > 1) {
            ArrayDeque<NodeModel<T>> queue = new ArrayDeque<>();
            queue.add(parentNode);
            boolean up = false;
            while (!queue.isEmpty()) {
                NodeModel<T> poll = queue.poll();
                if(poll == nodeModel && poll.getFloor() == nodeModel.getFloor()) {
                    break;
                }

                find = poll;
                LinkedList<NodeModel<T>> childNodes = parentNode.getChildNodes();
                if(childNodes.size() > 0) {
                    for (NodeModel<T> node : childNodes) {
                        queue.add(node);
                    }
                }
            }
        }
        return find;
    }

    public ArrayList<NodeModel<T>> getAllLowNodes(NodeModel<T> addNode) {
        ArrayList<NodeModel<T>> array = new ArrayList<>();
        NodeModel<T> parentNode = addNode.getParentNode();
        while (parentNode != null) {
            NodeModel<T> lowNode = getLowNode(parentNode);
            while (lowNode != null) {
                array.add(lowNode);
                lowNode = getLowNode(lowNode);
            }
            parentNode = parentNode.getParentNode();
        }
        return array;
    }

    public ArrayList<NodeModel<T>> getAllPreNodes(NodeModel<T> addNode) {
        ArrayList<NodeModel<T>> array = new ArrayList<>();
        NodeModel<T> parentNode = addNode.getParentNode();
        while (parentNode != null) {
            NodeModel<T> lowNode = getUpNode(parentNode);
            while (lowNode != null) {
                array.add(lowNode);
                lowNode = getUpNode(lowNode);
            }
            parentNode = parentNode.getParentNode();
        }
        return array;
    }
}
