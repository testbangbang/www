package com.onyx.kreader.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/21/16.
 *
 * a1 a2 a3 a4 a5  |  a6 a7 a8 a9 aa
 *     back        p     forward
 */
public class HistoryManager {

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
     * alias to undo
     * @return
     */
    public String backward() {
        return undo();
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
     * alias to redo
     * @return
     */
    public String forward() {
        return redo();
    }

    /**
     * add to back list, so you can undo
     * @param item
     * @param allowDuplicate allow duplicated item or not.
     * @return
     */
    public boolean addToHistory(final String item, boolean allowDuplicate) {
        if (!allowDuplicate && StringUtils.isNotBlank(current) && current.equalsIgnoreCase(item)) {
            return false;
        }
        current = item;
        back.add(item);
        forward.clear();
        return true;
    }

    public boolean canGoBack() {
        return back.isEmpty();
    }

    public boolean canGoForward() {
        return forward.isEmpty();
    }

}
