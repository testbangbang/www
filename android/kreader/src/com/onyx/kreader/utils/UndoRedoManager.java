package com.onyx.kreader.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/21/16.
 *
 * a b c d e  |  g h i j k
 *   back     p   forward
 */
public class UndoRedoManager {

    private String current;
    private List<String> back = new ArrayList<String>();
    private List<String> forward = new ArrayList<String>();

    /**
     * move back, undo, move from back to forward list.
     * @return
     */
    public String undo() {
        if (back.isEmpty()) {
            return null;
        }
        final String value = back.remove(back.size() - 1);
        forward.add(value);
        return value;
    }

    /**
     * move forward, redo. move from forward to back
     * @return
     */
    public String redo() {
        if (forward.isEmpty()) {
            return null;
        }
        final String value = forward.remove(forward.size() - 1);
        back.add(value);
        return value;
    }

    /**
     * add to back list, so you can undo
     * @param item
     * @param allowDuplicate allow duplicated item or not.
     * @return
     */
    public boolean addToHistory(final String item, boolean allowDuplicate) {
        if (!allowDuplicate && StringUtils.isNonBlank(current) && current.equalsIgnoreCase(item)) {
            return false;
        }
        current = item;
        back.add(item);
        forward.clear();
        return true;
    }

}
