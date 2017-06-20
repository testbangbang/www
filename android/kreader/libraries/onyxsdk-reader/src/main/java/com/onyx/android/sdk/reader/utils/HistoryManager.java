package com.onyx.android.sdk.reader.utils;

import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/21/16.
 *
 * a1 a2 a3 a4 a5  |  a6 a7 a8 a9 aa
 *     back     current    forward
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
        add(forward, current);
        current = back.remove(back.size() - 1);
        return current;
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
        add(back, current);
        current = forward.remove(forward.size() - 1);
        return current;
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
        if (StringUtils.isNullOrEmpty(item)) {
            return false;
        }
        if (!allowDuplicate && StringUtils.isNotBlank(current) && current.equalsIgnoreCase(item)) {
            return false;
        }
        add(back, current);
        current = item;
        forward.clear();
        return true;
    }

    public String getCurrent() {
        return current;
    }

    public boolean canGoBack() {
        return !back.isEmpty();
    }

    public boolean canGoForward() {
        return !forward.isEmpty();
    }

    private boolean add(final List<String> list, final String value) {
        if (StringUtils.isNotBlank(value)) {
            list.add(value);
            return true;
        }
        return false;
    }

}
